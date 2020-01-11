/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IStarlightReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:43
 */
public interface IStarlightReceiver<T extends ITransmissionReceiver> extends IStarlightTransmission {

    @Nonnull
    public T provideEndpoint(BlockPos at);

    @Override
    @Nonnull
    default public T provideTransmissionNode(BlockPos at) {
        return provideEndpoint(at);
    }
}
