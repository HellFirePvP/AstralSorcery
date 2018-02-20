/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.auxiliary.link.ILinkableTile;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileSourceBase
 * Created by HellFirePvP
 * Date: 03.08.2016 / 17:48
 */
public abstract class TileSourceBase extends TileNetworkSkybound implements IStarlightSource, ILinkableTile {

    protected boolean needsUpdate = false;
    private boolean linked = false;
    private List<BlockPos> positions = new LinkedList<>();

    @Override
    public World getLinkWorld() {
        return getTrWorld();
    }

    @Override
    public BlockPos getLinkPos() {
        return getTrPos();
    }

    @Override
    protected void updateSkyState(boolean seesSky) {
        boolean oldState = doesSeeSky();
        super.updateSkyState(seesSky);
        if(oldState != doesSeeSky()) {
            needsUpdate = true;
        }
    }

    public boolean hasBeenLinked() {
        return linked;
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

        this.linked = compound.getBoolean("wasLinkedBefore");
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
        compound.setBoolean("wasLinkedBefore", linked);
    }

    @Override
    @Nonnull
    public BlockPos getTrPos() {
        return getPos();
    }

    @Override
    @Nonnull
    public World getTrWorld() {
        return getWorld();
    }

    @Override
    public void onLinkCreate(EntityPlayer player, BlockPos other) {
        if(other.equals(getPos())) return;

        if(TransmissionNetworkHelper.createTransmissionLink(this, other)) {
            if(!this.positions.contains(other)) {
                this.positions.add(other);
                markDirty();
            }

            if(!hasBeenLinked()) {
                this.linked = true;
                this.needsUpdate = true;
            }
        }
    }

    @Override
    public boolean tryLink(EntityPlayer player, BlockPos other) {
        return !other.equals(getPos()) && TransmissionNetworkHelper.canCreateTransmissionLink(this, other);
    }

    @Override
    public boolean tryUnlink(EntityPlayer player, BlockPos other) {
        if(other.equals(getPos())) return false;

        if(TransmissionNetworkHelper.hasTransmissionLink(this, other)) {
            TransmissionNetworkHelper.removeTransmissionLink(this, other);
            this.positions.remove(other);
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean doesAcceptLinks() {
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return positions;
    }

    @Override
    public void markUpdated() {
        this.needsUpdate = false;
    }

    @Override
    public boolean needToUpdateStarlightSource() {
        return needsUpdate;
    }

}
