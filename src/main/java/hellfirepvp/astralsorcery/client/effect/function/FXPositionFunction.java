/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXPositionFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXPositionFunction<T extends EntityFX> {

    FXPositionFunction<?> IDENTITY = (fx, position, motionToBeMoved) -> position.add(motionToBeMoved);

    @Nonnull
    Vector3 updatePosition(@Nonnull T fx, @Nonnull Vector3 position, @Nonnull Vector3 motionToBeMoved);

}
