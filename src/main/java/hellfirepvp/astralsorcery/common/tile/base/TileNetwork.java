/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.starlight.transmission.TransmissionNetworkHelper;
import net.minecraft.tileentity.TileEntityType;

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
    private boolean isNetworkInformed = false;

    protected TileNetwork(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
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
