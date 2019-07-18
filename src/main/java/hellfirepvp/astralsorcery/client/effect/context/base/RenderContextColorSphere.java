/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.context.base;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.effect.vfx.FXColorEffectSphere;
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderContextColorSphere
 * Created by HellFirePvP
 * Date: 18.07.2019 / 21:08
 */
public class RenderContextColorSphere extends BatchRenderContext<FXColorEffectSphere> {

    public RenderContextColorSphere() {
        super(AbstractRenderableTexture.wrap(TextureHelper.getMissingTexture()),
                (ctx, pTicks) -> {
                    GlStateManager.enableBlend();
                    Blending.DEFAULT.applyStateManager();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0001F);
                    GlStateManager.disableTexture();
                    GlStateManager.depthMask(false);
                    ctx.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
                },
                (pTicks) -> {
                    GlStateManager.depthMask(true);
                    GlStateManager.enableTexture();
                    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
                    GlStateManager.disableBlend();
                },
                (ctx, pos) -> new FXColorEffectSphere(pos));
    }
}
