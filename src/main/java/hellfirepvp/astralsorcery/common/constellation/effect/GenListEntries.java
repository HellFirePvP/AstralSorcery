package hellfirepvp.astralsorcery.common.constellation.effect;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenListEntries
 * Created by HellFirePvP
 * Date: 08.11.2016 / 22:05
 */
public class GenListEntries {

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
