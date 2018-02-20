/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionSource;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightSource
 * Created by HellFirePvP
 * Date: 01.08.2016 / 12:23
 */
public interface IStarlightSource extends IStarlightTransmission {

    @Nonnull
    public IIndependentStarlightSource provideNewSourceNode();

    @Nonnull
    public ITransmissionSource provideSourceNode(BlockPos at);

    public boolean needToUpdateStarlightSource();

    public void markUpdated();

    @Override
    @Nonnull
    default public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return provideSourceNode(at);
    }

}
