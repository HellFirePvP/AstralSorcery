/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXAlphaFunction
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:30
 */
public interface VFXAlphaFunction<T extends EntityVisualFX> {

    VFXAlphaFunction CONSTANT = (fx, alphaIn, pTicks) -> alphaIn;

    VFXAlphaFunction FADE_OUT = (fx, alphaIn, pTicks) -> alphaIn - (((float) fx.getAge()) / ((float) fx.getMaxAge())) * alphaIn;

    VFXAlphaFunction PYRAMID = (fx, alphaIn, pTicks) -> {
        float halfAge = fx.getMaxAge() / 2F;
        return alphaIn - (Math.abs(halfAge - fx.getAge()) / fx.getMaxAge()) * alphaIn;
    };

    public float getAlpha(T fx, float alphaIn, float pTicks);

}
