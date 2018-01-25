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
import hellfirepvp.astralsorcery.common.tile.TileStorageCore;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetworkBuffer
 * Created by HellFirePvP
 * Date: 13.12.2017 / 21:52
 */
public class StorageNetworkBuffer extends CachedWorldData {

    private List<BlockPos> coreLocations = new LinkedList<>();

    public StorageNetworkBuffer() {
        //super(WorldCacheManager.SaveKey.STORAGE_BUFFER);
        super(null);
    }

    public void add(TileStorageCore core) {
        if(this.coreLocations.add(core.getPos())) {
            markDirty();
        }
    }

    public void remove(TileStorageCore core) {
        if(this.coreLocations.remove(core.getPos())) {
            markDirty();
        }
    }

    @Override
    public void updateTick(World world) {}

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.coreLocations.clear();

        NBTTagList list = compound.getTagList("coreLocations", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            this.coreLocations.add(NBTUtils.readBlockPosFromNBT(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (BlockPos p : this.coreLocations) {
            NBTTagCompound cmp = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(p, cmp);
            list.appendTag(cmp);
        }
        compound.setTag("coreLocations", list);
    }

}
