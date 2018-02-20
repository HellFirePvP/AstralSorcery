/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ControllerNoisePlane
 * Created by HellFirePvP
 * Date: 12.12.2017 / 21:13
 */
public class ControllerNoisePlane implements EntityComplexFX.RenderOffsetController {

    private static final Random T_RAND = new Random();
    private static final int SAMPLE_TIME_MIN = 35;
    private static final int SAMPLE_TIME_MAX = 55;

    private long lastSample, targetSample;

    private final float ringSize;

    private Vector3 prevRotationDeg, rotationDeg;

    public ControllerNoisePlane(float ringSizeDiameter) {
        this.ringSize = ringSizeDiameter;
        buildRotations();
        this.lastSample = ClientScheduler.getClientTick() - randomSampleTime();
        this.targetSample = this.lastSample + randomSampleTime();
    }

    public EntityFXFacingParticle setupParticle() {
        FXPlanarEffect p = new FXPlanarEffect(0, 0, 0,
                T_RAND.nextFloat() * 360F,
                ringSize * 0.9F + T_RAND.nextFloat() * ringSize * 0.2F);
        p.enableAlphaFade(EntityComplexFX.AlphaFunction.PYRAMID).setAlphaMultiplier(0.75F).setColor(new Color(60, 0, 255));
        p.setRenderOffsetController(this).gravity(0.004);
        EffectHandler.getInstance().registerFX(p);
        return p;
    }

    private Vector3 getCurrentRotationDegree(float partial) {
        checkRotations();

        long current = ClientScheduler.getClientTick();
        double perc = 1 - (((double) (targetSample - current - partial)) / ((double) (targetSample - lastSample)));
        return interpolateRotation(perc, prevRotationDeg, rotationDeg);
    }

    private void checkRotations() {
        if(ClientScheduler.getClientTick() >= targetSample) {
            buildRotations();
        }
    }

    private void buildRotations() {
        this.lastSample = ClientScheduler.getClientTick();
        if(this.rotationDeg != null) {
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
                RenderingUtils.interpolate(vZero.getX(), vOne.getX(), percent),
                RenderingUtils.interpolate(vZero.getY(), vOne.getY(), percent),
                RenderingUtils.interpolate(vZero.getZ(), vOne.getZ(), percent));
    }

    @Override
    public Vector3 changeRenderPosition(EntityComplexFX fx, Vector3 currentRenderPos, Vector3 currentMotion, float pTicks) {
        if(!(fx instanceof FXPlanarEffect)) {
            return currentRenderPos;
        }
        Vector3 angle = getCurrentRotationDegree(pTicks);
        Vector3 v = angle.clone().perpendicular().normalize().multiply(((FXPlanarEffect) fx).initialDistance);
        v.rotate(Math.toRadians(((FXPlanarEffect) fx).degreeRotation), angle);
        return currentRenderPos.add(v);
    }

    private static class FXPlanarEffect extends EntityFXFacingParticle {

        private final float degreeRotation;
        private final float initialDistance;

        FXPlanarEffect(double x, double y, double z, float degreeRotation, float initialDistance) {
            super(x, y, z);
            this.degreeRotation = degreeRotation;
            this.initialDistance = initialDistance;
        }

    }

}
