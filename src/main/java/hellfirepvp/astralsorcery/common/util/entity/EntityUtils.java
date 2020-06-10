/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.entity;

import com.google.common.base.Predicate;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.*;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:26
 */
public class EntityUtils {

    private static final Random rand = new Random();

    public static void applyVortexMotion(Supplier<Vector3> positionSupplier, Consumer<Vector3> addMotion, Vector3 to, double vortexRange, double multiplier) {
        Vector3 pos = positionSupplier.get();
        double diffX = (to.getX() - pos.getX()) / vortexRange;
        double diffY = (to.getY() - pos.getY()) / vortexRange;
        double diffZ = (to.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            addMotion.accept(toAdd);
        }
    }

    @Nullable
    public static LivingEntity performWorldSpawningAt(ServerWorld world, BlockPos pos, EntityClassification category, SpawnReason reason, boolean ignoreWeighting) {
        List<Biome.SpawnListEntry> spawnList = world.getChunkProvider().getChunkGenerator().getPossibleCreatures(EntityClassification.MONSTER, pos);
        spawnList = ForgeEventFactory.getPotentialSpawns(world, category, pos, spawnList);
        spawnList.removeIf(s -> !s.entityType.isSummonable());
        Biome.SpawnListEntry entry;
        if (ignoreWeighting) {
            entry = MiscUtils.getRandomEntry(spawnList, rand);
        } else {
            entry = MiscUtils.getWeightedRandomEntry(spawnList, rand, ee -> ee.itemWeight);
        }

        if (entry != null && WorldEntitySpawner.isSpawnableSpace(world, pos, world.getBlockState(pos), world.getFluidState(pos))) {

            float x = pos.getX() + 0.5F;
            float y = pos.getY();
            float z = pos.getZ() + 0.5F;

            if (isSpawnableAt(world, pos)) {

                MobEntity entity;
                try {
                    entity = (MobEntity) entry.entityType.create(world);
                } catch (Exception exception) {
                    return null;
                }
                if (entity == null) {
                    return null;
                }

                entity.setLocationAndAngles(x, y, z, rand.nextFloat() * 360F, 0F);
                int result = ForgeHooks.canEntitySpawn(entity, world, x, y, z, null, reason);
                if (result == -1) {
                    return null;
                }

                if (!ForgeEventFactory.doSpecialSpawn(entity, world, x, y, z, null, reason)) {
                    entity.onInitialSpawn(world, world.getDifficultyForLocation(pos), reason, null, null);
                }

                if (world.addEntity(entity)) {
                    return entity;
                }
            }
        }
        return null;
    }

    private static boolean isSpawnableAt(World world, BlockPos pos) {
        BlockPos up = pos.up();
        return WorldEntitySpawner.isSpawnableSpace(world, pos, world.getBlockState(pos), world.getFluidState(pos)) &&
                WorldEntitySpawner.isSpawnableSpace(world, up, world.getBlockState(up), world.getFluidState(up));
    }

    public static boolean canEntitySpawnHere(World world, BlockPos at, EntityType<? extends Entity> type, SpawnReason spawnReason, @Nullable Consumer<Entity> preCheckEntity) {
        EntitySpawnPlacementRegistry.PlacementType placementType = EntitySpawnPlacementRegistry.getPlacementType(type);
        if (!world.getWorldBorder().contains(at) || !placementType.canSpawnAt(world, at, type)) {
            return false;
        }
        if (!EntitySpawnPlacementRegistry.func_223515_a(type, world, spawnReason, at, world.rand)) {
            return false;
        }
        if (world.hasNoCollisions(type.func_220328_a(at.getX() + 0.5, at.getY(), at.getZ() + 0.5))) {
            return false;
        }

        Entity entity = type.create(world);
        if (entity == null) {
            return false;
        }
        entity.setLocationAndAngles(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
        if (preCheckEntity != null) {
            preCheckEntity.accept(entity);
        }

        if (entity instanceof LivingEntity) {
            if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;
                Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(mobEntity, world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), null, spawnReason);
                if (canSpawn == Event.Result.DENY) {
                    return false;
                } else if (canSpawn == Event.Result.DEFAULT) {
                    if (!mobEntity.canSpawn(world, spawnReason) || !mobEntity.isNotColliding(world)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Nonnull
    public static List<ItemStack> generateLoot(LivingEntity entity, Random rand, DamageSource srcDeath, @Nullable LivingEntity lastAttacker) {
        MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        ServerWorld sw = (ServerWorld) entity.getEntityWorld();

        if (!sw.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            return Collections.emptyList();
        }

        ResourceLocation lootTableKey = entity.getLootTableResourceLocation();
        LootTable table = srv.getLootTableManager().getLootTableFromLocation(lootTableKey);
        LootContext.Builder builder = new LootContext.Builder(sw)
                .withRandom(rand)
                .withParameter(LootParameters.THIS_ENTITY, entity)
                .withParameter(LootParameters.POSITION, new BlockPos(entity))
                .withParameter(LootParameters.DAMAGE_SOURCE, srcDeath)
                .withNullableParameter(LootParameters.KILLER_ENTITY, srcDeath.getTrueSource())
                .withNullableParameter(LootParameters.DIRECT_KILLER_ENTITY, srcDeath.getImmediateSource());
        if (lastAttacker != null) {
            if (lastAttacker instanceof PlayerEntity) {
                builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, (PlayerEntity) lastAttacker)
                        .withLuck(((PlayerEntity) lastAttacker).getLuck());
            }
        }

        return table.generate(builder.build(LootParameterSets.ENTITY));
    }

    @Nullable
    public static <T extends Entity> T getClosestEntity(IWorld world, Class<T> type, AxisAlignedBB box, Vector3 closestTo) {
        List<T> entities = world.getEntitiesWithinAABB(type, box, Entity::isAlive);
        return selectClosest(entities, closestTo::distanceSquared);
    }

    public static Predicate<? super Entity> selectEntities(Class<? extends Entity>... entities) {
        return (Predicate<Entity>) entity -> {
            if (entity == null || !entity.isAlive()) return false;
            Class<? extends Entity> clazz = entity.getClass();
            for (Class<? extends Entity> test : entities) {
                if (test.isAssignableFrom(clazz)) return true;
            }
            return false;
        };
    }

    public static Predicate<? super Entity> selectItemClassInstaceof(Class<?> itemClass) {
        return (Predicate<Entity>) entity -> {
            if (entity == null || !entity.isAlive()) return false;
            if (!(entity instanceof ItemEntity)) return false;
            ItemStack i = ((ItemEntity) entity).getItem();
            if (i.isEmpty()) return false;
            return itemClass.isAssignableFrom(i.getItem().getClass());
        };
    }

    public static Predicate<? super Entity> selectItem(Item item) {
        return (Predicate<Entity>) entity -> {
            if (entity == null || !entity.isAlive()) return false;
            if (!(entity instanceof ItemEntity)) return false;
            ItemStack i = ((ItemEntity) entity).getItem();
            if (i.isEmpty()) return false;
            return i.getItem().equals(item);
        };
    }

    public static Predicate<? super Entity> selectItemStack(Function<ItemStack, Boolean> acceptor) {
        return entity -> {
            if (entity == null || !entity.isAlive()) return false;
            if (!(entity instanceof ItemEntity)) return false;
            ItemStack i = ((ItemEntity) entity).getItem();
            if (i.isEmpty()) return false;
            return acceptor.apply(i);
        };
    }

    @Nullable
    public static <T> T selectClosest(Collection<T> elements, Function<T, Double> dstFunc) {
        if (elements.isEmpty()) return null;

        double dstClosest = Double.MAX_VALUE;
        T closestElement = null;
        for (T element : elements) {
            double dst = dstFunc.apply(element);
            if (dst < dstClosest) {
                closestElement = element;
                dstClosest = dst;
            }
        }
        return closestElement;
    }

}
