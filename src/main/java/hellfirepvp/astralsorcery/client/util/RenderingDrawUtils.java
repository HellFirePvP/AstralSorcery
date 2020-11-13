/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingDrawUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:27
 */
public class RenderingDrawUtils {

    private static final Random rand = new Random();
    private static final MatrixStack EMPTY = new MatrixStack();

    public static void renderStringCentered(@Nullable FontRenderer fr, MatrixStack renderStack, ITextProperties text, int x, int y, float scale, int color) {
        if (fr == null) {
            fr = Minecraft.getInstance().fontRenderer;
        }

        float strLength = fr.getStringPropertyWidth(text) * scale;
        float offsetLeft = x - strLength;

        renderStack.push();
        renderStack.translate(offsetLeft, y, 0);
        renderStack.scale(scale, scale, scale);
        renderStringAt(fr, renderStack, text, color);
        renderStack.pop();
    }

    public static float renderString(ITextProperties text) {
        return renderStringAt(text, EMPTY, Minecraft.getInstance().fontRenderer, Color.WHITE.getRGB(), false);
    }

    public static float renderString(IReorderingProcessor text) {
        return renderStringAt(text, EMPTY, Minecraft.getInstance().fontRenderer, Color.WHITE.getRGB(), false);
    }

    public static float renderString(ITextProperties text, int color) {
        return renderStringAt(text, EMPTY, Minecraft.getInstance().fontRenderer, color, false);
    }

    public static float renderString(IReorderingProcessor text, int color) {
        return renderStringAt(text, EMPTY, Minecraft.getInstance().fontRenderer, color, false);
    }

    public static float renderString(@Nullable FontRenderer fr, ITextProperties text, int color) {
        return renderStringAt(text, EMPTY, fr, color, false);
    }

    public static float renderString(@Nullable FontRenderer fr, IReorderingProcessor text, int color) {
        return renderStringAt(text, EMPTY, fr, color, false);
    }

    public static float renderStringAt(@Nullable FontRenderer fr, MatrixStack renderStack, ITextProperties text, int color) {
        return renderStringAt(text, renderStack, fr, color, true);
    }

    public static float renderStringAt(@Nullable FontRenderer fr, MatrixStack renderStack, IReorderingProcessor text, int color) {
        return renderStringAt(text, renderStack, fr, color, true);
    }

    public static float renderStringAt(ITextProperties text, MatrixStack renderStack, @Nullable FontRenderer fr, int color, boolean dropShadow) {
        return renderStringAt(LanguageMap.getInstance().func_241870_a(text), renderStack, fr, color, dropShadow);
    }

    public static float renderStringAt(IReorderingProcessor text, MatrixStack renderStack, @Nullable FontRenderer fr, int color, boolean dropShadow) {
        if (fr == null) {
            fr = Minecraft.getInstance().fontRenderer;
        }
        IRenderTypeBuffer.Impl buffer = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        int length = fr.func_238416_a_(text, 0, 0, color, dropShadow, renderStack.getLast().getMatrix(), buffer, false, 0, LightmapUtil.getPackedFullbrightCoords());
        buffer.finish();
        return length;
    }

    public static Rectangle drawInfoStar(MatrixStack renderStack, IDrawRenderTypeBuffer buffer, float widthHeightBase, float pTicks) {
        IVertexBuilder vb = buffer.getBuffer(RenderTypesAS.GUI_MISC_INFO_STAR);

        float tick = ClientScheduler.getClientTick() + pTicks;
        float deg = (tick * 2) % 360F;
        float wh = widthHeightBase - (widthHeightBase / 6F) * (MathHelper.sin((float) Math.toRadians(((tick) * 4) % 360F)) + 1F);
        drawInfoStarSingle(renderStack, vb, wh, Math.toRadians(deg));

        deg = ((tick + 22.5F) * 2) % 360F;
        wh = widthHeightBase - (widthHeightBase / 6F) * (MathHelper.sin((float) Math.toRadians(((tick + 45F) * 4) % 360F)) + 1F);
        drawInfoStarSingle(renderStack, vb, wh, Math.toRadians(deg));

        buffer.draw(RenderTypesAS.GUI_MISC_INFO_STAR);
        return new Rectangle(MathHelper.floor(-widthHeightBase / 2F), MathHelper.floor(-widthHeightBase / 2F),
                MathHelper.floor(widthHeightBase), MathHelper.floor(widthHeightBase));
    }

    private static void drawInfoStarSingle(MatrixStack renderStack, IVertexBuilder vb, float widthHeight, double deg) {
        Vector3 offset = new Vector3(-widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv01   = new Vector3(-widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv11   = new Vector3( widthHeight / 2D,  widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);
        Vector3 uv10   = new Vector3( widthHeight / 2D, -widthHeight / 2D, 0).rotate(deg, Vector3.RotAxis.Z_AXIS);

        Matrix4f matr = renderStack.getLast().getMatrix();
        vb.pos(matr, (float) uv01.getX(),   (float) uv01.getY(),   0).tex(0, 1).endVertex();
        vb.pos(matr, (float) uv11.getX(),   (float) uv11.getY(),   0).tex(1, 1).endVertex();
        vb.pos(matr, (float) uv10.getX(),   (float) uv10.getY(),   0).tex(1, 0).endVertex();
        vb.pos(matr, (float) offset.getX(), (float) offset.getY(), 0).tex(0, 0).endVertex();
    }

    public static void renderBlueTooltipComponents(MatrixStack renderStack, float x, float y, float zLevel,
                                                   List<ITextProperties> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        List<Tuple<ItemStack, ITextProperties>> stackTooltip = MapStream.ofValues(tooltipData, t -> ItemStack.EMPTY).toTupleList();
        renderBlueTooltip(renderStack, x, y, zLevel, stackTooltip, fontRenderer, isFirstLineHeadline);
    }

    public static void renderBlueTooltip(MatrixStack renderStack, float x, float y, float zLevel,
                                         List<Tuple<ItemStack, ITextProperties>> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        renderTooltip(renderStack, x, y, zLevel, tooltipData, fontRenderer, isFirstLineHeadline, 0xFF000027, 0xFF000044, Color.WHITE);
    }

    public static void renderTooltip(MatrixStack renderStack, float x, float y, float zLevel,
                                     List<Tuple<ItemStack, ITextProperties>> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline,
                                     int color, int colorFade, Color strColor) {
        int stackBoxSize = 18;

        if (!tooltipData.isEmpty()) {
            boolean anyItemFound = false;

            int maxWidth = 0;
            for (Tuple<ItemStack, ITextProperties> toolTip : tooltipData) {
                FontRenderer customFR = toolTip.getA().getItem().getFontRenderer(toolTip.getA());
                if (customFR == null) {
                    customFR = fontRenderer;
                }
                int width = customFR.getStringPropertyWidth(toolTip.getB());
                if (!toolTip.getA().isEmpty()) {
                    anyItemFound = true;
                }
                if (anyItemFound) {
                    width += stackBoxSize;
                }
                if (width > maxWidth) {
                    maxWidth = width;
                }
            }
            if (x + 15 + maxWidth > Minecraft.getInstance().getMainWindow().getScaledWidth()) {
                x -= maxWidth + 24;
            }

            int formatWidth = anyItemFound ? maxWidth - stackBoxSize : maxWidth;
            List<Tuple<ItemStack, List<IReorderingProcessor>>> lengthLimitedToolTip = new LinkedList<>();
            for (Tuple<ItemStack, ITextProperties> toolTip : tooltipData) {
                FontRenderer customFR = toolTip.getA().getItem().getFontRenderer(toolTip.getA());
                if (customFR == null) {
                    customFR = fontRenderer;
                }

                List<IReorderingProcessor> textLines = customFR.trimStringToWidth(toolTip.getB(), formatWidth);
                if (textLines.isEmpty()) {
                    textLines = Collections.singletonList(IReorderingProcessor.field_242232_a);
                }
                lengthLimitedToolTip.add(new Tuple<>(toolTip.getA(), textLines));
            }

            float pX = x + 12;
            float pY = y - 12;
            int sumLineHeight = 0;
            if (!lengthLimitedToolTip.isEmpty()) {
                if (lengthLimitedToolTip.size() > 1 && isFirstLineHeadline) {
                    sumLineHeight += 2;
                }
                Iterator<Tuple<ItemStack, List<IReorderingProcessor>>> iterator = lengthLimitedToolTip.iterator();
                while (iterator.hasNext()) {
                    Tuple<ItemStack, List<IReorderingProcessor>> toolTip = iterator.next();
                    int segmentHeight = 0;
                    if (!toolTip.getA().isEmpty()) {
                        segmentHeight += 2;
                        segmentHeight += stackBoxSize;
                        segmentHeight += (Math.max(toolTip.getB().size() - 1, 0)) * 10;
                    } else {
                        segmentHeight += toolTip.getB().size() * 10;
                    }
                    if (!iterator.hasNext()) {
                        segmentHeight -= 2;
                    }
                    sumLineHeight += segmentHeight;
                }
            }

            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 4,                 pX + maxWidth + 3, pY - 3,                 color, colorFade);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY + sumLineHeight + 3, pX + maxWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(renderStack, zLevel, pX - 4,           pY - 3,                 pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(renderStack, zLevel, pX + maxWidth + 3,pY - 3,                 pX + maxWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int col = (color & 0x00FFFFFF) | color & 0xFF000000;
            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 3 + 1,             pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, col);
            drawGradientRect(renderStack, zLevel, pX + maxWidth + 2,pY - 3 + 1,             pX + maxWidth + 3, pY + sumLineHeight + 3 - 1, color, col);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY - 3 + 1,                 col,   col);
            drawGradientRect(renderStack, zLevel, pX - 3,           pY + sumLineHeight + 2, pX + maxWidth + 3, pY + sumLineHeight + 3,     color, color);

            int offset = anyItemFound ? stackBoxSize : 0;

            renderStack.push();
            renderStack.translate(pX, pY, 0);
            boolean first = true;
            for (Tuple<ItemStack, List<IReorderingProcessor>> toolTip : lengthLimitedToolTip) {
                int minYShift = 10;
                if (!toolTip.getA().isEmpty()) {
                    renderStack.push();
                    renderStack.translate(0, 0, zLevel);
                    RenderingUtils.renderItemStackGUI(renderStack, toolTip.getA(), null);
                    renderStack.pop();

                    minYShift = stackBoxSize;
                    renderStack.translate(0, 2, 0);
                }
                for (IReorderingProcessor text : toolTip.getB()) {
                    FontRenderer customFR = toolTip.getA().getItem().getFontRenderer(toolTip.getA());
                    if (customFR == null) {
                        customFR = fontRenderer;
                    }
                    renderStack.push();
                    renderStack.translate(offset, 0, zLevel);
                    renderStringAt(text, renderStack, customFR, strColor.getRGB(), false);
                    renderStack.pop();

                    renderStack.translate(0, 10, 0);
                    minYShift -= 10;
                }
                if (minYShift > 0) {
                    renderStack.translate(0, minYShift, 0);
                }
                if (isFirstLineHeadline && first) {
                    renderStack.translate(0, 2, 0);
                }
                first = false;
            }
            renderStack.pop();
        }
    }

    public static void renderBlueTooltipBox(MatrixStack renderStack, int x, int y, int width, int height) {
        renderTooltipBox(renderStack, x, y, width, height, 0x000027, 0x000044);
    }

    public static void renderTooltipBox(MatrixStack renderStack, int x, int y, int width, int height, int color, int colorFade) {
        int pX = x + 12;
        int pY = y - 12;

        drawGradientRect(renderStack, 0, pX - 3,           pY - 4,          pX + width + 3, pY - 3,         color, colorFade);
        drawGradientRect(renderStack, 0, pX - 3,           pY + height + 3, pX + width + 3, pY + height + 4, color, colorFade);
        drawGradientRect(renderStack, 0, pX - 3,           pY - 3,          pX + width + 3, pY + height + 3, color, colorFade);
        drawGradientRect(renderStack, 0, pX - 4,           pY - 3,          pX - 3,         pY + height + 3, color, colorFade);
        drawGradientRect(renderStack, 0, pX + width + 3,   pY - 3,          pX + width + 4, pY + height + 3, color, colorFade);

        int col = (color & 0x00FFFFFF) | color & 0xFF000000;
        drawGradientRect(renderStack, 0, pX - 3,           pY - 3 + 1,      pX - 3 + 1,     pY + height + 3 - 1, color, col);
        drawGradientRect(renderStack, 0, pX + width + 2,   pY - 3 + 1,      pX + width + 3, pY + height + 3 - 1, color, col);
        drawGradientRect(renderStack, 0, pX - 3,           pY - 3,          pX + width + 3, pY - 3 + 1,          col,   col);
        drawGradientRect(renderStack, 0, pX - 3,           pY + height + 2, pX + width + 3, pY + height + 3,     color, color);
    }

    public static void drawGradientRect(MatrixStack renderStack, float zLevel, float left, float top, float right, float bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed   = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float) (startColor       & 255) / 255.0F;
        float endAlpha   = (float) (endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float) (endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float) (endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float) (endColor         & 255) / 255.0F;

        RenderSystem.disableTexture();
        Blending.DEFAULT.apply();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR, buf -> {
            Matrix4f offset = renderStack.getLast().getMatrix();
            buf.pos(offset, right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            buf.pos(offset,  left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
            buf.pos(offset,  left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
            buf.pos(offset, right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        });

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.enableTexture();
    }

    public static void renderLightRayFan(MatrixStack renderStack, IRenderTypeBuffer buffer, Color color, long seed, int minScale, float scale, int count) {
        rand.setSeed(seed);

        float f1 = ClientScheduler.getClientTick() / 400.0F;
        float f2 = 0.0F;
        int alpha = (int) (255.0F * (1.0F - f2));

        IVertexBuilder vb = buffer.getBuffer(RenderTypesAS.EFFECT_LIGHTRAY_FAN);

        renderStack.push();
        for (int i = 0; i < count; i++) {
            renderStack.push();
            renderStack.rotate(Vector3f.XP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.rotate(Vector3f.YP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.rotate(Vector3f.ZP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.rotate(Vector3f.XP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.rotate(Vector3f.YP.rotationDegrees(rand.nextFloat() * 360.0F));
            renderStack.rotate(Vector3f.ZP.rotationDegrees(rand.nextFloat() * 360.0F + f1 * 360.0F));
            Matrix4f matr = renderStack.getLast().getMatrix();

            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);

            vb.pos(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.pos(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.pos(matr, -0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos(matr,  0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos(matr, 0F,     0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.pos(matr, 0F,     0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.pos(matr, 0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos(matr, 0F,        fa,    1F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.pos(matr, 0F,      0F, 0F)        .color(color.getRed(), color.getGreen(), color.getBlue(), alpha).endVertex();
            vb.pos(matr, 0F,         fa,    1F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos(matr, -0.7F * f4, fa, -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();

            renderStack.pop();
        }
        renderStack.pop();

        RenderingUtils.refreshDrawing(vb, RenderTypesAS.EFFECT_LIGHTRAY_FAN);
    }

    public static void renderFacingFullQuadVB(IVertexBuilder vb, MatrixStack renderStack, double px, double py, double pz, float scale, float angle, int r, int g, int b, int alpha) {
        renderFacingQuadVB(vb, renderStack, px, py, pz, scale, angle, 0F, 0F, 1F, 1F, r, g, b, alpha);
    }

    public static void renderFacingSpriteVB(IVertexBuilder vb, MatrixStack renderStack, double px, double py, double pz, float scale, float angle, SpriteSheetResource sprite, long spriteTick, int r, int g, int b, int alpha) {
        Tuple<Float, Float> uv = sprite.getUVOffset(spriteTick);
        renderFacingQuadVB(vb, renderStack, px, py, pz, scale, angle, uv.getA(), uv.getB(), sprite.getULength(), sprite.getVLength(), r, g, b, alpha);
    }

    public static void renderFacingQuadVB(IVertexBuilder vb, MatrixStack renderStack, double px, double py, double pz, float scale, float angle, float u, float v, float uLength, float vLength, int r, int g, int b, int alpha) {
        Vector3 pos = new Vector3(px, py, pz);

        RenderInfo ri = RenderInfo.getInstance();
        ActiveRenderInfo ari = ri.getARI();

        float arX =  ri.getRotationX();
        float arZ =  ri.getRotationZ();
        float arYZ = ri.getRotationYZ();
        float arXY = ri.getRotationXY();
        float arXZ = ri.getRotationXZ();

        Vector3d view = ari.getProjectedView();
        Vector3f look = ari.getViewVector();

        Vector3 iPos = new Vector3(view);
        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            float cAngle = MathHelper.cos(angle * 0.5F);
            float cAngleSq = cAngle * cAngle;

            Vector3 vAngle = new Vector3(
                    MathHelper.sin(angle * 0.5F) * look.getX(),
                    MathHelper.sin(angle * 0.5F) * look.getY(),
                    MathHelper.sin(angle * 0.5F) * look.getZ());

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

        Matrix4f matr = renderStack.getLast().getMatrix();
        pos.clone().add(v1).subtract(iPos).drawPos(matr, vb).color(r, g, b, alpha).tex(u + uLength, v + vLength).endVertex();
        pos.clone().add(v2).subtract(iPos).drawPos(matr, vb).color(r, g, b, alpha).tex(u + uLength, v).endVertex();
        pos.clone().add(v3).subtract(iPos).drawPos(matr, vb).color(r, g, b, alpha).tex(u, v ).endVertex();
        pos.clone().add(v4).subtract(iPos).drawPos(matr, vb).color(r, g, b, alpha).tex(u, v + vLength).endVertex();
    }

    public static void renderTexturedCubeCentralColorLighted(IVertexBuilder buf, MatrixStack renderStack,
                                                             float u, float v, float uLength, float vLength,
                                                             int r, int g, int b, int a,
                                                             int combinedLight) {

        Matrix4f matr = renderStack.getLast().getMatrix();

        buf.pos(matr, -0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();

        buf.pos(matr, -0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();

        buf.pos(matr, -0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();

        buf.pos(matr,  0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();

        buf.pos(matr,  0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();

        buf.pos(matr, -0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(matr,  0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(matr, -0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();
    }

    public static void renderTexturedCubeCentralColorNormal(MatrixStack renderStack, IVertexBuilder vb,
                                                            float u, float v, float uLength, float vLength,
                                                            int r, int g, int b, int a,
                                                            Matrix3f normalMatr) {

        Matrix4f offset = renderStack.getLast().getMatrix();
        vb.pos(offset, -0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();

        vb.pos(offset, -0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();

        vb.pos(offset, -0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u, v).normal(normalMatr, 0, 0, 0).endVertex();

        vb.pos(offset,  0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u, v).normal(normalMatr, 0, 0, 0).endVertex();

        vb.pos(offset,  0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F, -0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F,  0.5F, -0.5F).color(r, g, b, a).tex(u, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();

        vb.pos(offset, -0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F, -0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset,  0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u + uLength, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
        vb.pos(offset, -0.5F,  0.5F,  0.5F).color(r, g, b, a).tex(u, v + vLength).normal(normalMatr, 0, 0, 0).endVertex();
    }

    public static void renderAngleRotatedTexturedRectVB(IVertexBuilder vb, MatrixStack renderStack, Vector3 renderOffset, Vector3 axis, float angleRad, float scale, float u, float v, float uLength, float vLength, int r, int g, int b, int a) {
        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();
        Matrix4f matr = renderStack.getLast().getMatrix();

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).tex(u, v + vLength).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).tex(u + uLength, v + vLength).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).tex(u + uLength, v).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        vec.drawPos(matr, vb).color(r, g, b, a).tex(u, v).endVertex();
    }

}
