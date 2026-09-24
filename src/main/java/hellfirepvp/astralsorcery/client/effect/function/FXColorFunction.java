/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXColorFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXColorFunction<T extends EntityVisualFX> {

    RandomSource rand = RandomSource.create();

    FXColorFunction<? extends EntityVisualFX> WHITE = constant(ColorWrapper.WHITE);
    FXColorFunction<? extends EntityVisualFX> BLACK = constant(ColorWrapper.BLACK);

    @Nonnull
    ColorWrapper getColor(@Nonnull T fx, float pTicks);

    static <T extends EntityVisualFX> FXColorFunction<T> rainbow(int tickSpeed) {
        final float fTickSpeed = tickSpeed;
        return new FXColorFunction<T>() {
            @Nonnull
            @Override
            public ColorWrapper getColor(@Nonnull T fx, float pTicks) {
                return ColorWrapper.ofHSB((ClientProxy.getClientTick() % tickSpeed) / fTickSpeed, 1F, 1F);
            }
        };
    }

    static <T extends EntityVisualFX> FXColorFunction<T> randomBetween(ColorWrapper c1, ColorWrapper c2) {
        float[] hsb1 = ColorWrapper.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue());
        float[] hsb2 = ColorWrapper.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue());
        float degree = rand.nextFloat();
        return new FXColorFunction<T>() {
            @Nonnull
            @Override
            public ColorWrapper getColor(@Nonnull T fx, float pTicks) {
                float h = RenderVectorUtil.interpolate(hsb1[0], hsb2[0], degree);
                float s = RenderVectorUtil.interpolate(hsb1[1], hsb2[1], degree);
                float b = RenderVectorUtil.interpolate(hsb1[2], hsb2[2], degree);
                return ColorWrapper.ofHSB(h, s, b);
            }
        };
    }

    static <T extends EntityVisualFX> FXColorFunction<T> random() {
        ColorWrapper c = ColorWrapper.ofHSB(rand.nextFloat(), 1F, 1F);
        return (fx, pTicks) -> c;
    }

    static <T extends EntityVisualFX> FXColorFunction<T> constant(ColorWrapper c) {
        return (fx, pTicks) -> c;
    }

    static <T extends EntityVisualFX> FXColorFunction<T> constant(Supplier<ColorWrapper> c) {
        return (fx, pTicks) -> c.get();
    }
}
