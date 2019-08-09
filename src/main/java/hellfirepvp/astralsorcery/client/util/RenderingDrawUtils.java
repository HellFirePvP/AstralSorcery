/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingDrawUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:27
 */
public class RenderingDrawUtils {

    public static void renderStringCentered(@Nullable FontRenderer fr, String str, int x, int y, float scale, int color) {
        if (fr == null) {
            fr = Minecraft.getInstance().fontRenderer;
        }

        double strLength = fr.getStringWidth(str) * scale;
        double offsetLeft = x - strLength;

        GlStateManager.pushMatrix();
        GlStateManager.translated(offsetLeft, y, 0);
        GlStateManager.scaled(scale, scale, scale);
        renderStringAtCurrentPos(fr, str, color);
        GlStateManager.popMatrix();
    }

    public static void renderStringAtCurrentPos(@Nullable FontRenderer fr, String str, int color) {
        renderStringAtPos(0, 0, fr, str, color, false);
    }

    public static void renderStringWithShadowAtCurrentPos(@Nullable FontRenderer fr, String str, int color) {
        renderStringAtPos(0, 0, fr, str, color, true);
    }

    public static void renderStringAtPos(int x, int y, @Nullable FontRenderer fr, String str, int color, boolean dropShadow) {
        if (fr == null) {
            fr = Minecraft.getInstance().fontRenderer;
        }

        //FontRenderer always enables alpha test, so we disable it afterwards if necessary.
        boolean alphaTest = GL11.glGetBoolean(GL11.GL_ALPHA_TEST);

        fr.drawString(str, x, y, color);

        if (!alphaTest) {
            GlStateManager.disableAlphaTest();
        }
    }

    public static Rectangle drawInfoStar(float offsetX, float offsetY, float zLevel, float widthHeightBase, float pTicks) {
        float tick = ClientScheduler.getClientTick() + pTicks;
        float deg = (tick * 2) % 360F;
        float wh = widthHeightBase - (widthHeightBase / 6F) * (MathHelper.sin((float) Math.toRadians(((tick) * 4) % 360F)) + 1F);
        drawInfoStarSingle(offsetX, offsetY, zLevel, wh, Math.toRadians(deg));

        deg = ((tick + 22.5F) * 2) % 360F;
        wh = widthHeightBase - (widthHeightBase / 6F) * (MathHelper.sin((float) Math.toRadians(((tick + 45F) * 4) % 360F)) + 1F);
        drawInfoStarSingle(offsetX, offsetY, zLevel, wh, Math.toRadians(deg));

        return new Rectangle(MathHelper.floor(offsetX - widthHeightBase / 2F), MathHelper.floor(offsetY - widthHeightBase / 2F),
                MathHelper.floor(widthHeightBase), MathHelper.floor(widthHeightBase));
    }

    public static void drawInfoStarSingle(float offsetX, float offsetY, float zLevel, float widthHeight, double deg) {
        TexturesAS.TEX_STAR_1.bindTexture();
        Vector3 offset = new Vector3(-widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv01   = new Vector3(-widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv11   = new Vector3( widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv10   = new Vector3( widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX + uv01.getX(),   offsetY + uv01.getY(),   zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + uv11.getX(),   offsetY + uv11.getY(),   zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + uv10.getX(),   offsetY + uv10.getY(),   zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX + offset.getX(), offsetY + offset.getY(), zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    public static void renderBlueTooltipComponents(int x, int y, List<ITextComponent> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        renderBlueTooltip(x, y, tooltipData.stream().map(ITextComponent::getFormattedText).collect(Collectors.toList()), fontRenderer, isFirstLineHeadline);
    }

    public static void renderBlueTooltip(int x, int y, List<String> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        renderTooltip(x, y, tooltipData, 0x000027, 0x000044, Color.WHITE, fontRenderer, isFirstLineHeadline);
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int colorFade, Color strColor, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();

        if (!tooltipData.isEmpty()) {
            int maxWidth = 0;
            for (String toolTip : tooltipData) {
                int width = fontRenderer.getStringWidth(toolTip);
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            if(x + 15 + maxWidth > Minecraft.getInstance().mainWindow.getScaledWidth()) {
                x -= maxWidth + 24;
            }
            List<String> lengthLimitedToolTip = new ArrayList<>();
            for (String tTip : tooltipData) {
                lengthLimitedToolTip.addAll(fontRenderer.listFormattedStringToWidth(tTip, maxWidth));
            }

            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 8;
            if (tooltipData.size() > 1) {
                sumLineHeight += 2 + (tooltipData.size() - 1) * 10;
            }
            int z = 300;

            GuiUtils.drawGradientRect(z, pX - 3,           pY - 4,                 pX + maxWidth + 3, pY - 3,                 color, colorFade);
            GuiUtils.drawGradientRect(z, pX - 3,           pY + sumLineHeight + 3, pX + maxWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            GuiUtils.drawGradientRect(z, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            GuiUtils.drawGradientRect(z, pX - 4,           pY - 3,                 pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            GuiUtils.drawGradientRect(z, pX + maxWidth + 3,pY - 3,                 pX + maxWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int col = (color & 0x00FFFFFF) | color & 0xFF000000;
            GuiUtils.drawGradientRect(z, pX - 3,           pY - 3 + 1,             pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, col);
            GuiUtils.drawGradientRect(z, pX + maxWidth + 2,pY - 3 + 1,             pX + maxWidth + 3, pY + sumLineHeight + 3 - 1, color, col);
            GuiUtils.drawGradientRect(z, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY - 3 + 1,                 col,   col);
            GuiUtils.drawGradientRect(z, pX - 3,           pY + sumLineHeight + 2, pX + maxWidth + 3, pY + sumLineHeight + 3,     color, color);

            for (int i = 0; i < lengthLimitedToolTip.size(); ++i) {
                String str = lengthLimitedToolTip.get(i);
                fontRenderer.drawString(str, pX, pY, strColor.getRGB());
                if (isFirstLineHeadline && i == 0) {
                    pY += 2;
                }
                pY += 10;
            }
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }

    public static void renderBlueTooltipBox(int x, int y, int width, int height) {
        renderTooltipBox(x, y, width, height, 0x000027, 0x000044);
    }

    public static void renderTooltipBox(int x, int y, int width, int height, int color, int colorFade) {
        int pX = x + 12;
        int pY = y - 12;
        int z = 300;

        GuiUtils.drawGradientRect(z, pX - 3,           pY - 4,          pX + width + 3, pY - 3,         color, colorFade);
        GuiUtils.drawGradientRect(z, pX - 3,           pY + height + 3, pX + width + 3, pY + height + 4, color, colorFade);
        GuiUtils.drawGradientRect(z, pX - 3,           pY - 3,          pX + width + 3, pY + height + 3, color, colorFade);
        GuiUtils.drawGradientRect(z, pX - 4,           pY - 3,          pX - 3,         pY + height + 3, color, colorFade);
        GuiUtils.drawGradientRect(z, pX + width + 3,   pY - 3,          pX + width + 4, pY + height + 3, color, colorFade);

        int col = (color & 0x00FFFFFF) | color & 0xFF000000;
        GuiUtils. drawGradientRect(z, pX - 3,           pY - 3 + 1,      pX - 3 + 1,     pY + height + 3 - 1, color, col);
        GuiUtils. drawGradientRect(z, pX + width + 2,   pY - 3 + 1,      pX + width + 3, pY + height + 3 - 1, color, col);
        GuiUtils. drawGradientRect(z, pX - 3,           pY - 3,          pX + width + 3, pY - 3 + 1,          col,   col);
        GuiUtils. drawGradientRect(z, pX - 3,           pY + height + 2, pX + width + 3, pY + height + 3,     color, color);
    }

    public static void renderFacingFullQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, int r, int g, int b, float alpha) {
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, r, g, b, alpha);
    }

    public static void renderFacingQuadVB(BufferBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength, int r, int g, int b, float alpha) {
        Vector3 pos = new Vector3(px, py, pz);

        RenderInfo ri = RenderInfo.getInstance();
        ActiveRenderInfo ari = ri.getARI();

        float arX =  ri.getRotationX();
        float arZ =  ri.getRotationZ();
        float arYZ = ri.getRotationYZ();
        float arXY = ri.getRotationXY();
        float arXZ = ri.getRotationXZ();

        Vec3d view = ari.getProjectedView();
        Vec3d look = ari.getLookDirection();

        float cR = MathHelper.clamp(r / 255F, 0F, 1F);
        float cG = MathHelper.clamp(g / 255F, 0F, 1F);
        float cB = MathHelper.clamp(b / 255F, 0F, 1F);

        Vector3 iPos = new Vector3(view);
        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            float cAngle = MathHelper.cos(angle * 0.5F);
            float cAngleSq = cAngle * cAngle;

            Vector3 vAngle = new Vector3(
                    MathHelper.sin(angle * 0.5F) * look.x,
                    MathHelper.sin(angle * 0.5F) * look.y,
                    MathHelper.sin(angle * 0.5F) * look.z);

            v1 = vAngle.clone()
                    .multiply(2 * v1.dot(vAngle))
                    .add(v1.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v1.clone().multiply(2 * cAngle)));
            v2 = vAngle.clone()
                    .multiply(2 * v2.dot(vAngle))
                    .add(v2.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v2.clone().multiply(2 * cAngle)));
            v3 = vAngle.clone()
                    .multiply(2 * v3.dot(vAngle))
                    .add(v3.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v3.clone().multiply(2 * cAngle)));
            v4 = vAngle.clone()
                    .multiply(2 * v4.dot(vAngle))
                    .add(v4.clone().multiply(cAngleSq - vAngle.dot(vAngle)))
                    .add(vAngle.clone().crossProduct(v4.clone().multiply(2 * cAngle)));
        }

        pos.clone().add(v1).subtract(iPos).drawPos(vb).tex(u + uLength,           v + vLength).color(cR, cG, cB, alpha).endVertex();
        pos.clone().add(v2).subtract(iPos).drawPos(vb).tex(u + uLength, v).color(cR, cG, cB, alpha).endVertex();
        pos.clone().add(v3).subtract(iPos).drawPos(vb).tex(u, v          ).color(cR, cG, cB, alpha).endVertex();
        pos.clone().add(v4).subtract(iPos).drawPos(vb).tex(u,           v + vLength).color(cR, cG, cB, alpha).endVertex();
    }

    public static void renderTexturedCubeCentralColor(BufferContext buf, double size,
                                                                  double u, double v, double uLength, double vLength,
                                                                  float cR, float cG, float cB, float cA) {
        double half = size / 2D;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        buf.pos(-half, -half, -half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half,  half,  half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u, v).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u, v).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u, v).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).endVertex();

        buf.draw();
    }

    public static void renderTexturedCubeCentralWithLightAndColor(BufferContext buf, double size,
                                                                  double u, double v, double uLength, double vLength,
                                                                  int lX, int lY,
                                                                  float cR, float cG, float cB, float cA) {
        double half = size / 2D;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        buf.pos(-half, -half, -half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half, -half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half,  half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half,  half,  half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos( half, -half, -half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half, -half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half, -half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half, -half, -half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.pos(-half, -half,  half).tex(u, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos(-half,  half,  half).tex(u, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v + vLength).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v).lightmap(lX, lY).color(cR, cG, cB, cA).endVertex();

        buf.draw();
    }

    public static void renderAngleRotatedTexturedRectVB(BufferBuilder buf, Vector3 renderOffset, Vector3 axis, double angleRad, double scale, double u, double v, double uLength, double vLength, float r, float g, float b, float a) {
        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u,           v + vLength).color(r, g, b, a).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u + uLength, v + vLength).color(r, g, b, a).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u + uLength, v          ).color(r, g, b, a).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        vec.drawPos(buf).tex(u,           v          ).color(r, g, b, a).endVertex();
    }

}
