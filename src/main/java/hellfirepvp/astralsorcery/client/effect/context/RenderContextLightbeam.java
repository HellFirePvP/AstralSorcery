/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.effect.function.VFXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.vfx.FXLightbeam;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.ObjectReference;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextLightbeam
 * Created by HellFirePvP
 * Date: 17.07.2019 / 23:53
 */
public class RenderContextLightbeam extends BatchRenderContext<FXLightbeam> {

    public RenderContextLightbeam(ObjectReference<Boolean> lightingRef) {
        super(SpritesAS.SPR_LIGHTBEAM,
                (ctx, pTicks) -> {
                    GlStateManager.pushMatrix();
                    RenderingVectorUtils.removeStandardTranslationFromTESRMatrix(pTicks);
                    GlStateManager.enableBlend();
                    GlStateManager.disableCull();
                    GlStateManager.depthMask(false);
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
                    Blending.PREALPHA.applyStateManager();
                    lightingRef.set(GL11.glGetBoolean(GL11.GL_LIGHTING));
                    if (lightingRef.get()) {
                        GlStateManager.disableLighting();
                    }
                    ctx.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                },
                (pTicks) -> {
                    if (lightingRef.get()) {
                        GlStateManager.enableLighting();
                    }
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.depthMask(true);
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                },
                (ctx, pos) -> new FXLightbeam(pos).alpha(VFXAlphaFunction.PYRAMID));
    }

}
