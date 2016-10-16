package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.sky.RenderAstralSkybox;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.Tier;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderConstellation
 * Created by HellFirePvP
 * Date: 07.05.2016 / 17:38
 */
public class RenderConstellation {

    @SideOnly(Side.CLIENT)
    public static void renderConstellation(Tier tier, Constellation c, Vector3 renderOffset, BrightnessFunction brFunc) {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vb = tessellator.getBuffer();

        Tier.RInformation renderInfo = tier.getRenderInformation();
        Color rC = tier.calcRenderColor();

        //Now we build from the exact UV vectors a 32x32 grid and render the stars & connections.
        Vector3 dirU = renderInfo.incU.clone().subtract(renderOffset).divide(32);
        Vector3 dirV = renderInfo.incV.clone().subtract(renderOffset).divide(32);
        double uLength = dirU.length();
        RenderAstralSkybox.TEX_CONNECTION.bind();
        for (int j = 0; j < 2; j++) {
            for (StarConnection con : c.getConnections()) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                float brightness = brFunc.getBrightness();
                GlStateManager.color(((float) rC.getRed()) / 255F, ((float) rC.getGreen()) / 255F, ((float) rC.getBlue()) / 255F,
                        brightness < 0 ? 0 : brightness);
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

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarLocation star : c.getStars()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            float brightness = brFunc.getBrightness();
            GlStateManager.color(((float) rC.getRed()) / 255F, ((float) rC.getGreen()) / 255F, ((float) rC.getBlue()) / 255F,
                    brightness < 0 ? 0 : brightness);
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

    //non-rotating, builds into x/z space
    public static void renderConstellationIntoWorldFlat(Constellation c, Color rC, Vector3 offsetPos, double scale, double lineBreadth, float br) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        double s = 1D / 32D * scale;

        GL11.glTranslated(-16 * s, 0, -16 * s);

        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();

        RenderAstralSkybox.TEX_CONNECTION.bind();
        for (StarConnection sc : c.getConnections()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            float brightness = br;
            brightness *= 0.8;
            GlStateManager.color(((float) rC.getRed()) / 255F, ((float) rC.getGreen()) / 255F, ((float) rC.getBlue()) / 255F,
                    brightness < 0 ? 0 : brightness);

            Vector3 offset = offsetPos.clone().addX(sc.from.x * s).addZ(sc.from.y * s);
            Vector3 dirU = new Vector3(sc.to.x, 0, sc.to.y).subtract(sc.from.x, 0, sc.from.y).multiply(s);
            Vector3 dirV = dirU.clone().crossProduct(new Vector3(0, 1, 0)).normalize().multiply(lineBreadth * s);
            Vector3 offsetRender = offset.subtract(dirV.clone().divide(2));

            Vector3 pos = offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(1));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(1, 0).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(1));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(0, 0).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(0));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(0, 1).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(0));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(1, 1).endVertex();

            tes.draw();
        }

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarLocation sl : c.getStars()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            GlStateManager.color(((float) rC.getRed()) / 255F, ((float) rC.getGreen()) / 255F, ((float) rC.getBlue()) / 255F,
                    br < 0 ? 0 : br);

            Vector3 offsetRender = offsetPos.clone().add(sl.x * s - s, 0.01, sl.y * s - s);
            Vector3 dirU = new Vector3(s * 2, 0, 0);
            Vector3 dirV = new Vector3(0, 0, s * 2);

            Vector3 pos = offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(1));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(1, 0).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(1));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(0, 0).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(0));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(0, 1).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(0));
            vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(1, 1).endVertex();

            tes.draw();
        }

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /*public static void renderConstellationIntoWorld(Constellation c, Color rC, Vector3 offsetPos, double lineBreadth, BrightnessFunction func) {
        GL11.glPushMatrix();
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        Vector3 dirV = new Vector3(0, 0, 1);
        Vector3 dirU = new Vector3(1, 0, 0);

        RenderAstralSkybox.TEX_CONNECTION.bind();
        for (int j = 0; j < 2; j++) {
            for (StarConnection con : c.getConnections()) {
                vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                float brightness = func.getBrightness();
                GlStateManager.color(((float) rC.getRed()) / 255F, ((float) rC.getGreen()) / 255F, ((float) rC.getBlue()) / 255F,
                        brightness < 0 ? 0 : brightness);

                Vector3 vecA = offsetPos.clone().add(dirU.clone().multiply(con.from.x + 1)).add(dirV.clone().multiply(con.from.y + 1));
                Vector3 vecB = offsetPos.clone().add(dirU.clone().multiply(con.to.x + 1)).add(dirV.clone().multiply(con.to.y + 1));
                Vector3 vecCV = vecB.subtract(vecA);
                Vector3 oPane = dirV.clone().crossProduct(vecCV);
                Vector3 vecAD = oPane.clone().crossProduct(vecCV).normalize().multiply(lineBreadth);
                Vector3 offset00 = vecA.subtract(vecAD.clone().multiply(j == 0 ? 1 : -1));
                Vector3 vecU = vecAD.clone().multiply(j == 0 ? 2 : -2);

                for (int i = 0; i < 4; i++) {
                    Vector3 pos = offset00.clone().add(vecU.clone().multiply(((i + 1) & 2) >> 1)).add(vecCV.clone().multiply(((i + 2) & 2) >> 1));
                    vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(((i + 2) & 2) >> 1, ((i + 3) & 2) >> 1).endVertex();
                }
                tes.draw();
            }
        }

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarLocation star : c.getStars()) {
            vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            float brightness = func.getBrightness();
            GlStateManager.color(((float) rC.getRed()) / 255F, ((float) rC.getGreen()) / 255F, ((float) rC.getBlue()) / 255F,
                    brightness < 0 ? 0 : brightness);
            int x = star.x;
            int y = star.y;
            Vector3 ofStar = offsetPos.clone().add(dirU.clone().multiply(x)).add(dirV.clone().multiply(y));
            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;
                Vector3 pos = ofStar.clone().add(dirU.clone().multiply(u << 1)).add(dirV.clone().multiply(v << 1));
                vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
            }
            tes.draw();
        }
        GL11.glPopMatrix();
    }*/

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(Constellation c, int offsetX, int offsetY, float zLevel, int width, int height, double linebreadth, BrightnessFunction func, boolean isKnown, boolean applyStarBrightness) {
        return renderConstellationIntoGUI(c.queryTier(), c, offsetX, offsetY, zLevel, width, height, linebreadth, func, isKnown, applyStarBrightness);
    }

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(Tier tier, Constellation c, int offsetX, int offsetY, float zLevel, int width, int height, double linebreadth, BrightnessFunction func, boolean isKnown, boolean applyStarBrightness) {
        return renderConstellationIntoGUI(tier.calcRenderColor(), c, offsetX, offsetY, zLevel, width, height, linebreadth, func, isKnown, applyStarBrightness);
    }

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(Color col, Constellation c, int offsetX, int offsetY, float zLevel, int width, int height, double linebreadth, BrightnessFunction func, boolean isKnown, boolean applyStarBrightness) {
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        double ulength = ((double) width) / 32;
        double vlength = ((double) height) / 32;

        Vector3 offsetVec = new Vector3(offsetX, offsetY, zLevel);
        RenderAstralSkybox.TEX_CONNECTION.bind();
        if (isKnown) {
            for (int j = 0; j < 2; j++) {
                for (StarConnection sc : c.getConnections()) {
                    float brightness = func.getBrightness();
                    if (applyStarBrightness) {
                        float starBr = Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F);
                        if (starBr <= 0.23F) {
                            continue;
                        }
                        brightness *= (starBr * 2);
                    }
                    vb.begin(7, DefaultVertexFormats.POSITION_TEX);
                    GlStateManager.color(((float) col.getRed()) / 255F, ((float) col.getGreen()) / 255F, ((float) col.getBlue()) / 255F, brightness < 0 ? 0 : brightness);
                    Vector3 fromStar = new Vector3(offsetVec.getX() + sc.from.x * ulength, offsetVec.getY() + sc.from.y * vlength, offsetVec.getZ());
                    Vector3 toStar = new Vector3(offsetVec.getX() + sc.to.x * ulength, offsetVec.getY() + sc.to.y * vlength, offsetVec.getZ());

                    Vector3 dir = toStar.clone().subtract(fromStar);
                    Vector3 degLot = dir.clone().crossProduct(new Vector3(0, 0, 1)).normalize().multiply(linebreadth);//.multiply(j == 0 ? 1 : -1);

                    Vector3 vec00 = fromStar.clone().add(degLot);
                    Vector3 vecV = degLot.clone().multiply(-2);

                    for (int i = 0; i < 4; i++) {
                        int u = ((i + 1) & 2) >> 1;
                        int v = ((i + 2) & 2) >> 1;

                        Vector3 pos = vec00.clone().add(dir.clone().multiply(u)).add(vecV.clone().multiply(v));
                        vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
                    }

                    tes.draw();
                }
            }
        }

        Map<StarLocation, Rectangle> starRectangles = new HashMap<>();

        RenderAstralSkybox.TEX_STAR_1.bind();
        for (StarLocation sl : c.getStars()) {

            float brightness = func.getBrightness();
            if (applyStarBrightness) {
                float starBr = Minecraft.getMinecraft().theWorld.getStarBrightness(1.0F);
                if (starBr <= 0.23F) {
                    continue;
                }
                brightness *= (starBr * 2);
            }

            vb.begin(7, DefaultVertexFormats.POSITION_TEX);
            if (isKnown) {
                GlStateManager.color(((float) col.getRed()) / 255F, ((float) col.getGreen()) / 255F, ((float) col.getBlue()) / 255F, brightness < 0.2F ? 0.2F : brightness);
            } else {
                GlStateManager.color(brightness, brightness, brightness, brightness < 0 ? 0 : brightness);
            }
            int starX = sl.x;
            int starY = sl.y;

            Vector3 starVec = offsetVec.clone().addX(starX * ulength - ulength).addY(starY * vlength - vlength);
            Point upperLeft = new Point(starVec.getBlockX(), starVec.getBlockY());

            for (int i = 0; i < 4; i++) {
                int u = ((i + 1) & 2) >> 1;
                int v = ((i + 2) & 2) >> 1;

                Vector3 pos = starVec.clone().addX(ulength * u * 2).addY(vlength * v * 2);
                vb.pos(pos.getX(), pos.getY(), pos.getZ()).tex(u, v).endVertex();
            }

            starRectangles.put(sl, new Rectangle(upperLeft.x, upperLeft.y, (int) (ulength * 2), (int) (vlength * 2)));
            tes.draw();
        }

        GlStateManager.color(1, 1, 1, 1);
        return starRectangles;
    }

    public static float stdFlicker(long wtime, float partialTicks, int divisor) {
        return flickerSin(wtime, partialTicks, divisor, 2F, 0.5F);
    }

    public static float conSFlicker(long wtime, float partialTicks, int divisor) {
        return flickerSin(wtime, partialTicks, divisor, 4F, 0.575F);
    }

    public static float conCFlicker(long wtime, float partialTicks, int divisor) {
        return flickerSin(wtime, partialTicks, divisor, 4F, 0.375F);
    }

    public static float flickerSin(long wtime, float partialTicks, int divisor, float div, float move) {
        double rad = (((double) wtime) + partialTicks) / divisor;
        float sin = MathHelper.sin((float) rad);
        return (sin / div) + move;
    }

    public static abstract class BrightnessFunction {

        public abstract float getBrightness();

    }

}
