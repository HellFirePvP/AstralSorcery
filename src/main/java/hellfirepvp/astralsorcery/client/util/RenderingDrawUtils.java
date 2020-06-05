/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.MapStream;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingDrawUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:27
 */
public class RenderingDrawUtils {

    private static final Random rand = new Random();

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

        if (dropShadow) {
            fr.drawStringWithShadow(str, x, y, color);
        } else {
            fr.drawString(str, x, y, color);
        }

        if (!alphaTest) {
            GlStateManager.disableAlphaTest();
        }
    }

    public static void drawWithBlockLight(int lightLevel, Runnable run) {
        float brX = GLX.lastBrightnessX;
        float brY = GLX.lastBrightnessY;
        int lightCoord = lightLevel << 20 | lightLevel << 4;
        int x = lightCoord % 65536;
        int y = lightCoord / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, x, y);
        run.run();
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, brX, brY);
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
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX + uv01.getX(),   offsetY + uv01.getY(),   zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + uv11.getX(),   offsetY + uv11.getY(),   zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + uv10.getX(),   offsetY + uv10.getY(),   zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX + offset.getX(), offsetY + offset.getY(), zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    public static void renderBlueTooltipString(int x, int y, List<String> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        List<Tuple<ItemStack, ITextComponent>> stackTooltip = MapStream.ofValues(tooltipData, t -> ItemStack.EMPTY)
                .mapValue(tip -> (ITextComponent) new StringTextComponent(tip))
                .toTupleList();
        renderBlueTooltip(x, y, stackTooltip, fontRenderer, isFirstLineHeadline);
    }

    public static void renderBlueTooltipComponents(int x, int y, List<ITextComponent> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        List<Tuple<ItemStack, ITextComponent>> stackTooltip = MapStream.ofValues(tooltipData, t -> ItemStack.EMPTY).toTupleList();
        renderBlueTooltip(x, y, stackTooltip, fontRenderer, isFirstLineHeadline);
    }

    public static void renderBlueTooltip(int x, int y, List<Tuple<ItemStack, ITextComponent>> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        renderTooltip(x, y, tooltipData, 0xFF000027, 0xFF000044, Color.WHITE, fontRenderer, isFirstLineHeadline);
    }

    public static void renderBlueStackTooltip(int x, int y, List<Tuple<ItemStack, String>> tooltipData, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        List<Tuple<ItemStack, ITextComponent>> stackTooltip = MapStream.of(tooltipData)
                .mapValue(str -> (ITextComponent) new StringTextComponent(str))
                .toTupleList();
        renderTooltip(x, y, stackTooltip, 0xFF000027, 0xFF000044, Color.WHITE, fontRenderer, isFirstLineHeadline);
    }

    public static void renderTooltip(int x, int y, List<Tuple<ItemStack, ITextComponent>> tooltipData, int color, int colorFade, Color strColor, FontRenderer fontRenderer, boolean isFirstLineHeadline) {
        int stackBoxSize = 18;

        if (!tooltipData.isEmpty()) {
            boolean anyItemFound = false;

            int maxWidth = 0;
            for (Tuple<ItemStack, ITextComponent> toolTip : tooltipData) {
                FontRenderer customFR = toolTip.getA().getItem().getFontRenderer(toolTip.getA());
                if (customFR == null) {
                    customFR = fontRenderer;
                }
                int width = customFR.getStringWidth(toolTip.getB().getFormattedText());
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
            if (x + 15 + maxWidth > Minecraft.getInstance().mainWindow.getScaledWidth()) {
                x -= maxWidth + 24;
            }

            int formatWidth = anyItemFound ? maxWidth - stackBoxSize : maxWidth;
            List<Tuple<ItemStack, List<String>>> lengthLimitedToolTip = new LinkedList<>();
            for (Tuple<ItemStack, ITextComponent> toolTip : tooltipData) {
                FontRenderer customFR = toolTip.getA().getItem().getFontRenderer(toolTip.getA());
                if (customFR == null) {
                    customFR = fontRenderer;
                }
                lengthLimitedToolTip.add(new Tuple<>(toolTip.getA(), customFR.listFormattedStringToWidth(toolTip.getB().getFormattedText(), formatWidth)));
            }

            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 0;
            if (!lengthLimitedToolTip.isEmpty()) {
                if (lengthLimitedToolTip.size() > 1 && isFirstLineHeadline) {
                    sumLineHeight += 2;
                }
                Iterator<Tuple<ItemStack, List<String>>> iterator = lengthLimitedToolTip.iterator();
                while (iterator.hasNext()) {
                    Tuple<ItemStack, List<String>> toolTip = iterator.next();
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

            drawGradientRect(0, pX - 3,           pY - 4,                 pX + maxWidth + 3, pY - 3,                 color, colorFade);
            drawGradientRect(0, pX - 3,           pY + sumLineHeight + 3, pX + maxWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            drawGradientRect(0, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(0, pX - 4,           pY - 3,                 pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(0, pX + maxWidth + 3,pY - 3,                 pX + maxWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int col = (color & 0x00FFFFFF) | color & 0xFF000000;
            drawGradientRect(0, pX - 3,           pY - 3 + 1,             pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, col);
            drawGradientRect(0, pX + maxWidth + 2,pY - 3 + 1,             pX + maxWidth + 3, pY + sumLineHeight + 3 - 1, color, col);
            drawGradientRect(0, pX - 3,           pY - 3,                 pX + maxWidth + 3, pY - 3 + 1,                 col,   col);
            drawGradientRect(0, pX - 3,           pY + sumLineHeight + 2, pX + maxWidth + 3, pY + sumLineHeight + 3,     color, color);

            int offset = anyItemFound ? stackBoxSize : 0;

            boolean first = true;
            for (Tuple<ItemStack, List<String>> toolTip : lengthLimitedToolTip) {
                int minYShift = 10;
                if (!toolTip.getA().isEmpty()) {
                    RenderingUtils.renderItemStack(Minecraft.getInstance().getItemRenderer(), toolTip.getA(), pX, pY, null);
                    minYShift = stackBoxSize;
                    pY += 2;
                }
                for (String str : toolTip.getB()) {
                    FontRenderer customFR = toolTip.getA().getItem().getFontRenderer(toolTip.getA());
                    if (customFR == null) {
                        customFR = fontRenderer;
                    }
                    RenderingDrawUtils.renderStringAtPos(pX + offset, pY, customFR, str, strColor.getRGB(), false);
                    pY += 10;
                    minYShift -= 10;
                }
                if (minYShift > 0) {
                    pY += minYShift;
                }
                if (isFirstLineHeadline && first) {
                    pY += 2;
                }
                first = false;
            }
        }
    }

    public static void renderBlueTooltipBox(int x, int y, int width, int height) {
        renderTooltipBox(x, y, width, height, 0x000027, 0x000044);
    }

    public static void renderTooltipBox(int x, int y, int width, int height, int color, int colorFade) {
        int pX = x + 12;
        int pY = y - 12;

        drawGradientRect(0, pX - 3,           pY - 4,          pX + width + 3, pY - 3,         color, colorFade);
        drawGradientRect(0, pX - 3,           pY + height + 3, pX + width + 3, pY + height + 4, color, colorFade);
        drawGradientRect(0, pX - 3,           pY - 3,          pX + width + 3, pY + height + 3, color, colorFade);
        drawGradientRect(0, pX - 4,           pY - 3,          pX - 3,         pY + height + 3, color, colorFade);
        drawGradientRect(0, pX + width + 3,   pY - 3,          pX + width + 4, pY + height + 3, color, colorFade);

        int col = (color & 0x00FFFFFF) | color & 0xFF000000;
        drawGradientRect(0, pX - 3,           pY - 3 + 1,      pX - 3 + 1,     pY + height + 3 - 1, color, col);
        drawGradientRect(0, pX + width + 2,   pY - 3 + 1,      pX + width + 3, pY + height + 3 - 1, color, col);
        drawGradientRect(0, pX - 3,           pY - 3,          pX + width + 3, pY - 3 + 1,          col,   col);
        drawGradientRect(0, pX - 3,           pY + height + 2, pX + width + 3, pY + height + 3,     color, color);
    }

    public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
        float startRed   = (float) (startColor >> 16 & 255) / 255.0F;
        float startGreen = (float) (startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float) (startColor       & 255) / 255.0F;
        float endAlpha   = (float) (endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float) (endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float) (endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float) (endColor         & 255) / 255.0F;

        GlStateManager.disableTexture();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.pos(right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture();
    }

    public static void renderLightRayFan(double x, double y, double z, Color color, long seed, int minScale, float scale, int count) {
        rand.setSeed(seed);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();

        float f1 = ClientScheduler.getClientTick() / 400.0F;
        float f2 = 0.0F;

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableTexture();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Blending.ADDITIVE_ALPHA.applyStateManager();
        GlStateManager.depthMask(false);

        GlStateManager.pushMatrix();
        for (int i = 0; i < count; i++) {
            GlStateManager.rotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef(rand.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
            vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(minScale, 10 * scale) / 10.0F);
            vb.pos(0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), (int) (255.0F * (1.0F - f2))).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos( 0.7D * f4, fa,   -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos( 0.0D,      fa,    1.0F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(color.getRed(), color.getGreen(), color.getBlue(), 0).endVertex();
            tes.draw();
        }
        GlStateManager.popMatrix();

        GlStateManager.depthMask(true);
        Blending.DEFAULT.applyStateManager();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.enableTexture();
        RenderHelper.enableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    public static void renderFacingFullQuadVB(IVertexBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, int r, int g, int b, int alpha) {
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, 0F, 0F, 1F, 1F, r, g, b, alpha);
    }

    public static void renderFacingSpriteVB(IVertexBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, SpriteSheetResource sprite, long spriteTick, int r, int g, int b, int alpha) {
        Tuple<Float, Float> uv = sprite.getUVOffset(spriteTick);
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, uv.getA(), uv.getB(), sprite.getULength(), sprite.getVLength(), r, g, b, alpha);
    }

    public static void renderFacingQuadVB(IVertexBuilder vb, double px, double py, double pz, float partialTicks, float scale, float angle, float u, float v, float uLength, float vLength, int r, int g, int b, int alpha) {
        Vector3 pos = new Vector3(px, py, pz);

        RenderInfo ri = RenderInfo.getInstance();
        ActiveRenderInfo ari = ri.getARI();

        float arX =  ri.getRotationX();
        float arZ =  ri.getRotationZ();
        float arYZ = ri.getRotationYZ();
        float arXY = ri.getRotationXY();
        float arXZ = ri.getRotationXZ();

        Vec3d view = ari.getProjectedView();
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

        pos.clone().add(v1).subtract(iPos).drawPos(vb).color(r, g, b, alpha).tex(u + uLength, v + vLength).endVertex();
        pos.clone().add(v2).subtract(iPos).drawPos(vb).color(r, g, b, alpha).tex(u + uLength, v).endVertex();
        pos.clone().add(v3).subtract(iPos).drawPos(vb).color(r, g, b, alpha).tex(u, v ).endVertex();
        pos.clone().add(v4).subtract(iPos).drawPos(vb).color(r, g, b, alpha).tex(u, v + vLength).endVertex();
    }

    public static void renderTexturedCubeCentralColorLighted(IVertexBuilder buf,
                                                             float u, float v, float uLength, float vLength,
                                                             int r, int g, int b, int a,
                                                             int combinedLight) {

        buf.pos(-0.5, -0.5, -0.5).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5, -0.5, -0.5).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5, -0.5,  0.5).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(-0.5, -0.5,  0.5).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();

        buf.pos(-0.5,  0.5,  0.5).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5,  0.5,  0.5).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5,  0.5, -0.5).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(-0.5,  0.5, -0.5).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();

        buf.pos(-0.5, -0.5,  0.5).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(-0.5,  0.5,  0.5).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(-0.5,  0.5, -0.5).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(-0.5, -0.5, -0.5).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();

        buf.pos( 0.5, -0.5, -0.5).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5,  0.5, -0.5).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos( 0.5,  0.5,  0.5).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos( 0.5, -0.5,  0.5).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();

        buf.pos( 0.5, -0.5, -0.5).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos(-0.5, -0.5, -0.5).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos(-0.5,  0.5, -0.5).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos( 0.5,  0.5, -0.5).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();

        buf.pos(-0.5, -0.5,  0.5).color(r, g, b, a).tex(u, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5, -0.5,  0.5).color(r, g, b, a).tex(u + uLength, v).lightmap(combinedLight).endVertex();
        buf.pos( 0.5,  0.5,  0.5).color(r, g, b, a).tex(u + uLength, v + vLength).lightmap(combinedLight).endVertex();
        buf.pos(-0.5,  0.5,  0.5).color(r, g, b, a).tex(u, v + vLength).lightmap(combinedLight).endVertex();
    }

    public static void renderTexturedCubeCentralColorNormal(BufferBuilder buf, double size,
                                                            float u, float v, float uLength, float vLength,
                                                            float cR, float cG, float cB, float cA,
                                                            Vector3 normal) {
        double half = size / 2D;
        float nX = (float) normal.getX();
        float nY = (float) normal.getY();
        float nZ = (float) normal.getZ();

        buf.pos(-half, -half, -half).tex(u, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half, -half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();

        buf.pos(-half,  half,  half).tex(u, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();

        buf.pos(-half, -half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half,  half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half, -half, -half).tex(u, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();

        buf.pos( half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half,  half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half, -half,  half).tex(u, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();

        buf.pos( half, -half, -half).tex(u, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half, -half, -half).tex(u + uLength, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half,  half, -half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half,  half, -half).tex(u, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();

        buf.pos(-half, -half,  half).tex(u, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half, -half,  half).tex(u + uLength, v).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos( half,  half,  half).tex(u + uLength, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
        buf.pos(-half,  half,  half).tex(u, v + vLength).color(cR, cG, cB, cA).normal(nX, nY, nZ).endVertex();
    }

    public static void renderAngleRotatedTexturedRectVB(BufferBuilder buf, Vector3 renderOffset, Vector3 axis, double angleRad, double scale, float u, float v, float uLength, float vLength, float r, float g, float b, float a) {
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
