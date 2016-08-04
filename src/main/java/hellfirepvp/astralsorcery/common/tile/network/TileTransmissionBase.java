package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.util.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileEntityTransmissionBase
 * Created by HellFirePvP
 * Date: 03.08.2016 / 10:35
 */
public abstract class TileTransmissionBase extends TileNetwork implements IStarlightTransmission, ILinkableTile {

    private List<BlockPos> positions = new LinkedList<>();

    @Override
    public void update() {
        super.update();
    }

    @Override
    public boolean canConduct(@Nonnull Constellation type) {
        return true;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        positions.clear();

        if(compound.hasKey("linked")) {
            NBTTagList list = compound.getTagList("linked", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tag = list.getCompoundTagAt(i);
                positions.add(NBTUtils.readBlockPosFromNBT(tag));
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        NBTTagList list = new NBTTagList();
        for (BlockPos pos : positions) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(pos, tag);
            list.appendTag(tag);
        }
        compound.setTag("linked", list);
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
        TransmissionNetworkHelper.createTransmissionLink(this, other);
    }

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        return TransmissionNetworkHelper.canCreateTransmissionLink(this, other);
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        if(TransmissionNetworkHelper.hasTransmissionLink(this, other)) {
            TransmissionNetworkHelper.removeTransmissionLink(this, other);
            return true;
        }
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return positions;
    }
}
