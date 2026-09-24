/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EntitySpawnUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EntitySpawnUtil {

    @Nullable
    public static LivingEntity performNaturalSpawnAt(ServerLevel sLevel, BlockPos pos, boolean ignoreListWeighting, int conditionFlags) {
        return performNaturalSpawnAt(sLevel, pos, ignoreListWeighting, MobCategory.MONSTER, conditionFlags);
    }

    @Nullable
    public static LivingEntity performNaturalSpawnAt(ServerLevel sLevel, BlockPos pos, boolean ignoreListWeighting, MobCategory category, int conditionFlags) {
        WeightedRandomList<MobSpawnSettings.SpawnerData> spawnWeights = getSpawnLists(sLevel, pos, category);

        List<MobSpawnSettings.SpawnerData> temp = new ArrayList<>(spawnWeights.unwrap());
        temp.removeIf(data -> !data.type.canSummon());
        spawnWeights = WeightedRandomList.create(temp);


        MobSpawnSettings.SpawnerData spawnerData;
        if (ignoreListWeighting) {
            spawnerData = MiscUtil.getRandomEntry(spawnWeights.unwrap(), sLevel.random).orElse(null);
        } else {
            spawnerData = spawnWeights.getRandom(sLevel.random).orElse(null);
        }
        if (spawnerData == null) return null;
        if (!canEntityNaturallySpawnHere(sLevel, pos, spawnerData.type, MobSpawnType.NATURAL, conditionFlags)) return null;

        float posX = pos.getX() + 0.5F;
        float posY = pos.getY();
        float posZ = pos.getZ() + 0.5F;

        LivingEntity e;
        try {
            e = (LivingEntity) spawnerData.type.create(sLevel);
        } catch (Exception exc) {
            return null;
        }
        if (e == null) return null;

        e.moveTo(posX, posY, posZ, sLevel.random.nextFloat() * 360F, 0F);
        if (e instanceof Mob mob) {
            EventHooks.finalizeMobSpawn(mob, sLevel, sLevel.getCurrentDifficultyAt(pos), MobSpawnType.NATURAL, null);
        }

        sLevel.addFreshEntityWithPassengers(e);
        return e;
    }

    public static <T extends Entity> boolean canEntityNaturallySpawnHere(ServerLevel sLevel,
                                                                         BlockPos pos,
                                                                         EntityType<T> type,
                                                                         MobSpawnType spawnReason,
                                                                         int conditionFlags) {
        return canEntityNaturallySpawnHere(sLevel, pos, type, spawnReason, conditionFlags, e -> {});
    }

    public static <T extends Entity> boolean canEntityNaturallySpawnHere(ServerLevel sLevel,
                                                                         BlockPos pos,
                                                                         EntityType<T> type,
                                                                         MobSpawnType spawnReason,
                                                                         int conditionFlags,
                                                                         Consumer<T> preCheckFn) {
        MobCategory category = type.getCategory();
        if (category == MobCategory.MISC || !type.canSummon()) {
            return false;
        }
        if (!SpawnConditionFlags.isSet(conditionFlags, SpawnConditionFlags.IGNORE_MOB_SPAWN_LISTS)) {
            if (getSpawnLists(sLevel, pos, category).unwrap().stream()
                    .map(data -> data.type)
                    .noneMatch(spawnedType -> spawnedType == type)) {
                return false;
            }
        }
        if (!SpawnConditionFlags.isSet(conditionFlags, SpawnConditionFlags.IGNORE_PLACEMENT_TYPES)) {
            if (!SpawnPlacements.isSpawnPositionOk(type, sLevel, pos)) {
                return false;
            }
            if (!SpawnPlacements.checkSpawnRules(type, sLevel, spawnReason, pos, sLevel.random)) {
                return false;
            }
        }
        if (!SpawnConditionFlags.isSet(conditionFlags, SpawnConditionFlags.IGNORE_BLOCK_COLLISION)) {
            if (!EntityUtil.canEntityFit(sLevel, type, pos)) {
                return false;
            }
        }

        T e;
        try {
            e = type.create(sLevel);
        } catch (Exception exc) {
            return false;
        }
        if (e == null) return false;
        preCheckFn.accept(e);
        e.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, sLevel.random.nextFloat() * 360F, 0F);
        if (e instanceof Mob mob) {
            MobSpawnEvent.PositionCheck check = new MobSpawnEvent.PositionCheck(mob, sLevel, spawnReason, null);
            NeoForge.EVENT_BUS.post(check);
            if (check.getResult() == MobSpawnEvent.PositionCheck.Result.FAIL) {
                return false;
            }
            if (check.getResult() == MobSpawnEvent.PositionCheck.Result.DEFAULT) {
                if (!SpawnConditionFlags.isSet(conditionFlags, SpawnConditionFlags.IGNORE_ENTITY_SPAWN_PLACEMENT)) {
                    if (!mob.checkSpawnRules(sLevel, spawnReason)) {
                        return false;
                    }
                }
                if (!SpawnConditionFlags.isSet(conditionFlags, SpawnConditionFlags.IGNORE_ENTITY_SPAWN_COLLISION)) {
                    if (!mob.checkSpawnObstruction(sLevel)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static WeightedRandomList<MobSpawnSettings.SpawnerData> getSpawnLists(ServerLevel sLevel, BlockPos pos, MobCategory category) {
        return getSpawnLists(sLevel, pos, category, sLevel.structureManager(), sLevel.getChunkSource().getGenerator());
    }

    private static WeightedRandomList<MobSpawnSettings.SpawnerData> getSpawnLists(ServerLevel sLevel, BlockPos pos, MobCategory category, StructureManager mgr, ChunkGenerator gen) {
        WeightedRandomList<MobSpawnSettings.SpawnerData> spawnOptions = null;
        if (NaturalSpawner.isInNetherFortressBounds(pos, sLevel, category, mgr)) {
            StructureSpawnOverride ovr = mgr.registryAccess().registryOrThrow(Registries.STRUCTURE).getOrThrow(BuiltinStructures.FORTRESS).spawnOverrides().get(category);
            if (ovr != null) {
                spawnOptions = ovr.spawns();
            }
        }
        if (spawnOptions == null) {
            spawnOptions = gen.getMobsAt(sLevel.getBiome(pos), mgr, category, pos);
        }
        return EventHooks.getPotentialSpawns(sLevel, category, pos, spawnOptions);
    }

    public static class SpawnConditionFlags {

        public static final int IGNORE_MOB_SPAWN_LISTS        = 0b00001; //checks if the entity is in the position's spawn lists
        public static final int IGNORE_PLACEMENT_TYPES        = 0b00010; //checks if this entity type's placement type is valid for breakPos
        public static final int IGNORE_BLOCK_COLLISION        = 0b00100; //checks block collision in world
        public static final int IGNORE_ENTITY_SPAWN_PLACEMENT = 0b01000; //IDK, checks something with pathfinding
        public static final int IGNORE_ENTITY_SPAWN_COLLISION = 0b10000; //checks only fluids & entity collision

        public static final int C_IGNORE_COLLISIONS = IGNORE_BLOCK_COLLISION | IGNORE_ENTITY_SPAWN_COLLISION;
        public static final int C_IGNORE_SPAWN_RULES = IGNORE_MOB_SPAWN_LISTS | IGNORE_PLACEMENT_TYPES | IGNORE_ENTITY_SPAWN_PLACEMENT;

        public static boolean isSet(int flags, int flag) {
            return (flags & flag) != 0;
        }

    }
}
