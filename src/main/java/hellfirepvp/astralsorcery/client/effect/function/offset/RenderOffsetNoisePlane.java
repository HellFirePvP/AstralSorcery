/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.function.offset;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXRenderOffsetFunction;
import hellfirepvp.astralsorcery.client.lib.EffectTemplatesAS;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderOffsetNoisePlane
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderOffsetNoisePlane implements FXRenderOffsetFunction<EntityVisualFX> {

    private static final String KEY_PLANE_DATA = "plane";

    private static final int SAMPLE_TIME_MIN = 35;
    private static final int SAMPLE_TIME_MAX = 55;

    private final RandomSource rand = RandomSource.create();
    private long lastSample, targetSample;

    private final float ringSize;

    private Vector3 prevRotationDeg, rotationDeg;

    public RenderOffsetNoisePlane(float ringSizeDiameter) {
        this.ringSize = ringSizeDiameter;

        this.lastSample = ClientProxy.getClientTick() - randomSampleTime();
        this.targetSample = this.lastSample + randomSampleTime();
    }

    private int randomSampleTime() {
        return rand.nextInt(SAMPLE_TIME_MIN + (SAMPLE_TIME_MAX - SAMPLE_TIME_MIN));
    }

    private Vector3 interpolateRotation(double partial, Vector3 vZero, Vector3 vOne) {
        double v = (20 * Mth.clamp(partial, 0, 1)) - 10;
        v = Mth.clamp(((Math.atan(v) / 2.9423D) + 0.5D), 0, 1);
        return RenderVectorUtil.interpolate(vZero, vOne, (float) v);
    }

    private Vector3 getCurrentRotationDegree(float partial) {
        checkRotations();

        long current = ClientProxy.getClientTick();
        double perc = 1 - (((double) (targetSample - current - partial)) / ((double) (targetSample - lastSample)));
        return interpolateRotation(perc, prevRotationDeg, rotationDeg);
    }

    private void checkRotations() {
        if (ClientProxy.getClientTick() >= this.targetSample) {
            buildRotations();
        }
    }

    private void buildRotations() {
        this.lastSample = ClientProxy.getClientTick();
        if (this.rotationDeg != null) {
            this.prevRotationDeg = this.rotationDeg;
        } else {
            this.prevRotationDeg = Vector3.positiveYRandom(rand);
        }
        this.rotationDeg = Vector3.positiveYRandom(rand);
        this.targetSample = this.lastSample + randomSampleTime();
    }

    public EntityVisualFX createParticle(Vector3 position) {
        EntityVisualFX p = EffectHelper.of(EffectTemplatesAS.GENERIC_PARTICLE)
                .spawn(position)
                .color(FXColorFunction.WHITE)
                .renderOffset(this);
        p.getOrCreateData(KEY_PLANE_DATA, () -> new PlanarRotationData(
                rand.nextFloat() * 360F,
                ringSize * 0.9F + rand.nextFloat() * ringSize * 0.2F));
        return p;
    }

    @Nonnull
    @Override
    public Vector3 getRenderOffset(@Nonnull EntityVisualFX fx, @Nonnull Vector3 renderPosition, float pTicks) {
        return fx.<PlanarRotationData>getData(KEY_PLANE_DATA).map(data -> {
            Vector3 angle = getCurrentRotationDegree(pTicks);
            Vector3 v = angle.copy().perpendicular().normalize().multiply(data.initialDistance());
            v.rotate(Math.toRadians(data.degreeRotation()), angle);
            return renderPosition.add(v);
        }).orElse(renderPosition);
    }

    private record PlanarRotationData(float degreeRotation, float initialDistance) {
    }
}
