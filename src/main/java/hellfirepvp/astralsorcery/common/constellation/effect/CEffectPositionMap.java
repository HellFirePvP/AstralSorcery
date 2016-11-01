package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionMap
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:24
 */
public abstract class CEffectPositionMap<T extends CEffectPositionMap.PositionEntry> extends ConstellationEffect {

    protected HashMap<BlockPos, T> positions = new HashMap<>();
    private final int searchRange, maxCount;
    private final Verifier verifier;

    public CEffectPositionMap(Constellation c, int searchRange, int maxCount, Verifier verifier) {
        super(c);
        this.searchRange = searchRange;
        this.maxCount = maxCount;
        this.verifier = verifier;
    }

    public boolean doRandomOnPositions(World world) {
        return positions.size() > 0 && world.rand.nextInt((maxCount - positions.size()) / 4 + 1) == 0;
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(maxCount > positions.size()) {
            int offX = -searchRange + world.rand.nextInt(searchRange * 2);
            int offY = -searchRange + world.rand.nextInt(searchRange * 2);
            int offZ = -searchRange + world.rand.nextInt(searchRange * 2);
            BlockPos at = pos.add(offX, offY, offZ);
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(pos)) && verifier.isValid(world, at) && !positions.containsKey(at)) {
                positions.put(at, provideNewPositionEntry(at));
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(NBTTagCompound cmp) {
        positions.clear();
        NBTTagList list = cmp.getTagList("positions", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            BlockPos t = NBTUtils.readBlockPosFromNBT(tag);
            T dat = provideNewPositionEntry(t);
            dat.read(tag);
            positions.put(t, dat);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        NBTTagList listPositions = new NBTTagList();
        for (BlockPos pos : positions.keySet()) {
            T t = positions.get(pos);
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, tag);
            t.write(tag);
            listPositions.appendTag(tag);
        }
        cmp.setTag("positions", listPositions);
    }

    public abstract T provideNewPositionEntry(BlockPos pos);

    public static interface PositionEntry {

        public void read(NBTTagCompound tag);

        public void write(NBTTagCompound tag);

    }

    public static class EntryInteger implements PositionEntry {

        public int value = 0;

        @Override
        public void read(NBTTagCompound tag) {
            value = tag.getInteger("value");
        }

        @Override
        public void write(NBTTagCompound tag) {
            tag.setInteger("value", value);
        }

    }

    public static interface Verifier {

        public boolean isValid(World world, BlockPos pos);

    }

}
