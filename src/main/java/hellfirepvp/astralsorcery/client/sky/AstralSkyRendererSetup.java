/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.sky;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralSkyRendererSetup
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralSkyRendererSetup {

    private static final RandomSource rand = RandomSource.create();

    static MeshData generateSky() {
        return buildSkyMesh(16F);
    }

    static MeshData generateSkyHorizon() {
        return buildSkyMesh(-16F);
    }

    private static MeshData buildSkyMesh(float offsetY) {
        float f = Math.signum(offsetY) * 512.0F;
        float f1 = 512.0F;
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferbuilder.addVertex(0.0F, offsetY, 0.0F);

        for (int i = -180; i <= 180; i += 45) {
            bufferbuilder.addVertex(f * Mth.cos((float)i * (float) (Math.PI / 180.0)), offsetY, f1 * Mth.sin((float)i * (float) (Math.PI / 180.0)));
        }

        return bufferbuilder.buildOrThrow();
    }

    static MeshData generateStars(int amount, float sizeMultiplier) {
        BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0; i < amount; ++i) { //Amount of stars.
            float x = -1F + rand.nextFloat() * 2F;
            float y = -1F + rand.nextFloat() * 2F;
            float z = -1F + rand.nextFloat() * 2F;
            float ovrSize = 0.15F + rand.nextFloat() * 0.2F; //Size flat increase.
            float d4 = x * x + y * y + z * z;
            if (d4 < 1.0F && d4 > 0.01F) {

                //d4 = Vector3.fastInvSqrt(d4);
                d4 = 1.F / Mth.sqrt(d4);
                x *= d4;
                y *= d4;
                z *= d4;

                float d5 = x * 100.0F;
                float d6 = y * 100.0F;
                float d7 = z * 100.0F;

                float d8 = (float) Mth.atan2(x, z);
                float d9 = Mth.sin(d8);
                float d10 = Mth.cos(d8);

                float d11 = (float) Mth.atan2(Math.sqrt(x * x + z * z), y);
                float d12 = Mth.sin(d11);
                float d13 = Mth.cos(d11);

                //Sizes
                float d14 = rand.nextFloat() * Mth.PI * 2;
                float size = Mth.sin(d14) * 2; //Size percentage increase.
                float d16 = Mth.cos(d14);

                size *= sizeMultiplier;

                //Set 2D vertices
                for (int j = 0; j < 4; ++j) {
                    float d18 = (float) ((j & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[
                    float d19 = (float) ((j + 1 & 2) - 1) * ovrSize; //0 = -1 * [0.15-0.25[

                    float d21 = d18 * d16 - d19 * size;
                    float d22 = d19 * d16 + d18 * size;

                    float d23 = d21 * d12 + 0.0F * d13;
                    float d24 = 0.0F * d12 - d21 * d13;

                    float d25 = d24 * d9 - d22 * d10;
                    float d26 = d22 * d9 + d24 * d10;

                    buffer.addVertex(d5 + d25, d6 + d23, d7 + d26)
                            .setUv(((j + 1) & 2) >> 1, ((j + 2) & 2) >> 1);
                }
            }
        }

        return buffer.buildOrThrow();
    }
}
