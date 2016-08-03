package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 18:12
 */
public abstract class TileNetwork extends TileEntityTick {

    @Override
    protected void onFirstTick() {
        if(worldObj.isRemote) return;
        TransmissionNetworkHelper.informNetworkTilePlacement(this);
    }

    public void onBreak() {
        if(worldObj.isRemote) return;
        TransmissionNetworkHelper.informNetworkTileRemoval(this);
    }

}
