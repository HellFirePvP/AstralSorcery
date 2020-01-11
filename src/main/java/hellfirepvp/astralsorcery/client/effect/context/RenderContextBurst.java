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
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.Blending;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextBurst
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:06
 */
public class RenderContextBurst extends BatchRenderContext<FXFacingParticle> {

    public RenderContextBurst(SpriteSheetResource sprite) {
        super(sprite,
                (ctx, pTicks) -> {
                    GlStateManager.disableAlphaTest();
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.disableCull();
                    GlStateManager.depthMask(false);
                    ctx.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                },
                (pTicks) -> {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableCull();
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlphaTest();
                },
                (ctx, pos) -> new FXFacingParticle(pos));
    }

}
