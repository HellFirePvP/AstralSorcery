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
import hellfirepvp.astralsorcery.client.effect.vfx.FXCrystal;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 10:14
 */
public class RenderContextCrystal extends BatchRenderContext<FXCrystal> {

    public RenderContextCrystal(AbstractRenderableTexture resource) {
        super(resource,
                (ctx, pTicks) -> {
                    GlStateManager.pushMatrix();
                    RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableCull();
                    GlStateManager.depthMask(false);
                },
                (pTicks) -> {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                },
                (ctx, pos) -> new FXCrystal(pos));
    }
}
