/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXScaleFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:33
 */
public interface VFXScaleFunction<T extends EntityVisualFX> {

    VFXScaleFunction<?> IDENTITY = (fx, scaleIn, pTicks) -> scaleIn;

    VFXScaleFunction<?> SHRINK = (VFXScaleFunction<EntityVisualFX>) (fx, scaleIn, pTicks) -> {
        float prevAge = Math.max(0F, ((float) fx.getAge() - 1)) / ((float) fx.getMaxAge());
        float currAge = Math.max(0F, ((float) fx.getAge())) / ((float) fx.getMaxAge());
        return (float) (scaleIn * (1 - (RenderingVectorUtils.interpolate(prevAge, currAge, pTicks))));
    };

    public float getScale(@Nonnull T fx, float scaleIn, float pTicks);

}
