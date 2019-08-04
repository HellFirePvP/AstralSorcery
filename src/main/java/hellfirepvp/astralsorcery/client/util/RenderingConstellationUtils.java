/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.star.StarConnection;
import hellfirepvp.astralsorcery.common.constellation.star.StarLocation;
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
