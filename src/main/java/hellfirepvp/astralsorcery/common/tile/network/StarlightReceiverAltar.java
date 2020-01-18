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
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightReceiverAltar
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:54
 */
public class StarlightReceiverAltar extends SimpleTransmissionReceiver {

    public StarlightReceiverAltar(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public void onStarlightReceive(World world, IWeakConstellation type, double amount) {
        TileAltar well = getTileAtPos(world, TileAltar.class);
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
            return new StarlightReceiverAltar(null);
        }

    }
}
