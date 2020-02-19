/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.base;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.constellation.world.DayTimeHelper;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ListEntries
 * Created by HellFirePvP
 * Date: 11.06.2019 / 20:18
 */
public class ListEntries {

    public static class EntitySpawnEntry extends CounterEntry {

        private EntityType<?> type;

        public EntitySpawnEntry(BlockPos pos) {
            super(pos);
        }

        public EntitySpawnEntry(BlockPos pos, EntityType<?> type) {
            super(pos);
            this.type = type;
        }

        @Override
        public void readFromNBT(CompoundNBT nbt) {
            super.readFromNBT(nbt);

            this.type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("entity")));
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            super.writeToNBT(nbt);

            nbt.putString("entity", this.type.getRegistryName().toString());
        }

        public static EntitySpawnEntry createEntry(World world, BlockPos pos, SpawnReason reason) {
            Biome b = world.getBiome(pos);
            List<Biome.SpawnListEntry> applicable = new LinkedList<>();
            if (DayTimeHelper.isNight(world)) {
                applicable.addAll(b.getSpawns(EntityClassification.MONSTER));
            } else {
                applicable.addAll(b.getSpawns(EntityClassification.CREATURE));
            }
            if (applicable.isEmpty()) {
                return null; //Duh.
            }
            Collections.shuffle(applicable);
            Biome.SpawnListEntry entry = applicable.get(world.rand.nextInt(applicable.size()));
            EntityType<?> type = entry.entityType;
            if (type != null && EntityUtils.canEntitySpawnHere(world, pos, type, reason,
                    (e) -> e.addTag(ConstellationEffectRegistry.ENTITY_TAG_LUCERNA_SKIP_ENTITY))) {
                return new EntitySpawnEntry(pos, type);
            }
            return null;
        }

        public void spawn(World world, SpawnReason reason) {
            if (this.type == null) {
                return;
            }

            Entity e = this.type.create(world);
            if (e != null) {
                BlockPos at = getPos();
                e.setLocationAndAngles(
                        at.getX() + 0.5,
                        at.getY() + 0.5,
                        at.getZ() + 0.5,
                        world.rand.nextFloat() * 360.0F, 0.0F);
                if (e instanceof MobEntity) {
                    ((MobEntity) e).onInitialSpawn(world, world.getDifficultyForLocation(at), reason, null, null);
                    if (!((MobEntity) e).isNotColliding(world)) {
                        e.remove();
                        return;
                    }
                }
                e.addTag(ConstellationEffectRegistry.ENTITY_TAG_LUCERNA_SKIP_ENTITY);
                world.addEntity(e);
                world.playEvent(2004, e.getPosition(), 0);
                world.playEvent(2004, e.getPosition(), 0);
            }
        }
    }

    public static class CounterMaxEntry extends CounterEntry {

        private int maxCount;

        public CounterMaxEntry(BlockPos pos) {
            super(pos);
        }

        public CounterMaxEntry(BlockPos pos, int maxCount) {
            super(pos);
            this.maxCount = maxCount;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            super.writeToNBT(nbt);

            nbt.putInt("maxCount", this.maxCount);
        }

        @Override
        public void readFromNBT(CompoundNBT nbt) {
            super.readFromNBT(nbt);

            this.maxCount = nbt.getInt("maxCount");
        }
    }

    public static class CounterEntry extends PosEntry {

        private int counter = 0;

        public CounterEntry(BlockPos pos) {
            super(pos);
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {
            super.writeToNBT(nbt);

            nbt.putInt("counter", this.counter);
        }

        @Override
        public void readFromNBT(CompoundNBT nbt) {
            super.readFromNBT(nbt);

            this.counter = nbt.getInt("counter");
        }
    }

    public static class PosEntry implements CEffectAbstractList.ListEntry {

        private final BlockPos pos;

        public PosEntry(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getPos() {
            return this.pos;
        }

        @Override
        public void writeToNBT(CompoundNBT nbt) {}

        @Override
        public void readFromNBT(CompoundNBT nbt) {}

    }
}
