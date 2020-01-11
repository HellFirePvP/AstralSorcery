/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXPositionController
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:33
 */
public interface VFXPositionController<T extends EntityVisualFX> {

    VFXPositionController<?> CONSTANT = (fx, position, motionToBeMoved) -> position.add(motionToBeMoved);

    @Nonnull
    public Vector3 updatePosition(@Nonnull T fx, @Nonnull Vector3 position, @Nonnull Vector3 motionToBeMoved);

}
