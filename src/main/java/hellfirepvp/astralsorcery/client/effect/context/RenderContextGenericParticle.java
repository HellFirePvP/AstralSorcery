/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextGenericParticle
 * Created by HellFirePvP
 * Date: 17.07.2019 / 20:32
 */
public class RenderContextGenericParticle extends BatchRenderContext<FXFacingParticle> {

    private static final VFXColorFunction<FXFacingParticle> defaultColor =
            VFXColorFunction.constant(ColorsAS.DEFAULT_GENERIC_PARTICLE);

    public RenderContextGenericParticle() {
        super(TexturesAS.TEX_PARTICLE_SMALL, RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE,
                (ctx, pos) -> new FXFacingParticle(pos)
                        .setScaleMultiplier(0.2F)
                        .setAlphaMultiplier(0.85F)
                        .alpha(VFXAlphaFunction.PYRAMID)
                        .color(defaultColor));
    }

}
