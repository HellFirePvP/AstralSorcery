/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import net.minecraft.entity.Entity;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VFXMotionController
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:33
 */
public interface VFXMotionController<T extends EntityVisualFX> {

    VFXMotionController<?> IDENTITY = (fx, motion) -> motion;

    @Nonnull
    public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion);

    public static class EntityTarget<T extends EntityVisualFX> implements VFXMotionController<T> {

        private final Entity target;
        private final Function<T, Vector3> positionFunction;

        public EntityTarget(Entity target, Function<T, Vector3> positionFunction) {
            this.target = target;
            this.positionFunction = positionFunction;
        }

        @Override
        @Nonnull
        public Vector3 updateMotion(@Nonnull T fx, @Nonnull Vector3 motion) {
            if (!target.isAlive()) {
                return motion;
            }
            EntityUtils.applyVortexMotion((v) -> positionFunction.apply(fx), motion::add, Vector3.atEntityCorner(target), 256, 1);
            return motion.multiply(0.9);
        }

    }

}
