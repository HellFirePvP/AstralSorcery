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
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightbeam;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextLightbeam
 * Created by HellFirePvP
 * Date: 17.07.2019 / 23:53
 */
public class RenderContextLightbeam extends BatchRenderContext<FXLightbeam> {

    public RenderContextLightbeam(SpriteSheetResource sprite) {
        super(sprite, RenderTypesAS.EFFECT_FX_LIGHTBEAM, (ctx, pos) -> new FXLightbeam(pos).alpha(VFXAlphaFunction.PYRAMID));
    }

}
