/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXScaleFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:33
 */
public interface VFXScaleFunction<T extends EntityVisualFX> {

    VFXScaleFunction<EntityVisualFX> IDENTITY = (fx, scaleIn, pTicks) -> scaleIn;

    VFXScaleFunction<EntityVisualFX> SHRINK = (fx, scaleIn, pTicks) -> {
        float prevAge = Math.max(0F, ((float) fx.getAge() - 1)) / ((float) fx.getMaxAge());
        float currAge = Math.max(0F, ((float) fx.getAge())) / ((float) fx.getMaxAge());
        return (float) (scaleIn * (1 - (RenderingVectorUtils.interpolate(prevAge, currAge, pTicks))));
    };

    VFXScaleFunction<EntityVisualFX> SHRINK_EXP = (fx, scaleIn, pTicks) -> {
        return MathHelper.sqrt(SHRINK.getScale(fx, scaleIn, pTicks));
    };

    public float getScale(@Nonnull T fx, float scaleIn, float pTicks);

}
