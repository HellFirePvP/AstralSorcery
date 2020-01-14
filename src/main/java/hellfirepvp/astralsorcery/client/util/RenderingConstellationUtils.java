/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.ActiveCelestialsHandler;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingConstellationUtils
 * Created by HellFirePvP
 * Date: 02.08.2019 / 21:27
 */
public class RenderingConstellationUtils {

    public static void renderConstellationSky(IConstellation c, ActiveCelestialsHandler.RenderPosition renderPos, Supplier<Float> brightnessFn) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vb = tessellator.getBuffer();

        Vector3 renderOffset = renderPos.offset;
        Color rC = c.getTierRenderColor();
        float r = rC.getRed() / 255F;
        float g = rC.getGreen() / 255F;
        float b = rC.getBlue() / 255F;

        //Now we build from the exact UV vectors a 31x31 grid and render the stars & connections.
        Vector3 dirU = renderPos.incU.clone().subtract(renderOffset).divide(31);
        Vector3 dirV = renderPos.incV.clone().subtract(renderOffset).divide(31);
        double uLength = dirU.length();
        TexturesAS.TEX_STAR_CONNECTION.bindTexture();
        for (int j = 0; j < 2; j++) {
            for (StarConnection con : c.getStarConnections()) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                GlStateManager.color4f(r, g, b, Math.max(0, brightnessFn.get()));
                Vector3 vecA = renderOffset.clone().add(dirU.clone().multiply(con.from.x + 1)).add(dirV.clone().multiply(con.from.y + 1));
                Vector3 vecB = renderOffset.clone().add(dirU.clone().multiply(con.to.x + 1)).add(dirV.clone().multiply(con.to.y + 1));
                Vector3 vecCV = vecB.subtract(vecA);
                Vector3 oPane = dirV.clone().crossProduct(vecCV);
                Vector3 vecAD = oPane.clone().crossProduct(vecCV).normalize().multiply(uLength);
                Vector3 offset00 = vecA.subtract(vecAD.clone().multiply(j == 0 ? 1 : -1));
                Vector3 vecU = vecAD.clone().multiply(j == 0 ? 2 : -2);

                for (int i = 0; i < 4; i++) {
                    Vector3 pos = offset00.clone().add(vecU.clone().multiply(((i + 1) & 2) >> 1)).add(vecCV.clone().multiply(((i + 2) & 2) >> 1));
                    vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(((i + 2) & 2) >> 1, ((i + 3) & 2) >> 1).endVertex();
                }
                tessellator.draw();
            }
        }

        TexturesAS.TEX_STAR_1.bindTexture();
        for (StarLocation star : c.getStars()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            GlStateManager.color4f(r, g, b, Math.max(0, brightnessFn.get()));
            int x = star.x;
            int y = star.y;
            Vector3 ofStar = renderOffset.clone().add(dirU.clone().multiply(x)).add(dirV.clone().multiply(y));
            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;
                Vector3 pos = ofStar.clone().add(dirU.clone().multiply(u << 1)).add(dirV.clone().multiply(v << 1));
                vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
            }
            tessellator.draw();
        }
    }

    public static void renderConstellationIntoWorldFlat(IConstellation c, Vector3 offset, double scale, double line, float brightness) {
        renderConstellationIntoWorldFlat(c.getConstellationColor(), c, offset, scale, line, brightness);
    }

    public static void renderConstellationIntoWorldFlat(Color color, IConstellation c, Vector3 offset, double scale, double line, float brightness) {
        Vector3 thisOffset = offset.clone();
        double starSize = 1D / ((double) IConstellation.STAR_GRID_SIZE) * scale;
        float fRed   = ((float) color.getRed()) / 255F;
        float fGreen = ((float) color.getGreen()) / 255F;
        float fBlue  = ((float) color.getBlue()) / 255F;
        float fAlpha = brightness * 0.8F;

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.translated(-15.5D * starSize, 0, -15.5D * starSize);

        TexturesAS.TEX_STAR_CONNECTION.bindTexture();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (StarConnection sc : c.getStarConnections()) {
            thisOffset.addY(0.001);

            Vector3 starOffset = thisOffset.clone().addX(sc.from.x * starSize).addZ(sc.from.y * starSize);
            Vector3 dirU = new Vector3(sc.to.x, 0, sc.to.y).subtract(sc.from.x, 0, sc.from.y).multiply(starSize);
            Vector3 dirV = dirU.clone().crossProduct(new Vector3(0, 1, 0)).setY(0).normalize().multiply(line * starSize);
            Vector3 offsetRender = starOffset.subtract(dirV.clone().divide(2));

            Vector3 pos = offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(1));
            pos.drawPos(buf).tex(1, 0).color(fRed, fGreen, fBlue, fAlpha).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(1));
            pos.drawPos(buf).tex(0, 0).color(fRed, fGreen, fBlue, fAlpha).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(0));
            pos.drawPos(buf).tex(0, 1).color(fRed, fGreen, fBlue, fAlpha).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(0));
            pos.drawPos(buf).tex(1, 1).color(fRed, fGreen, fBlue, fAlpha).endVertex();

        }
        tes.draw();

        TexturesAS.TEX_STAR_1.bindTexture();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (StarLocation sl : c.getStars()) {

            Vector3 offsetRender = thisOffset.clone().add(sl.x * starSize - starSize, 0.005, sl.y * starSize - starSize);
            Vector3 dirU = new Vector3(starSize * 2, 0, 0);
            Vector3 dirV = new Vector3(0, 0, starSize * 2);

            Vector3 pos = offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(1));
            pos.drawPos(buf).tex(1, 0).color(fRed, fGreen, fBlue, brightness).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(1));
            pos.drawPos(buf).tex(0, 0).color(fRed, fGreen, fBlue, brightness).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(0));
            pos.drawPos(buf).tex(0, 1).color(fRed, fGreen, fBlue, brightness).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(0));
            pos.drawPos(buf).tex(1, 1).color(fRed, fGreen, fBlue, brightness).endVertex();
        }
        tes.draw();

        GlStateManager.popMatrix();
    }

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(IConstellation c, int offsetX, int offsetY, float zLevel, int width, int height, double linebreadth, Supplier<Float> brightness, boolean isKnown, boolean applyStarBrightness) {
        return renderConstellationIntoGUI(c.getTierRenderColor(), c, offsetX, offsetY, zLevel, width, height, linebreadth, brightness, isKnown, applyStarBrightness);
    }

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(Color col, IConstellation c, int offsetX, int offsetY, float zLevel, int width, int height, double linebreadth, Supplier<Float> brightness, boolean isKnown, boolean applyStarBrightness) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        double ulength = ((double) width) / IConstellation.STAR_GRID_SIZE;
        double vlength = ((double) height) / IConstellation.STAR_GRID_SIZE;

        float r = col.getRed() / 255F;
        float g = col.getGreen() / 255F;
        float b = col.getBlue() / 255F;

        Vector3 offsetVec = new Vector3(offsetX, offsetY, zLevel);

        float starBrightness = 1F;
        if (applyStarBrightness && Minecraft.getInstance().world != null) {
            starBrightness = Minecraft.getInstance().world.getStarBrightness(1.0F);
            if (starBrightness <= 0.23F) {
                return new HashMap<>();
            }
            starBrightness *= 2;
        }

        TexturesAS.TEX_STAR_CONNECTION.bindTexture();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        if (isKnown) {
            for (int j = 0; j < 2; j++) {
                for (StarConnection sc : c.getStarConnections()) {
                    float alpha = brightness.get() * starBrightness;

                    Vector3 fromStar = new Vector3(offsetVec.getX() + sc.from.x * ulength, offsetVec.getY() + sc.from.y * vlength, offsetVec.getZ());
                    Vector3 toStar = new Vector3(offsetVec.getX() + sc.to.x * ulength, offsetVec.getY() + sc.to.y * vlength, offsetVec.getZ());

                    Vector3 dir = toStar.clone().subtract(fromStar);
                    Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(linebreadth);

                    Vector3 vec00 = fromStar.clone().add(degLot);
                    Vector3 vecV = degLot.clone().multiply(-2);

                    for (int i = 0; i < 4; i++) {
                        int u = ((i + 1) & 2) >> 1;
                        int v = ((i + 2) & 2) >> 1;

                        Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
                        vb.pos(pos.getX(), pos.getY(), pos.getZ())
                                .tex(u, v)
                                .color(r, g, b, alpha)
                                .endVertex();
                    }
                }
            }
        }
        tes.draw();

        Map<StarLocation, Rectangle> starRectangles = new HashMap<>();

        TexturesAS.TEX_STAR_1.bindTexture();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (StarLocation sl : c.getStars()) {

            float alpha = brightness.get() * starBrightness;

            int starX = sl.x;
            int starY = sl.y;

            Vector3 starVec = offsetVec.clone().addX(starX * ulength - ulength).addY(starY * vlength - vlength);
            Point upperLeft = new Point(starVec.getBlockX(), starVec.getBlockY());

            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;

                Vector3 pos = starVec.clone().addX(ulength * u * 2).addY(vlength * v * 2);
                vb.pos(pos.getX(), pos.getY(), pos.getZ())
                        .tex(u, v)
                        .color(isKnown ? r : alpha,
                                isKnown ? g : alpha,
                                isKnown ? b : alpha,
                                Math.min(alpha * 1.5F, 1F))
                        .endVertex();
            }

            starRectangles.put(sl, new Rectangle(upperLeft.x, upperLeft.y, (int) (ulength * 2), (int) (vlength * 2)));
        }
        tes.draw();

        return starRectangles;
    }

    public static float stdFlicker(long wtime, float partialTicks, int divisor) {
        return flickerSin(wtime, partialTicks, divisor, 2F, 0.5F);
    }

    public static float conCFlicker(long wtime, float partialTicks, int divisor) {
        return flickerSin(wtime, partialTicks, divisor, 4F, 0.375F);
    }

    private static float flickerSin(long wtime, float partialTicks, double divisor, float div, float move) {
        double rad = ((wtime % (GeneralConfig.CONFIG.dayLength.get() / 2)) + partialTicks) / divisor;
        float sin = MathHelper.sin((float) rad);
        return (sin / div) + move;
    }

}
