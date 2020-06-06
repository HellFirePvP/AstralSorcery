/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingSprite;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextFacingSprite
 * Created by HellFirePvP
 * Date: 18.07.2019 / 00:25
 */
public class RenderContextFacingSprite extends BatchRenderContext<FXFacingSprite> {

    public RenderContextFacingSprite() {
        super(new SpriteSheetResource(TexturesAS.TEX_BLANK), RenderTypesAS.EFFECT_FX_TEXTURE_SPRITE,
                (ctx, pos) -> new FXFacingSprite(pos));
    }

}
