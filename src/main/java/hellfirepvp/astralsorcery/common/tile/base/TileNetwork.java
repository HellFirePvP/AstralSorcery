/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileNetwork
 * Created by HellFirePvP
 * Date: 03.08.2016 / 18:12
 */
public abstract class TileNetwork extends TileEntityTick {

    protected static final Random rand = new Random();

    @Override
    protected void onFirstTick() {
        if(world.isRemote) return;
        TransmissionNetworkHelper.informNetworkTilePlacement(this);
    }

    public void onBreak() {
        if(world.isRemote) return;
        TransmissionNetworkHelper.informNetworkTileRemoval(this);
    }

}
