/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
import hellfirepvp.astralsorcery.common.constellation.world.ActiveCelestialsHandler;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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

    public static void renderConstellationSky(IConstellation c, MatrixStack renderStack, ActiveCelestialsHandler.RenderPosition renderPos, Supplier<Float> brightnessFn) {
        Matrix4f matr = renderStack.getLast().getMatrix();

        Vector3 renderOffset = renderPos.offset;
        Color rC = c.getTierRenderColor();
        int r = rC.getRed();
        int g = rC.getGreen();
        int b = rC.getBlue();

        //Now we build from the exact UV vectors a 31x31 grid and render the stars & connections.
        Vector3 dirU = renderPos.incU.clone().subtract(renderOffset).divide(31);
        Vector3 dirV = renderPos.incV.clone().subtract(renderOffset).divide(31);
        double uLength = dirU.length();
        TexturesAS.TEX_STAR_CONNECTION.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            for (int j = 0; j < 2; j++) {
                for (StarConnection con : c.getStarConnections()) {
                    Vector3 vecA = renderOffset.clone().add(dirU.clone().multiply(con.from.x + 1)).add(dirV.clone().multiply(con.from.y + 1));
                    Vector3 vecB = renderOffset.clone().add(dirU.clone().multiply(con.to.x + 1)).add(dirV.clone().multiply(con.to.y + 1));
                    Vector3 vecCV = vecB.subtract(vecA);
                    Vector3 oPane = dirV.clone().crossProduct(vecCV);
                    Vector3 vecAD = oPane.clone().crossProduct(vecCV).normalize().multiply(uLength);
                    Vector3 offset00 = vecA.subtract(vecAD.clone().multiply(j == 0 ? 1 : -1));
                    Vector3 vecU = vecAD.clone().multiply(j == 0 ? 2 : -2);

                    for (int i = 0; i < 4; i++) {
                        Vector3 pos = offset00.clone().add(vecU.clone().multiply(((i + 1) & 2) >> 1)).add(vecCV.clone().multiply(((i + 2) & 2) >> 1));
                        buf.pos(matr, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ())
                                .color(r, g, b, MathHelper.clamp((int) (brightnessFn.get() * 255), 0, 255))
                                .tex(((i + 2) & 2) >> 1, ((i + 3) & 2) >> 1)
                                .endVertex();
                    }
                }
            }
        });

        TexturesAS.TEX_STAR_1.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            for (StarLocation star : c.getStars()) {
                int x = star.x;
                int y = star.y;
                Vector3 ofStar = renderOffset.clone().add(dirU.clone().multiply(x)).add(dirV.clone().multiply(y));
                for (int i = 0; i < 4; i++) {
                    int u = ((i + 1) & 2) >> 1;
                    int v = ((i + 2) & 2) >> 1;
                    Vector3 pos = ofStar.clone().add(dirU.clone().multiply(u << 1)).add(dirV.clone().multiply(v << 1));
                    buf.pos(matr, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ())
                            .color(r, g, b, MathHelper.clamp((int) (brightnessFn.get() * 255), 0, 255))
                            .tex(u, v)
                            .endVertex();
                }
            }
        });
    }

    public static void renderConstellationIntoWorldFlat(IConstellation c, MatrixStack renderStack, IRenderTypeBuffer buffer, Vector3 offset, double scale, double line, float brightness) {
        renderConstellationIntoWorldFlat(c.getConstellationColor(), c, renderStack, buffer, offset, scale, line, brightness);
    }

    public static void renderConstellationIntoWorldFlat(Color color, IConstellation c, MatrixStack renderStack, IRenderTypeBuffer buffer, Vector3 offset, double scale, double line, float brightness) {
        Matrix4f matr = renderStack.getLast().getMatrix();
        Vector3 thisOffset = offset.clone();
        double starSize = 1D / ((double) IConstellation.STAR_GRID_WIDTH_HEIGHT) * scale;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int connAlpha = (int) ((brightness * 0.8F) * 255F);
        int starAlpha = (int) (brightness * 255F);

        Vector3 drawOffset = new Vector3(-15.5D * starSize, 0, -15.5D * starSize);

        IVertexBuilder buf = buffer.getBuffer(RenderTypesAS.CONSTELLATION_WORLD_CONNECTION);
        for (StarConnection sc : c.getStarConnections()) {
            thisOffset.addY(0.001);

            Vector3 dirU = new Vector3(sc.to.x, 0, sc.to.y).subtract(sc.from.x, 0, sc.from.y).multiply(starSize);
            Vector3 dirV = dirU.clone().crossProduct(new Vector3(0, 1, 0)).setY(0).normalize().multiply(line * starSize);

            Vector3 starOffset = thisOffset.clone().addX(sc.from.x * starSize).addZ(sc.from.y * starSize);
            Vector3 offsetRender = starOffset.subtract(dirV.clone().divide(2));
            offsetRender.add(drawOffset);

            Vector3 pos = offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(1));
            pos.drawPos(matr, buf).color(r, g, b, connAlpha).tex(1, 0).endVertex();

            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(1));
            pos.drawPos(matr, buf).color(r, g, b, connAlpha).tex(0, 0).endVertex();

            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(0));
            pos.drawPos(matr, buf).color(r, g, b, connAlpha).tex(0, 1).endVertex();

            pos =         offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(0));
            pos.drawPos(matr, buf).color(r, g, b, connAlpha).tex(1, 1).endVertex();
        }

        Vector3 dirU = new Vector3(starSize * 2, 0, 0);
        Vector3 dirV = new Vector3(0, 0, starSize * 2);

        buf = buffer.getBuffer(RenderTypesAS.CONSTELLATION_WORLD_STAR);
        for (StarLocation sl : c.getStars()) {
            Vector3 offsetRender = thisOffset.clone().add(sl.x * starSize - starSize, 0.005, sl.y * starSize - starSize);
            offsetRender.add(drawOffset);

            Vector3 pos = offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(1));
            pos.drawPos(matr, buf).color(r, g, b, starAlpha).tex(1, 0).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(1));
            pos.drawPos(matr, buf).color(r, g, b, starAlpha).tex(0, 0).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(1)).add(dirV.clone().multiply(0));
            pos.drawPos(matr, buf).color(r, g, b, starAlpha).tex(0, 1).endVertex();
            pos =         offsetRender.clone().add(dirU.clone().multiply(0)).add(dirV.clone().multiply(0));
            pos.drawPos(matr, buf).color(r, g, b, starAlpha).tex(1, 1).endVertex();
        }
    }

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(IConstellation c, float offsetX, float offsetY, float zLevel, float width, float height, double linebreadth, Supplier<Float> brightnessFn, boolean isKnown, boolean applyStarBrightness) {
        return renderConstellationIntoGUI(c.getTierRenderColor(), c, offsetX, offsetY, zLevel, width, height, linebreadth, brightnessFn, isKnown, applyStarBrightness);
    }

    public static Map<StarLocation, Rectangle> renderConstellationIntoGUI(Color col, IConstellation c, float offsetX, float offsetY, float zLevel, float width, float height, double linebreadth, Supplier<Float> brightnessFn, boolean isKnown, boolean applyStarBrightness) {
        double ulength = ((double) width) / IConstellation.STAR_GRID_WIDTH_HEIGHT;
        double vlength = ((double) height) / IConstellation.STAR_GRID_WIDTH_HEIGHT;

        int r = col.getRed();
        int g = col.getGreen();
        int b = col.getBlue();

        Vector3 offsetVec = new Vector3(offsetX, offsetY, zLevel);

        float starBrightness = 1F;
        if (applyStarBrightness && Minecraft.getInstance().world != null) {
            starBrightness = Minecraft.getInstance().world.getStarBrightness(1.0F);
            if (starBrightness <= 0.23F) {
                return new HashMap<>();
            }
            starBrightness *= 2;
        }
        float brightness = starBrightness;

        if (isKnown) {
            TexturesAS.TEX_STAR_CONNECTION.bindTexture();
            RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
                for (int j = 0; j < 2; j++) {
                    for (StarConnection sc : c.getStarConnections()) {
                        int alpha = MathHelper.clamp((int) (brightnessFn.get() * brightness * 255F), 0, 255);

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
                            buf.pos(pos.getX(), pos.getY(), pos.getZ())
                                    .color(r, g, b, alpha)
                                    .tex(u, v)
                                    .endVertex();
                        }
                    }
                }
            });
        }

        Map<StarLocation, Rectangle> starRectangles = new HashMap<>();

        TexturesAS.TEX_STAR_1.bindTexture();
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            for (StarLocation sl : c.getStars()) {
                int alpha = MathHelper.clamp((int) (brightnessFn.get() * brightness * 255F), 0, 255);

                int starX = sl.x;
                int starY = sl.y;

                Vector3 starVec = offsetVec.clone().addX(starX * ulength - ulength).addY(starY * vlength - vlength);
                Point upperLeft = new Point(starVec.getBlockX(), starVec.getBlockY());

                for (int i = 0; i < 4; i++) {
                    int u = ((i + 1) & 2) >> 1;
                    int v = ((i + 2) & 2) >> 1;

                    Vector3 pos = starVec.clone().addX(ulength * u * 2).addY(vlength * v * 2);
                    buf.pos(pos.getX(), pos.getY(), pos.getZ())
                            .color(isKnown ? r : alpha,
                                    isKnown ? g : alpha,
                                    isKnown ? b : alpha,
                                    MathHelper.clamp((int) (alpha * 1.2F + 0.2F), 0, 255))
                            .tex(u, v)
                            .endVertex();
                }

                starRectangles.put(sl, new Rectangle(upperLeft.x, upperLeft.y, (int) (ulength * 2), (int) (vlength * 2)));
            }
        });

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
