/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function.impl;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.VFXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.VFXRenderOffsetFunction;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.vfx.FXFacingParticle;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderOffsetNoisePlane
 * Created by HellFirePvP
 * Date: 19.07.2019 / 10:11
 */
public class RenderOffsetNoisePlane implements VFXRenderOffsetFunction<EntityVisualFX> {

    private static final String KEY_PLANE_DATA = "plane";
    private static final Random T_RAND = new Random();

    private static final int SAMPLE_TIME_MIN = 35;
    private static final int SAMPLE_TIME_MAX = 55;

    private long lastSample, targetSample;

    private final float ringSize;

    private Vector3 prevRotationDeg, rotationDeg;

    public RenderOffsetNoisePlane(float ringSizeDiameter) {
        this.ringSize = ringSizeDiameter;
        buildRotations();
        this.lastSample = ClientScheduler.getClientTick() - randomSampleTime();
        this.targetSample = this.lastSample + randomSampleTime();
    }

    public FXFacingParticle createParticle(Vector3 position) {
        FXFacingParticle p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(position)
                .color(VFXColorFunction.WHITE)
                .renderOffset(this);
        p.getOrCreateData(KEY_PLANE_DATA, () -> new PlanarRotationData(
                T_RAND.nextFloat() * 360F,
                ringSize * 0.9F + T_RAND.nextFloat() * ringSize * 0.2F));
        return p;
    }

    private Vector3 getCurrentRotationDegree(float partial) {
        checkRotations();

        long current = ClientScheduler.getClientTick();
        double perc = 1 - (((double) (targetSample - current - partial)) / ((double) (targetSample - lastSample)));
        return interpolateRotation(perc, prevRotationDeg, rotationDeg);
    }

    private void checkRotations() {
        if (ClientScheduler.getClientTick() >= targetSample) {
            buildRotations();
        }
    }

    private void buildRotations() {
        this.lastSample = ClientScheduler.getClientTick();
        if (this.rotationDeg != null) {
            this.prevRotationDeg = this.rotationDeg;
        } else {
            this.prevRotationDeg = Vector3.positiveYRandom();
        }
        this.rotationDeg = Vector3.positiveYRandom();
        this.targetSample = this.lastSample + randomSampleTime();
    }

    private int randomSampleTime() {
        return T_RAND.nextInt(SAMPLE_TIME_MIN + (SAMPLE_TIME_MAX - SAMPLE_TIME_MIN));
    }

    private Vector3 interpolateRotation(double partial, Vector3 vZero, Vector3 vOne) {
        double v = (20 * MathHelper.clamp(partial, 0, 1)) - 10;
        v = MathHelper.clamp(((Math.atan(v) / 2.9423D) + 0.5D), 0, 1);
        return getInterpolatedVectorRotation((float) v, vZero, vOne);
    }

    private Vector3 getInterpolatedVectorRotation(float percent, Vector3 vZero, Vector3 vOne) {
        return new Vector3(
                RenderingVectorUtils.interpolate(vZero.getX(), vOne.getX(), percent),
                RenderingVectorUtils.interpolate(vZero.getY(), vOne.getY(), percent),
                RenderingVectorUtils.interpolate(vZero.getZ(), vOne.getZ(), percent));
    }

    @Nonnull
    @Override
    public Vector3 changeRenderPosition(@Nonnull EntityVisualFX fx, Vector3 interpolatedPos, float pTicks) {
        PlanarRotationData data = fx.getData(KEY_PLANE_DATA);
        if (data == null) {
            return interpolatedPos;
        }
        Vector3 angle = getCurrentRotationDegree(pTicks);
        Vector3 v = angle.clone().perpendicular().normalize().multiply(data.initialDistance);
        v.rotate(Math.toRadians(data.degreeRotation), angle);
        return interpolatedPos.add(v);
    }

    private static class PlanarRotationData {

        private final float degreeRotation;
        private final float initialDistance;

        private PlanarRotationData(float degreeRotation, float initialDistance) {
            this.degreeRotation = degreeRotation;
            this.initialDistance = initialDistance;
        }

    }
}
