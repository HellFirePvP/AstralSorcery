/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightning;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextLightning
 * Created by HellFirePvP
 * Date: 17.07.2019 / 22:30
 */
public class RenderContextLightning extends BatchRenderContext<FXLightning> {

    public RenderContextLightning() {
        super(TexturesAS.TEX_LIGHTNING_PART, RenderTypesAS.EFFECT_FX_LIGHTNING, (ctx, pos) -> new FXLightning(pos));
    }

}
