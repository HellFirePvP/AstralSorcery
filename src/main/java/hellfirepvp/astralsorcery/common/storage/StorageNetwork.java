/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetwork
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:57
 */
public class StorageNetwork {

    private CoreArea master = null;
    private Map<BlockPos, AxisAlignedBB> cores = Maps.newHashMap();

    //True if set.
    public boolean setMaster(@Nullable BlockPos pos) {
        if (pos == null) {
            this.master = null;
            return true;
        }
        if (cores.containsKey(pos)) {
            this.master = new CoreArea(pos, this.cores.get(pos));
            return true;
        }
        return false;
    }

    @Nullable
    public CoreArea getMaster() {
        return master;
    }

    //True if it didn't overwrite a previous one
    public boolean addCore(BlockPos pos, AxisAlignedBB box) {
        return this.cores.put(pos, box) == null;
    }

    //True if it had a position like that
    public boolean removeCore(BlockPos pos) {
        return this.cores.remove(pos) != null;
    }

    public List<CoreArea> getCores() {
        return MapStream.of(this.cores).toList(CoreArea::new);
    }

    public void writeToNBT(CompoundNBT tag) {
        ListNBT list = new ListNBT();
        for (CoreArea coreData : this.getCores()) {
            CompoundNBT coreTag = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(coreData.getPos(), coreTag);
            NBTHelper.writeBoundingBox(coreData.getOffsetBox(), coreTag);
            list.add(coreTag);
        }
        tag.put("cores", list);

        CoreArea master;
        if ((master = getMaster()) != null) {
            NBTHelper.setAsSubTag(tag, "master", nbt -> NBTHelper.writeBlockPosToNBT(master.getPos(), nbt));
        }
    }

    public void readFromNBT(CompoundNBT tag) {
        this.cores.clear();

        ListNBT list = tag.getList("cores", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT coreTag = list.getCompound(i);
            BlockPos pos = NBTHelper.readBlockPosFromNBT(coreTag);
            AxisAlignedBB box = NBTHelper.readBoundingBox(coreTag);
            this.addCore(pos, box);
        }

        this.setMaster(NBTHelper.readFromSubTag(tag, "master", NBTHelper::readBlockPosFromNBT));
    }

    public static class CoreArea {

        private final BlockPos pos;
        private final AxisAlignedBB offsetBox;

        private CoreArea(BlockPos pos, AxisAlignedBB offsetBox) {
            this.pos = pos;
            this.offsetBox = offsetBox;
        }

        public BlockPos getPos() {
            return pos;
        }

        public AxisAlignedBB getOffsetBox() {
            return offsetBox;
        }

        public AxisAlignedBB getRealBox() {
            return offsetBox.offset(getPos());
        }
    }

}