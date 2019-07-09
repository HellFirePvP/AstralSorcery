/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXColorFunction
 * Created by HellFirePvP
 * Date: 08.07.2019 / 19:31
 */
public interface VFXColorFunction<T extends EntityVisualFX> {

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
