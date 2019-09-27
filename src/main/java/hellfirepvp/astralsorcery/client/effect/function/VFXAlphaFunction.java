/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXAlphaFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:30
 */
public interface VFXAlphaFunction<T extends EntityVisualFX> {

    VFXAlphaFunction CONSTANT = (fx, alphaIn, pTicks) -> alphaIn;

    VFXAlphaFunction FADE_OUT = (fx, alphaIn, pTicks) -> (1F - (((float) fx.getAge()) / ((float) fx.getMaxAge()))) * alphaIn;

    VFXAlphaFunction PYRAMID = (fx, alphaIn, pTicks) -> {
        float halfAge = fx.getMaxAge() / 2F;
        return (1F - (Math.abs(halfAge - fx.getAge()) / halfAge)) * alphaIn;
    };

    public float getAlpha(T fx, float alphaIn, float pTicks);

    public static  <T extends EntityVisualFX> VFXAlphaFunction<T> fadeIn(float fadeInTicks) {
        return (fx, alphaIn, pTicks) -> {
            if (fx.getAgeRefreshCount() > 0) {
                return alphaIn;
            }
            float mul = MathHelper.clamp((fadeInTicks - (fx.getAge() + pTicks)) / fadeInTicks, 0F, 1F);
            return alphaIn * mul;
        };
    }

}
