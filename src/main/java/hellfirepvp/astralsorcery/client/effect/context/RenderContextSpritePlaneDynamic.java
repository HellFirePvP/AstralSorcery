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
import hellfirepvp.astralsorcery.client.effect.vfx.FXSpritePlane;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextSpritePlaneDynamic
 * Created by HellFirePvP
 * Date: 30.08.2019 / 19:40
 */
public class RenderContextSpritePlaneDynamic extends BatchRenderContext<FXSpritePlane> {

    public RenderContextSpritePlaneDynamic() {
        super(new SpriteSheetResource(TexturesAS.TEX_BLANK),
                (ctx, pTicks) -> {
                    GlStateManager.pushMatrix();
                    RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
                    GlStateManager.disableCull();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                },
                (pTicks) -> {
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableBlend();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.enableCull();
                    GlStateManager.popMatrix();
                },
                (ctx, pos) -> new FXSpritePlane(pos));
    }
}
