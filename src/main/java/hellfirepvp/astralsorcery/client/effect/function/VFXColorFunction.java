/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXColorFunction
 * Created by HellFirePvP
 * Date: 08.07.2019 / 19:31
 */
public interface VFXColorFunction<T extends EntityVisualFX> {

    static final Random rand = new Random();

    public static VFXColorFunction<? extends EntityVisualFX> WHITE = constant(Color.WHITE);
    public static VFXColorFunction<? extends EntityVisualFX> BLACK = constant(Color.BLACK);

    @Nonnull
    Color getColor(@Nonnull T fx, float pTicks);

    public static <T extends EntityVisualFX> VFXColorFunction<T> rainbow(int tickSpeed) {
        final float fTickSpeed = tickSpeed;
        return new VFXColorFunction<T>() {
            @Nonnull
            @Override
            public Color getColor(@Nonnull T fx, float pTicks) {
                return Color.getHSBColor((ClientScheduler.getClientTick() % tickSpeed) / fTickSpeed, 1F, 1F);
            }
        };
    }

    public static <T extends EntityVisualFX> VFXColorFunction<T> randomBetween(Color c1, Color c2) {
        float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);
        float degree = rand.nextFloat();
        return new VFXColorFunction<T>() {
            @Nonnull
            @Override
            public Color getColor(@Nonnull T fx, float pTicks) {
                float h = (float) RenderingVectorUtils.interpolate(hsb1[0], hsb2[0], degree);
                float s = (float) RenderingVectorUtils.interpolate(hsb1[1], hsb2[1], degree);
                float b = (float) RenderingVectorUtils.interpolate(hsb1[2], hsb2[2], degree);
                return Color.getHSBColor(h, s, b);
            }
        };
    }

    public static <T extends EntityVisualFX> VFXColorFunction<T> random() {
        Color c = Color.getHSBColor(rand.nextFloat(), 1F, 1F);
        return new VFXColorFunction<T>() {
            @Nonnull
            @Override
            public Color getColor(@Nonnull T fx, float pTicks) {
                return c;
            }
        };
    }

    public static <T extends EntityVisualFX> VFXColorFunction<T> constant(Color c) {
        return new VFXColorFunction<T>() {
            @Nonnull
            @Override
            public Color getColor(@Nonnull T fx, float pTicks) {
                return c;
            }
        };
    }

}
