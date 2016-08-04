package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightSource
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:23
 */
public interface IStarlightSource extends IStarlightTransmission {

    public IIndependentStarlightSource provideNewSourceNode();

    public ITransmissionSource provideSourceNode(BlockPos at);

    public boolean updateStarlightSource();

    public void markUpdated();

    @Override
    default public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return provideSourceNode(at);
    }

}
