/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingAtlasParticle;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextAtlasParticle
 * Created by HellFirePvP
 * Date: 10.11.2019 / 15:17
 */
public class RenderContextAtlasParticle extends BatchRenderContext<FXFacingAtlasParticle> {

    public RenderContextAtlasParticle() {
        super(RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE_ATLAS, (ctx, pos) -> new FXFacingAtlasParticle(pos));
    }

}
