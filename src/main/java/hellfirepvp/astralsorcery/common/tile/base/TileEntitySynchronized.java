/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.util.block.ILocatable;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:17
 */
public abstract class TileEntitySynchronized extends TileEntity implements ILocatable {

    protected static final Random rand = new Random();
    protected static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    protected TileEntitySynchronized(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public BlockPos getLocationPos() {
        return this.getPos();
    }

    @Override
    public final void read(CompoundNBT compound) {
        super.read(compound);
        readCustomNBT(compound);
        readSaveNBT(compound);
    }

    //Both Network & Chunk-saving
    public void readCustomNBT(CompoundNBT compound) {}

    //Only Network-read
    public void readNetNBT(CompoundNBT compound) {}

    //Only Chunk-read
    public void readSaveNBT(CompoundNBT compound) {}

    @Override
    public final CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        writeCustomNBT(compound);
        writeSaveNBT(compound);
        return compound;
    }

    //Both Network & Chunk-saving
    public void writeCustomNBT(CompoundNBT compound) {}

    //Only Network-write
    public void writeNetNBT(CompoundNBT compound) {}

    //Only Chunk-write
    public void writeSaveNBT(CompoundNBT compound) {}

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT compound = new CompoundNBT();
        super.write(compound);
        writeCustomNBT(compound);
        writeNetNBT(compound);
        return new SUpdateTileEntityPacket(getPos(), 255, compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compound = new CompoundNBT();
        super.write(compound);
        writeCustomNBT(compound);
        return compound;
    }

    public final void onDataPacket(NetworkManager manager, SUpdateTileEntityPacket packet) {
        super.onDataPacket(manager, packet);
        readCustomNBT(packet.getNbtCompound());
        readNetNBT(packet.getNbtCompound());
        this.onDataReceived();
    }

    @OnlyIn(Dist.CLIENT)
    protected void onDataReceived() {}

    public void markForUpdate() {
        if (getWorld() != null) {
            BlockState thisState = this.getBlockState();
            getWorld().notifyBlockUpdate(getPos(), thisState, thisState, 3);
        }
        markDirty();
    }

    public void dropItemOnTop(ItemStack stack) {
        ItemUtils.dropItem(getWorld(), getPos().getX() + 0.5, getPos().getY() + 1.5, getPos().getZ() + 0.5, stack);
    }

}
