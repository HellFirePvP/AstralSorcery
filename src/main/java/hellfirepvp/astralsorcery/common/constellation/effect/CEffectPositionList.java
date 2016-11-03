package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectPositionList
 * Created by HellFirePvP
 * Date: 17.10.2016 / 09:33
 */
public abstract class CEffectPositionList extends ConstellationEffect {

    protected List<BlockPos> positions = new LinkedList<>();
    private final int searchRange, maxCount;
    protected final Verifier verifier;

    public CEffectPositionList(Constellation c, int searchRange, int maxCount, Verifier verifier) {
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
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(pos)) && verifier.isValid(world, at) && !positions.contains(at)) {
                positions.add(at);
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
            positions.add(NBTUtils.readBlockPosFromNBT(tag));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound cmp) {
        NBTTagList listPositions = new NBTTagList();
        for (BlockPos pos : positions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, tag);
            listPositions.appendTag(tag);
        }
        cmp.setTag("positions", listPositions);
    }

    public static interface Verifier {

        public boolean isValid(World world, BlockPos pos);

    }

}
