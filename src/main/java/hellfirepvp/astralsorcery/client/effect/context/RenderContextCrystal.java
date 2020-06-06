/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXCrystal;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 10:14
 */
public class RenderContextCrystal extends BatchRenderContext<FXCrystal> {

    public RenderContextCrystal(AbstractRenderableTexture resource) {
        super(resource, RenderTypesAS.EFFECT_FX_CRYSTAL, (ctx, pos) -> new FXCrystal(pos));
    }
}
