/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXMotionController
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:33
 */
public interface VFXMotionController<T extends EntityVisualFX> {

    VFXMotionController<?> IDENTITY = (fx, motion) -> motion;

    public static <T extends EntityVisualFX> VectorTarget<T> target(Supplier<Vector3> targetSupplier) {
        return target(targetSupplier, 1);
    }

    public static <T extends EntityVisualFX> VectorTarget<T> target(Supplier<Vector3> targetSupplier, double velocityMultiplier) {
        return new VectorTarget<>(targetSupplier, velocityMultiplier);
    }

    public static <T extends EntityVisualFX> VFXMotionController<T> accelerate(Supplier<Vector3> originalMotion) {
        return new VFXMotionController<T>() {
            @Nonnull
            @Override
            public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
                float perc = (float) fx.getAge() / (float) fx.getMaxAge();
                return originalMotion.get().clone().multiply(perc);
            }
        };
    }

    public static <T extends EntityVisualFX> VFXMotionController<T> decelerate(Supplier<Vector3> originalMotion) {
        return new VFXMotionController<T>() {
            @Nonnull
            @Override
            public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
                float perc = 1F - ((float) fx.getAge() / (float) fx.getMaxAge());
                return originalMotion.get().clone().multiply(perc);
            }
        };
    }

    @Nonnull
    public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion);

    public static class VectorTarget<T extends EntityVisualFX> implements VFXMotionController<T> {

        private final Supplier<Vector3> positionSupplier;
        private final double velocityMultiplier;

        protected VectorTarget(Supplier<Vector3> positionSupplier, double velocityMultiplier) {
            this.positionSupplier = positionSupplier;
            this.velocityMultiplier = velocityMultiplier;
        }

        @Nonnull
        @Override
        public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
            Vector3 target = positionSupplier.get();
            if (target == null) {
                return motion;
            }
            EntityUtils.applyVortexMotion(fx::getPosition, motion::add, target, 256, this.velocityMultiplier);
            return motion.multiply(0.9);
        }
    }
}
