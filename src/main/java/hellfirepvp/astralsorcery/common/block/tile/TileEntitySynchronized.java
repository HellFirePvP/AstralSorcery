package hellfirepvp.astralsorcery.common.block.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntitySynchronized
 * Created by HellFirePvP
 * Date: 11.05.2016 / 18:17
 */
public class TileEntitySynchronized extends TileEntity {

    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
    }

    public void readCustomNBT(NBTTagCompound compound) {
    }

    public final void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeCustomNBT(compound);
    }

    public void writeCustomNBT(NBTTagCompound compound) {
    }

    public final Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeCustomNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), 255, compound);
    }

    public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity paket) {
        super.onDataPacket(manager, paket);
        readCustomNBT(paket.getNbtCompound());
    }

}
