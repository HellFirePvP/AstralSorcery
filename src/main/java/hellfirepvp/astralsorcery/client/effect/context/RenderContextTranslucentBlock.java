/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXBlock;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextTranslucentBlock
 * Created by HellFirePvP
 * Date: 18.07.2019 / 16:09
 */
public class RenderContextTranslucentBlock extends BatchRenderContext<FXBlock> {

    public RenderContextTranslucentBlock() {
        super(RenderTypesAS.EFFECT_FX_BLOCK_TRANSLUCENT, (ctx, pos) -> new FXBlock(pos));
    }
}
