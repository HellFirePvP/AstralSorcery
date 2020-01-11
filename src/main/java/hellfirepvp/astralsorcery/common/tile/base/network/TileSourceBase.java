/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base.network;

import hellfirepvp.astralsorcery.common.auxiliary.link.LinkableTileEntity;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileSourceBase
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:06
 */
public abstract class TileSourceBase<T extends ITransmissionSource> extends TileNetwork<T> implements IStarlightSource<T>, LinkableTileEntity {

    protected boolean needsNetworkChainRebuild = false;
    private boolean linked = false;
    private List<BlockPos> positions = new LinkedList<>();

    protected TileSourceBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public boolean hasBeenLinked() {
        return linked;
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);
        positions.clear();

        if (compound.contains("linked")) {
            ListNBT list = compound.getList("linked", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundNBT tag = list.getCompound(i);
                positions.add(NBTHelper.readBlockPosFromNBT(tag));
            }
        }

        this.linked = compound.getBoolean("wasLinkedBefore");
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        ListNBT list = new ListNBT();
        for (BlockPos pos : positions) {
            CompoundNBT tag = new CompoundNBT();
            NBTHelper.writeBlockPosToNBT(pos, tag);
            list.add(tag);
        }
        compound.put("linked", list);
        compound.putBoolean("wasLinkedBefore", linked);
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
    public void onLinkCreate(PlayerEntity player, BlockPos other) {
        if (other.equals(getPos())) return;

        if (TransmissionNetworkHelper.createTransmissionLink(this, other)) {
            if (!this.positions.contains(other)) {
                this.positions.add(other);
                this.needsNetworkChainRebuild = true;
                markForUpdate();
            }

            if (!hasBeenLinked()) {
                this.linked = true;
            }
        }
    }

    @Override
    public boolean tryLink(PlayerEntity player, BlockPos other) {
        return !other.equals(getPos()) && TransmissionNetworkHelper.canCreateTransmissionLink(this, other);
    }

    @Override
    public boolean tryUnlink(PlayerEntity player, BlockPos other) {
        if (other.equals(getPos())) return false;

        if (TransmissionNetworkHelper.hasTransmissionLink(this, other)) {
            TransmissionNetworkHelper.removeTransmissionLink(this, other);
            this.positions.remove(other);
            this.needsNetworkChainRebuild = true;
            markForUpdate();
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
    public boolean needsToRefreshNetworkChain() {
        return this.needsNetworkChainRebuild;
    }

    @Override
    public void markChainRebuilt() {
        this.needsNetworkChainRebuild = false;
    }
}
