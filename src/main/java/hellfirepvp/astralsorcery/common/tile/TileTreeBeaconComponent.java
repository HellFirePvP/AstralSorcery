/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.tile.base.TileFakedState;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileTreeBeaconComponent
 * Created by HellFirePvP
 * Date: 04.09.2020 / 19:37
 */
public class TileTreeBeaconComponent extends TileFakedState {

    private BlockPos treeBeaconPos = BlockPos.ZERO;

    public TileTreeBeaconComponent() {
        super(TileEntityTypesAS.TREE_BEACON_COMPONENT);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isRemote() && this.getTicksExisted() % 200 == 0) {
            if (this.getTreeBeaconPos().equals(BlockPos.ZERO)) {
                this.removeSelf();
            } else {
                TileTreeBeacon ttb = MiscUtils.getTileAt(this.getWorld(), this.getTreeBeaconPos(), TileTreeBeacon.class, false);
                if (ttb == null) {
                    this.removeSelf();
                }
            }
        }
    }

    @Nonnull
    public BlockPos getTreeBeaconPos() {
        return treeBeaconPos;
    }

    public void setTreeBeaconPos(BlockPos treeBeaconPos) {
        this.treeBeaconPos = treeBeaconPos;
        this.markForUpdate();
    }

    @Override
    public void readCustomNBT(CompoundNBT compound) {
        super.readCustomNBT(compound);

        this.treeBeaconPos = NBTHelper.readFromSubTag(compound, "treeBeaconPos", NBTHelper::readBlockPosFromNBT);
    }

    @Override
    public void writeCustomNBT(CompoundNBT compound) {
        super.writeCustomNBT(compound);

        NBTHelper.setAsSubTag(compound, "treeBeaconPos", tag -> NBTHelper.writeBlockPosToNBT(this.treeBeaconPos, tag));
    }
}
