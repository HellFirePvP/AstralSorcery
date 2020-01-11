/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionProvider;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionSourceNode
 * Created by HellFirePvP
 * Date: 04.08.2016 / 14:59
 */
public class SimpleTransmissionSourceNode extends SimplePrismTransmissionNode implements ITransmissionSource {

    public SimpleTransmissionSourceNode(BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public IIndependentStarlightSource provideNewIndependentSource(IStarlightSource source) {
        return source.provideNewSourceNode();
    }

    @Override
    public TransmissionProvider getProvider() {
        return new Provider();
    }

    public static class Provider extends TransmissionProvider {

        @Override
        public IPrismTransmissionNode get() {
            return new SimpleTransmissionSourceNode(null);
        }

    }

}
