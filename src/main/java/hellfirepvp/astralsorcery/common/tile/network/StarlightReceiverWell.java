/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.network;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.base.SimpleTransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import hellfirepvp.astralsorcery.common.tile.TileWell;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightReceiverWell
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:57
 */
public class StarlightReceiverWell extends SimpleTransmissionReceiver {

    public StarlightReceiverWell(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public void onStarlightReceive(World world, IWeakConstellation type, double amount) {
        TileWell well = getTileAtPos(world, TileWell.class);
        if (well != null) {
            well.receiveStarlight(amount);
        }
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new StarlightReceiverWell(null);
        }

    }

}
