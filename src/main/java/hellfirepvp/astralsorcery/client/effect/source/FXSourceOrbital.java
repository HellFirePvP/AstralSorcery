/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source;

import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXSourceOrbital
 * Created by HellFirePvP
 * Date: 18.07.2019 / 21:43
 */
public abstract class FXSourceOrbital<E extends EntityVisualFX, T extends BatchRenderContext<E>> extends FXSource<E, T> {

    private double orbitRadius = 1;
    private int tickOffset = 0;
    private Vector3 orbitAxis = Vector3.RotAxis.Y_AXIS;
    private Vector3 offset = new Vector3();

    public FXSourceOrbital(Vector3 pos, T template) {
        super(pos, template);
    }

    public FXSourceOrbital setTicksPerRotation(int ticks) {
        this.maxAge = ticks;
        return this;
    }

    public FXSourceOrbital setOrbitRadius(double orbitRadius) {
        this.orbitRadius = orbitRadius;
        return this;
    }

    public FXSourceOrbital setOrbitAxis(Vector3 orbitAxis) {
        this.orbitAxis = orbitAxis;
        return this;
    }

    public FXSourceOrbital setTickOffset(int tickOffset) {
        this.tickOffset = tickOffset;
        return this;
    }

    public FXSourceOrbital setOffset(Vector3 offset) {
        this.offset = offset;
        return this;
    }

    @Override
    public void tickSpawnFX(Function<Vector3, E> effectRegistrar) {
        Vector3 point = orbitAxis.clone()
                .perpendicular()
                .normalize()
                .multiply(orbitRadius)
                .rotate(Math.toRadians(getRotationDegree()), orbitAxis)
                .add(offset);
        this.spawnOrbitalParticle(point, effectRegistrar);
    }

    public abstract void spawnOrbitalParticle(Vector3 pos, Function<Vector3, E> effectRegistrar);

    private double getRotationDegree() {
        double perc = ((double) ((this.age + this.tickOffset) % this.maxAge)) / ((double) this.maxAge);
        return 360D * perc;
    }
}
