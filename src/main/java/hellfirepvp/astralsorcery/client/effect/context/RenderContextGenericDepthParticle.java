/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextGenericDepthParticle
 * Created by HellFirePvP
 * Date: 17.07.2019 / 22:24
 */
public class RenderContextGenericDepthParticle extends BatchRenderContext<FXFacingParticle> {

    public RenderContextGenericDepthParticle() {
        super(TexturesAS.TEX_STATIC_FLARE, 0,
                (ctx, pTicks) -> {
                    GlStateManager.disableAlphaTest();
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableCull();
                    GlStateManager.disableDepthTest();
                    GlStateManager.depthMask(false);
                    ctx.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                },
                (pTicks) -> {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableDepthTest();
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlphaTest();
                },
                (ctx, pos) -> new FXFacingParticle(pos).setScaleMultiplier(0.2F));
    }

}