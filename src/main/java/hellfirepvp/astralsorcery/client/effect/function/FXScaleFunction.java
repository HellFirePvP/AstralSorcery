/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import net.minecraft.util.Mth;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXScaleFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXScaleFunction<T extends EntityVisualFX> {

    FXScaleFunction<EntityVisualFX> IDENTITY = (fx, scaleIn, pTicks) -> scaleIn;

    FXScaleFunction<EntityVisualFX> SHRINK = (fx, scaleIn, pTicks) -> {
        float prevAge = Math.max(0F, ((float) fx.getAge() - 1)) / ((float) fx.getMaxAge());
        float currAge = Math.max(0F, ((float) fx.getAge())) / ((float) fx.getMaxAge());
        return scaleIn * (1 - (RenderVectorUtil.interpolate(prevAge, currAge, pTicks)));
    };

    FXScaleFunction<EntityVisualFX> SHRINK_EXP = (fx, scaleIn, pTicks) -> {
        return Mth.sqrt(SHRINK.getScale(fx, scaleIn, pTicks));
    };

    float getScale(@Nonnull T fx, float scaleIn, float pTicks);

    default FXScaleFunction<T> andThen(FXScaleFunction<T> multiplied) {
        FXScaleFunction<T> existing = this;
        return (fx, scaleIn, pTicks) -> multiplied.getScale(fx, existing.getScale(fx, scaleIn, pTicks), pTicks);
    }
}
