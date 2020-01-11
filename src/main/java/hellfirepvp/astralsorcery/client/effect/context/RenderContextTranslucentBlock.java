/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.vfx.FXBlock;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTextureResource;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextTranslucentBlock
 * Created by HellFirePvP
 * Date: 18.07.2019 / 16:09
 */
public class RenderContextTranslucentBlock extends BatchRenderContext<FXBlock> {

    public RenderContextTranslucentBlock() {
        super(BlockAtlasTextureResource.getInstance(),
                (ctx, pTicks) -> {
                    GlStateManager.pushMatrix();
                    RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
                    GlStateManager.enableBlend();
                    Blending.ADDITIVEDARK.applyStateManager();
                    GlStateManager.disableCull();
                },
                (pTicks) -> {
                    GlStateManager.enableCull();
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableBlend();
                    GlStateManager.color4f(1F, 1F, 1F, 1F);
                    GlStateManager.popMatrix();
                },
                (ctx, pos) -> new FXBlock(pos));
    }

}
