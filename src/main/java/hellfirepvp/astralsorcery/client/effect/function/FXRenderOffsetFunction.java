/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXRenderOffsetFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXRenderOffsetFunction<T extends EntityVisualFX> {

    FXRenderOffsetFunction<?> IDENTITY = (fx, renderPosition, pTicks) -> renderPosition;

    @Nonnull
    Vector3 getRenderOffset(@Nonnull T fx, @Nonnull Vector3 renderPosition, float pTicks);

    static FXRenderOffsetFunction<?> tumble(float intensity) {
        RandomSource rand = RandomSource.create();
        float offsetX = rand.nextFloat() * 2 * Mth.PI;
        float offsetZ = rand.nextFloat() * 2 * Mth.PI;

        return (fx, renderPosition, pTicks) -> {
            return renderPosition.copy()
                .addX(Math.sin((offsetX + fx.getAge() + pTicks) / 2 + offsetX) * intensity)
                .addZ(Math.cos((offsetZ + fx.getAge() + pTicks) / 2 + offsetZ) * intensity);
        };
    }
}
