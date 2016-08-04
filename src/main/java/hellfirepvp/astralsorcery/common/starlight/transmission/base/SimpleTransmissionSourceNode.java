package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.common.starlight.IIndependentStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.IStarlightSource;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionSourceNode
 * Created by HellFirePvP
 * Date: 04.08.2016 / 14:59
 */
public class SimpleTransmissionSourceNode extends SimplePrismTransmissionNode implements ITransmissionSource {

    public SimpleTransmissionSourceNode(@Nonnull BlockPos thisPos) {
        super(thisPos);
    }

    @Override
    public IPrismTransmissionNode provideEmptyNBTReadInstance() {
        return new SimpleTransmissionSourceNode(null);
    }

    @Override
    public IIndependentStarlightSource provideNewIndependentSource(IStarlightSource source) {
        return source.provideNewSourceNode();
    }
}
