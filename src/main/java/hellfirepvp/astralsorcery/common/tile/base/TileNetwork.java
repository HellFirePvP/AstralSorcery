/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 18:12
 */
public abstract class TileNetwork<T extends IPrismTransmissionNode> extends TileEntityTick {

    protected static final Random rand = new Random();
    private boolean isNetworkInformed = false;

    private T cachedNetworkNode = null;
    private boolean needsNetworkSync = false;

    protected TileNetwork(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Nullable
    public T getNetworkNode() {
        if (cachedNetworkNode != null) {
            if (!cachedNetworkNode.getLocationPos().equals(getPos())) {
                cachedNetworkNode = null;
            }
        }
        if (cachedNetworkNode == null) {
            cachedNetworkNode = resolveNode();
        }
        return cachedNetworkNode;
    }

    @Nullable
    private T resolveNode() {
        IPrismTransmissionNode node = WorldNetworkHandler.getNetworkHandler(getWorld()).getTransmissionNode(getPos());
        if (node == null) {
            return null;
        }
        return (T) node;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.getWorld().isRemote()) {
            if (!this.isNetworkInformed) {
                if (!TransmissionNetworkHelper.isTileInNetwork(this)) {
                    TransmissionNetworkHelper.informNetworkTilePlacement(this);
                }
                this.isNetworkInformed = true;
            }

            if (this.needsNetworkSync) {
                this.doNetworkSync();
            }
        }
    }

    protected void doNetworkSync() {
        T networkNode = this.getNetworkNode();
        if (networkNode != null && networkNode.updateFromTileEntity(this)) {
            this.needsNetworkSync = false;
            this.markForUpdate();
            this.preventNetworkSync();
        }
    }

    @Override
    public void markForUpdate() {
        super.markForUpdate();
        this.needsNetworkSync = true;
    }

    protected void preventNetworkSync() {
        this.needsNetworkSync = false;
    }

    public void onBreak() {
        if (this.getWorld().isRemote()) {
            return;
        }
        TransmissionNetworkHelper.informNetworkTileRemoval(this);
        this.isNetworkInformed = false;
    }

    @Override
    public void writeSaveNBT(CompoundNBT compound) {
        super.writeSaveNBT(compound);

        compound.putBoolean("needsNetworkSync", this.needsNetworkSync);
    }

    @Override
    public void readSaveNBT(CompoundNBT compound) {
        super.readSaveNBT(compound);

        this.needsNetworkSync = compound.getBoolean("needsNetworkSync");
    }
}
