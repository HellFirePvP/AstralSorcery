/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky.astral;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralSkyRendererSetup
 * Created by HellFirePvP
 * Date: 13.01.2020 / 21:01
 */
class AstralSkyRendererSetup {

    private static final Random RAND = new Random();

    static void generateSky(BufferBuilder skyBuffer) {
        prepareSky(skyBuffer, 16F, false);
    }

    static void generateSkyHorizon(BufferBuilder skyBuffer) {
        prepareSky(skyBuffer, -16F, true);
    }

    private static void prepareSky(BufferBuilder buf, float offsetY, boolean flip) {
        int scale = 64;
        int segments = 6;
        int width = segments * scale;
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);

        for (int x = -width; x <= width; x += scale) {
            for (int z = -width; z <= width; z += scale) {
                float x0 = (float)  x;
                float x1 = (float) (x + scale);
                if (flip) {
                    x1 = (float)  x;
                    x0 = (float) (x + scale);
                }

                buf.pos(x0, offsetY, z).endVertex();
                buf.pos(x1, offsetY, z).endVertex();
                buf.pos(x1, offsetY, z + scale).endVertex();
                buf.pos(x0, offsetY, z + scale).endVertex();
            }
        }
    }

    static void generateStars(BufferBuilder starBuffer, int amount, float sizeMultiplier) {
        starBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        for (int i = 0; i < amount; ++i) { //Amount of stars.
            double x = -1F + RAND.nextFloat() * 2F;
            double y = -1F + RAND.nextFloat() * 2F;
            double z = -1F + RAND.nextFloat() * 2F;
            double ovrSize = 0.15F + RAND.nextFloat() * 0.2F; //Size flat increase.
            double d4 = x * x + y * y + z * z;
            if (d4 < 1.0D && d4 > 0.01D) {

                //d4 = Vector3.fastInvSqrt(d4);
                d4 = 1.0D / Math.sqrt(d4);
                x *= d4;
                y *= d4;
                z *= d4;

                double d5 = x * 100.0D;
                double d6 = y * 100.0D;
                double d7 = z * 100.0D;

                double d8 = Math.atan2(x, z);
                double d9 = Math.sin(d8);
                double d10 = Math.cos(d8);

                double d11 = Math.atan2(Math.sqrt(x * x + z * z), y);
                double d12 = Math.sin(d11);
                double d13 = Math.cos(d11);

                //Sizes
                double d14 = RAND.nextDouble() * Math.PI * 2.0D;
                double size = Math.sin(d14) * 2; //Size percentage increase.
                double d16 = Math.cos(d14);

                size *= sizeMultiplier;

                //Set 2D vertices
                for (int j = 0; j < 4; ++j) {
                    double d18 = (double) ((j & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[
                    double d19 = (double) ((j + 1 & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[

                    double d21 = d18 * d16 - d19 * size;
                    double d22 = d19 * d16 + d18 * size;
                    double d23 = d21 * d12 + 0.0D * d13;

                    double d24 = 0.0D * d12 - d21 * d13;

                    double d25 = d24 * d9 - d22 * d10;
                    double d26 = d22 * d9 + d24 * d10;

                    starBuffer
                            .pos(d5 + d25, d6 + d23, d7 + d26)
                            .tex(((j + 1) & 2) >> 1, ((j + 2) & 2) >> 1)
                            .endVertex();
                }
            }
        }
    }
}
