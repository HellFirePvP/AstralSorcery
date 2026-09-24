/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.screen.base.ScalingSizeHandler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.GameRenderer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeStarParallaxLayer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TomeStarParallaxLayer {

    default void drawStarParallaxLayers(GuiGraphics guiGraphics, float sizeScale, ScreenRectangle rect, float scalePosX, float scalePosY) {
        TexturesAS.SCREEN_TOME_STARFIELD_OVERLAY.bindTexture();
        RenderSystem.enableBlend();
        Blending.OVERLAYDARK.apply();

        float offsetX = scalePosX / 2000F;
        float offsetY = scalePosY / 1000F;

        RenderUtil.draw(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR, GameRenderer::getPositionTexColorShader, buf -> {
            drawStarParallaxLayer(buf, guiGraphics, rect, sizeScale, offsetX, offsetY, 2F);
            drawStarParallaxLayer(buf, guiGraphics, rect, sizeScale, offsetX, offsetY, 1.5F);
            drawStarParallaxLayer(buf, guiGraphics, rect, sizeScale, offsetX, offsetY, 1F);
            drawStarParallaxLayer(buf, guiGraphics, rect, sizeScale, offsetX, offsetY, 0.75F);
            drawStarParallaxLayer(buf, guiGraphics, rect, sizeScale, offsetX, offsetY, 0.5F);
            drawStarParallaxLayer(buf, guiGraphics, rect, sizeScale, offsetX, offsetY, 0.3F);
        });

        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
    }

    default void drawStarParallaxLayer(VertexConsumer buf, GuiGraphics guiGraphics, ScreenRectangle rect, float sizeScale, float scalePosX, float scalePosY, float scaleFactor) {
        float scale = sizeScale / 40F;

        float minX = rect.left();
        float minY = rect.top();
        float maxX = rect.right();
        float maxY = rect.bottom();

        float u  = 0.2F + scalePosX + scaleFactor + scale;
        float v  = 0.2F + scalePosY + scaleFactor + scale;
        float uL = 0.6F * scaleFactor - (scale * 2);
        float vL = 0.6F * scaleFactor - (scale * 2);

        if (vL <= 0 || uL <= 0) {
            return;
        }

        PoseStack.Pose offset = guiGraphics.pose().last();
        buf.addVertex(offset, minX, maxY, 0)
                .setColor(0.75F, 0.75F, 0.75F, 0.7F)
                .setUv(u,  v + vL);
        buf.addVertex(offset, maxX, maxY, 0)
                .setColor(0.75F, 0.75F, 0.75F, 0.7F)
                .setUv(u + uL, v + vL);
        buf.addVertex(offset, maxX, minY, 0)
                .setColor(0.75F, 0.75F, 0.75F, 0.7F)
                .setUv(u + uL, v);
        buf.addVertex(offset, minX, minY, 0)
                .setColor(0.75F, 0.75F, 0.75F, 0.7F)
                .setUv(u, v);
    }

}
