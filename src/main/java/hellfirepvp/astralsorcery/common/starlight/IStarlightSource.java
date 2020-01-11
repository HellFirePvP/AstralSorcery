/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

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
public interface IStarlightSource<T extends ITransmissionSource> extends IStarlightTransmission {

    @Nonnull
    public IIndependentStarlightSource provideNewSourceNode();

    @Nonnull
    public T provideSourceNode(BlockPos at);

    public boolean needsToRefreshNetworkChain();

    public void markChainRebuilt();

    @Override
    @Nonnull
    default public T provideTransmissionNode(BlockPos at) {
        return provideSourceNode(at);
    }

}
