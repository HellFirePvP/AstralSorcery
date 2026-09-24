/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityFX;
import hellfirepvp.astralsorcery.common.util.VectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXMotionFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface FXMotionFunction<T extends EntityFX> {

    FXMotionFunction<?> IDENTITY = (fx, motion) -> motion;

    static <T extends EntityFX> VectorTarget<T> target(Supplier<Vector3> targetSupplier) {
        return target(targetSupplier, 0.1F);
    }

    static <T extends EntityFX> VectorTarget<T> target(Supplier<Vector3> targetSupplier, float velocityMultiplier) {
        return new VectorTarget<>(targetSupplier, velocityMultiplier);
    }

    static <T extends EntityFX> FXMotionFunction<T> accelerate(Supplier<Vector3> originalMotion) {
        return new FXMotionFunction<T>() {
            @Nonnull
            @Override
            public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
                float perc = (float) fx.getAge() / (float) fx.getMaxAge();
                return originalMotion.get().copy().multiply(perc);
            }
        };
    }

    static <T extends EntityFX> FXMotionFunction<T> decelerate(Supplier<Vector3> originalMotion) {
        return new FXMotionFunction<T>() {
            @Nonnull
            @Override
            public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
                float perc = 1F - ((float) fx.getAge() / (float) fx.getMaxAge());
                return originalMotion.get().copy().multiply(perc);
            }
        };
    }

    static <T extends EntityFX> FXMotionFunction<T> decelerate(float decelerationFactor) {
        return new FXMotionFunction<T>() {
            @Nonnull
            @Override
            public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
                return motion.copy().multiply(1f - decelerationFactor);
            }
        };
    }

    @Nonnull
    Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion);

    class VectorTarget<T extends EntityFX> implements FXMotionFunction<T> {

        private final Supplier<Vector3> targetSupplier;
        private final float velocityMultiplier;

        protected VectorTarget(Supplier<Vector3> targetSupplier, float velocityMultiplier) {
            this.targetSupplier = targetSupplier;
            this.velocityMultiplier = velocityMultiplier;
        }

        @Nonnull
        @Override
        public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
            Vector3 target = this.targetSupplier.get();
            if (target == null) {
                return motion;
            }
            motion.add(VectorUtil.getVortexMotion(fx.getPos(), target, 256, this.velocityMultiplier));
            return motion.multiply(0.9);
        }
    }

}
