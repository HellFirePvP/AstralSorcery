/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextBurst
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:06
 */
public class RenderContextBurst extends BatchRenderContext<FXFacingParticle> {

    public RenderContextBurst(SpriteSheetResource sprite) {
        super(sprite, RenderTypesAS.EFFECT_FX_BURST, (ctx, pos) -> new FXFacingParticle(pos));
    }
}
