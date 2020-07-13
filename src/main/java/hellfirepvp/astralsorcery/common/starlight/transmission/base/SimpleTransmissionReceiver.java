/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:59
 */
public abstract class SimpleTransmissionReceiver implements ITransmissionReceiver {

    private BlockPos thisPos;

    private Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimpleTransmissionReceiver(BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getLocationPos() {
        return thisPos;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos changed) {
        return false;
    }

    @Override
    public List<BlockPos> getSources() {
        return new LinkedList<>(sourcesToThis);
    }

    @Nullable
    public <T extends TileEntity> T getTileAtPos(World world, Class<T> tileClass) {
        return MiscUtils.getTileAt(world, getLocationPos(), tileClass, false);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        this.thisPos = NBTHelper.readBlockPosFromNBT(compound);
        this.sourcesToThis.clear();

        ListNBT list = compound.getList("sources", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            sourcesToThis.add(NBTHelper.readBlockPosFromNBT(list.getCompound(i)));
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        NBTHelper.writeBlockPosToNBT(thisPos, compound);

        ListNBT sources = new ListNBT();
        for (BlockPos source : sourcesToThis) {
            CompoundNBT comp = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(source, comp);
            sources.add(comp);
        }
        compound.put("sources", sources);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTransmissionReceiver that = (SimpleTransmissionReceiver) o;
        return Objects.equals(thisPos, that.thisPos);
    }

}
