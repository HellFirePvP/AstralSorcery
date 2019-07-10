/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
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

        if (!this.world.isRemote() && !this.isNetworkInformed) {
            if (!TransmissionNetworkHelper.isTileInNetwork(this)) {
                TransmissionNetworkHelper.informNetworkTilePlacement(this);
            }
            this.isNetworkInformed = true;
        }
    }

    public void onBreak() {
        if (this.world.isRemote()) return;
        TransmissionNetworkHelper.informNetworkTileRemoval(this);
        this.isNetworkInformed = false;
    }

}
