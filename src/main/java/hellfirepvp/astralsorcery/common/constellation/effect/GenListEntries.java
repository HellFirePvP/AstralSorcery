/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import net.minecraft.entity.*;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenListEntries
 * Created by HellFirePvP
 * Date: 08.11.2016 / 22:05
 */
public class GenListEntries {

    public static class PelotrioSpawnListEntry extends CounterListEntry {

        private ResourceLocation entityName;

        public PelotrioSpawnListEntry(BlockPos at) {
            super(at);
        }

        public PelotrioSpawnListEntry(BlockPos at, ResourceLocation entityName) {
            super(at);
            this.entityName = entityName;
        }

        @Nullable
        public static PelotrioSpawnListEntry createEntry(World world, BlockPos pos) {
            Biome b = world.getBiome(pos);
            List<Biome.SpawnListEntry> applicable = new LinkedList<>();
            if(ConstellationSkyHandler.getInstance().isNight(world)) {
                applicable.addAll(b.getSpawnableList(EnumCreatureType.MONSTER));
            } else {
                applicable.addAll(b.getSpawnableList(EnumCreatureType.CREATURE));
            }
            if(applicable.isEmpty()) {
                return null; //Duh.
            }
            Collections.shuffle(applicable);
            Biome.SpawnListEntry entry = applicable.get(world.rand.nextInt(applicable.size()));
            Class<? extends EntityLivingBase> applicableClass = entry.entityClass;
            ResourceLocation key = EntityList.getKey(applicableClass);
            if(key != null && EntityUtils.canEntitySpawnHere(world, pos, key, true)) {
                return new PelotrioSpawnListEntry(pos, key);
            }
            return null;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            this.entityName = new ResourceLocation(nbt.getString("entityName"));
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            nbt.setString("entityName", this.entityName.toString());
        }

        public void spawn(World world) {
            if(entityName != null && EntityUtils.canEntitySpawnHere(world, getPos(), entityName, true)) {
                Entity entity = EntityList.createEntityByIDFromName(entityName, world);
                if(entity != null) {
                    BlockPos at = getPos();
                    entity.setLocationAndAngles(at.getX() + 0.5, at.getY() + 0.5, at.getZ() + 0.5, world.rand.nextFloat() * 360.0F, 0.0F);
                    if(entity instanceof EntityLiving) {
                        if (!ForgeEventFactory.doSpecialSpawn((EntityLiving) entity, world, at.getX() + 0.5F, at.getY() + 0.5F, at.getZ() + 0.5F)) {
                            ((EntityLiving) entity).onInitialSpawn(world.getDifficultyForLocation(at), null);
                        }
                        if(!((EntityLiving) entity).isNotColliding()) {
                            entity.setDead();
                            return;
                        }
                    }
                    world.spawnEntity(entity);
                    world.playEvent(2004, entity.getPosition(), 0);
                    world.playEvent(2004, entity.getPosition(), 0);
                }
            }
        }

    }

    public static class CounterListEntry implements CEffectPositionListGen.CEffectGenListEntry {

        private final BlockPos at;
        public int counter;

        public CounterListEntry(BlockPos at) {
            this.at = at;
        }

        @Override
        public BlockPos getPos() {
            return at;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            counter = nbt.getInteger("counter");
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setInteger("counter", counter);
        }
    }

    public static class CounterMaxListEntry extends CounterListEntry {

        public int maxCount;

        public CounterMaxListEntry(BlockPos at, int maxCount) {
            super(at);
            this.maxCount = maxCount;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            super.readFromNBT(nbt);
            maxCount = nbt.getInteger("maxCounter");
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            super.writeToNBT(nbt);
            nbt.setInteger("maxCounter", maxCount);
        }

    }

    public static class SimpleBlockPosEntry implements CEffectPositionListGen.CEffectGenListEntry {

        private final BlockPos pos;

        public SimpleBlockPosEntry(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

    }

    public static class PosDefinedTuple<K extends NBTBase, V extends NBTBase> implements CEffectPositionListGen.CEffectGenListEntry {

        private final BlockPos pos;
        public K key;
        public V value;

        public PosDefinedTuple(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound nbt) {
            this.key = (K) nbt.getTag("mapKey");
            this.value = (V) nbt.getTag("mapValue");
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {
            nbt.setTag("mapKey", key);
            nbt.setTag("mapValue", value);
        }

    }

}
