/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world.data;

import hellfirepvp.astralsorcery.common.data.world.CachedWorldData;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureGenBuffer
 * Created by HellFirePvP
 * Date: 17.04.2017 / 09:06
 */
public class StructureGenBuffer extends CachedWorldData {

    private Map<StructureType, List<BlockPos>> generatedStructures = new HashMap<>();

    public StructureGenBuffer() {
        super(WorldCacheManager.SaveKey.STRUCTURE_GEN);
        for (StructureType type : StructureType.values()) {
            generatedStructures.put(type, new LinkedList<>());
        }
    }

    public void markStructureGeneration(BlockPos pos, StructureType type) {
        generatedStructures.get(type).add(pos);
        markDirty();
    }

    public double getDstToClosest(StructureType type, double idealDistance, BlockPos dstTo) {
        double closest = Double.MAX_VALUE;
        double halfDst = idealDistance / 2.0D;
        int x = dstTo.getX();
        int y = dstTo.getY();
        int z = dstTo.getZ();

        if (type.needsDistanceToAnyStructure()) {
            for (StructureType tt : StructureType.values()) {
                if (!tt.needsDistanceToAnyStructure() ||
                        tt.equals(type)) {
                    continue;
                }
                for (BlockPos position : generatedStructures.get(type)) {
                    double dst = position.getDistance(x, y, z);
                    if (dst <= halfDst) {
                        return dst; //Fast fail on close structures
                    }
                }
            }
        }

        for (BlockPos position : generatedStructures.get(type)) {
            double dst = position.getDistance(x, y, z);
            if(dst < closest) {
                closest = dst;
            }
        }
        return closest;
    }

    @Nullable
    public BlockPos getClosest(StructureType type, BlockPos dstTo) {
        double closest = Double.MAX_VALUE;
        BlockPos closestPos = null;
        int x = dstTo.getX();
        int y = dstTo.getY();
        int z = dstTo.getZ();
        for (BlockPos position : generatedStructures.get(type)) {
            double dst = position.getDistance(x, y, z);
            if(dst < closest) {
                closest = dst;
                closestPos = position;
            }
        }
        return closestPos;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        for (StructureType type : StructureType.values()) {
            generatedStructures.get(type).clear();
        }

        for (StructureType type : StructureType.values()) {
            NBTTagList list = compound.getTagList(type.name().toLowerCase(), Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound cmp = list.getCompoundTagAt(i);
                BlockPos pos = NBTHelper.readBlockPosFromNBT(cmp);
                generatedStructures.get(type).add(pos);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        for (StructureType type : StructureType.values()) {
            NBTTagList list = new NBTTagList();
            for (BlockPos pos : generatedStructures.get(type)) {
                NBTTagCompound tag = new NBTTagCompound();
                NBTHelper.writeBlockPosToNBT(pos, tag);
                list.appendTag(tag);
            }
            compound.setTag(type.name().toLowerCase(), list);
        }
    }

    @Override
    public void updateTick(World world) {}

    public static enum StructureType {

        MOUNTAIN(true),
        DESERT(true),
        SMALL(true),
        TREASURE,
        SMALL_RUIN(true);

        private final boolean averageDistanceRequired;

        StructureType() {
            this(false);
        }

        StructureType(boolean averageDistanceRequired) {
            this.averageDistanceRequired = averageDistanceRequired;
        }

        public boolean needsDistanceToAnyStructure() {
            return this.averageDistanceRequired;
        }
    }

}
