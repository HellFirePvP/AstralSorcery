/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base.network;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.auxiliary.link.LinkableTileEntity;
import hellfirepvp.astralsorcery.common.starlight.IStarlightTransmission;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import hellfirepvp.astralsorcery.common.tile.base.TileNetwork;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTransmissionBase
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:46
 */
public abstract class TileTransmissionBase<T extends IPrismTransmissionNode> extends TileNetwork<T> implements IStarlightTransmission<T>, LinkableTileEntity {

    private List<BlockPos> positions = new LinkedList<>();

    protected TileTransmissionBase(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public boolean onSelect(PlayerEntity player) {
        if (player.isSneaking()) {
            for (BlockPos linkTo : Lists.newArrayList(getLinkedPositions())) {
                tryUnlink(player, linkTo);
            }
            player.sendMessage(new TranslationTextComponent("astralsorcery.misc.link.unlink.all").setStyle(new Style().setColor(TextFormatting.GREEN)));
            return false;
        }
        return true;
    }

    public abstract boolean isSingleLink();

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
    }

    @Override
    public void onLinkCreate(PlayerEntity player, BlockPos other) {
        if (other.equals(getPos())) return;

        if (TransmissionNetworkHelper.createTransmissionLink(this, other)) {
            if (this.isSingleLink()) {
                this.positions.clear();
            }

            if (this.isSingleLink() || !this.positions.contains(other)) {
                this.positions.add(other);
                markForUpdate();
            }
        }
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
    public boolean tryLink(PlayerEntity player, BlockPos other) {
        return !other.equals(getPos()) && TransmissionNetworkHelper.canCreateTransmissionLink(this, other);
    }

    @Override
    public boolean tryUnlink(PlayerEntity player, BlockPos other) {
        if (other.equals(getPos())) return false;

        if (TransmissionNetworkHelper.hasTransmissionLink(this, other)) {
            TransmissionNetworkHelper.removeTransmissionLink(this, other);
            this.positions.remove(other);
            markForUpdate();
            return true;
        }
        return false;
    }

    @Override
    public List<BlockPos> getLinkedPositions() {
        return positions;
    }
}