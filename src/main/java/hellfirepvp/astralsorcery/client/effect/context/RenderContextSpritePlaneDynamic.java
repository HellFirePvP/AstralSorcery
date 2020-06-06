/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextSpritePlaneDynamic
 * Created by HellFirePvP
 * Date: 30.08.2019 / 19:40
 */
public class RenderContextSpritePlaneDynamic extends BatchRenderContext<FXSpritePlane> {

    public RenderContextSpritePlaneDynamic() {
        super(new SpriteSheetResource(TexturesAS.TEX_BLANK), RenderTypesAS.EFFECT_FX_DYNAMIC_TEXTURE_SPRITE,
                (ctx, pos) -> new FXSpritePlane(pos));
    }
}
