/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import hellfirepvp.astralsorcery.client.effect.vfx.FXColorEffectSphere;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextColorSphere
 * Created by HellFirePvP
 * Date: 18.07.2019 / 21:08
 */
public class RenderContextColorSphere extends BatchRenderContext<FXColorEffectSphere> {

    public RenderContextColorSphere() {
        super(RenderTypesAS.EFFECT_FX_COLOR_SPHERE, (ctx, pos) -> new FXColorEffectSphere(pos));
    }
}
