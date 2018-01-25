/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.EntityUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityComplexFX
 * Created by HellFirePvP
 * Date: 17.09.2016 / 22:55
 */
public abstract class EntityComplexFX implements IComplexEffect {

    private static long counter = 0;

    public final long id;
    protected int age = 0;
    protected int maxAge = 40;
    protected boolean removeRequested = false;

    private boolean flagRemoved = true;

    public EntityComplexFX() {
        this.id = counter;
        counter++;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean canRemove() {
        return age >= maxAge || removeRequested;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void tick() {
        age++;
    }

    public void requestRemoval() {
        this.removeRequested = true;
    }

    public boolean isRemoved() {
        return flagRemoved;
    }

    public void flagAsRemoved() {
        flagRemoved = true;
        removeRequested = false;
    }

    public void clearRemoveFlag() {
        flagRemoved = false;
    }

    public static interface RenderAlphaFunction<T extends IComplexEffect> {

        public float getRenderAlpha(T fx, float currentAlpha);

    }

    public static enum AlphaFunction {

        CONSTANT,
        FADE_OUT,
        PYRAMID;

        AlphaFunction() {}

        public float getAlpha(int age, int maxAge) {
            switch (this) {
                case CONSTANT:
                    return 1F;
                case FADE_OUT:
                    return 1F - (((float) age) / ((float) maxAge));
                case PYRAMID:
                    float halfAge = maxAge / 2F;
                    return 1F - (Math.abs(halfAge - age) / halfAge);
            }
            return 1F;
        }

    }

    public static interface RenderOffsetController {

        public Vector3 changeRenderPosition(EntityComplexFX fx, Vector3 currentRenderPos, Vector3 currentMotion, float pTicks);

    }

    public static interface PositionController<T extends IComplexEffect> {

        public Vector3 updatePosition(T fx, Vector3 position, Vector3 motionToBeMoved);

    }

    public static interface MotionController<T extends IComplexEffect> {

        public Vector3 updateMotion(T fx, Vector3 motion);

        public static class EntityTarget<T extends IComplexEffect> implements MotionController<T> {

            private final Entity target;
            private final Function<T, Vector3> positionFunction;

            public EntityTarget(Entity target, Function<T, Vector3> positionFunction) {
                this.target = target;
                this.positionFunction = positionFunction;
            }

            @Override
            public Vector3 updateMotion(T fx, Vector3 motion) {
                if (target.isDead) return motion;
                EntityUtils.applyVortexMotion((v) -> positionFunction.apply(fx), motion::add, Vector3.atEntityCorner(target), 256, 1);
                return motion.multiply(0.9);
            }

        }

    }

    public static interface ScaleFunction<T extends IComplexEffect> {

        public float getScale(T fx, float pTicks, float scaleIn);

        public static class Shrink<T extends EntityComplexFX> implements ScaleFunction<T> {

            @Override
            public float getScale(T fx, float pTicks, float scaleIn) {
                float prevAge = Math.max(0F, ((float) fx.getAge() - 1)) / ((float) fx.getMaxAge());
                float currAge = Math.max(0F, ((float) fx.getAge()))     / ((float) fx.getMaxAge());
                return (float) (scaleIn * (1 - (RenderingUtils.interpolate(prevAge, currAge, pTicks))));
            }

        }

    }

    public static interface RefreshFunction {

        public boolean shouldRefresh();

    }

}
