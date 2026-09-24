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
import hellfirepvp.astralsorcery.common.util.level.DayTimeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXAlphaFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXAlphaFunction<T extends EntityVisualFX> {

    FXAlphaFunction CONSTANT = (fx, alphaIn, pTicks) -> alphaIn;

    FXAlphaFunction FADE_OUT = (fx, alphaIn, pTicks) -> (1F - (((float) fx.getAge()) / ((float) fx.getMaxAge()))) * alphaIn;

    FXAlphaFunction PYRAMID = (fx, alphaIn, pTicks) -> {
        float halfAge = fx.getMaxAge() / 2F;
        return (1F - (Math.abs(halfAge - fx.getAge()) / halfAge)) * alphaIn;
    };

    static <T extends EntityVisualFX> FXAlphaFunction<T> invert(FXAlphaFunction<T> fn) {
        return (fx, alphaIn, pTicks) -> Mth.clamp(1F - fn.getAlpha(fx, alphaIn, pTicks), 0F, 1f);
    }

    static <T extends EntityVisualFX> FXAlphaFunction<T> proximity(Supplier<Vector3> targetSupplier, float distance) {
        return (fx, alpha, pTicks) -> alpha * Mth.clamp(((float) fx.getInterpolatedPos(pTicks).distance(targetSupplier.get())) / distance, 0F, 1F);
    }

    static <T extends EntityVisualFX> FXAlphaFunction<T> proximityToPlayer(float distance) {
        return (fx, alpha, pTicks) -> {
            Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            return alpha * Mth.clamp((float) fx.getInterpolatedPos(pTicks).distance(cameraPos) / distance, 0F, 1F);
        };
    }

    static <T extends EntityVisualFX> FXAlphaFunction<T> distance(float minDistance, float maxDistance) {
        return (fx, alpha, pTicks) -> {
            Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            double dst = fx.getInterpolatedPos(pTicks).distance(cameraPos);
            if (dst <= minDistance) return alpha;
            if (dst > maxDistance) return 0F;
            float dstMultiplier = (float) (dst - minDistance) / (maxDistance - minDistance);
            return alpha * (1F - dstMultiplier);
        };
    }

    float getAlpha(T fx, float alphaIn, float pTicks);

    default FXAlphaFunction<T> andThen(FXAlphaFunction<T> multiplied) {
        FXAlphaFunction<T> existing = this;
        return (fx, alphaIn, pTicks) -> multiplied.getAlpha(fx, existing.getAlpha(fx, alphaIn, pTicks), pTicks);
    }

    static <T extends EntityVisualFX> FXAlphaFunction<T> fadeIn(float fadeInTicks) {
        return (fx, alphaIn, pTicks) -> {
            if (fx.getAgeRefreshCount() > 0) {
                return alphaIn;
            }
            float mul = Mth.clamp((fx.getAge() + pTicks) / fadeInTicks, 0F, 1F);
            return alphaIn * mul;
        };
    }

    static <T extends EntityVisualFX> FXAlphaFunction<T> visibleAtNight(float multiplier) {
        return (fx, alphaIn, pTicks) -> {
            Level clientLevel = Minecraft.getInstance().level;
            if (clientLevel == null) return 0F;
            return alphaIn * DayTimeHelper.getCurrentDaytimeDistribution(clientLevel) * multiplier;
        };
    }
}
