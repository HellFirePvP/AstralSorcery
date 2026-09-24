/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.source;

import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntitySourceFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;

import java.util.function.BiConsumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FXOrbitalSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class FXOrbitalSource extends EntitySourceFX {

    private float orbitRadius = 1F;
    private int orbitalPoints = 1;
    private Vector3.RotAxis orbitAxis = Vector3.RotAxis.Y_AXIS;
    private Vector3 offset = new Vector3();

    public FXOrbitalSource(Vector3 pos) {
        super(pos);
    }

    public FXOrbitalSource setOrbitRadius(float orbitRadius) {
        this.orbitRadius = orbitRadius;
        return this;
    }

    public FXOrbitalSource setOrbitalPoints(int orbitalPoints) {
        this.orbitalPoints = Math.max(1, orbitalPoints);
        return this;
    }

    public FXOrbitalSource setOrbitAxis(Vector3.RotAxis orbitAxis) {
        this.orbitAxis = orbitAxis;
        return this;
    }

    public FXOrbitalSource setOffset(Vector3 offset) {
        this.offset = offset.copy();
        return this;
    }

    public float getOrbitRadius() {
        return this.orbitRadius;
    }

    public int getOrbitalPoints() {
        return this.orbitalPoints;
    }

    public Vector3 getOrbitAxis() {
        return this.orbitAxis.getVector();
    }

    public Vector3 getOffset() {
        return this.offset.copy();
    }

    @Override
    public void tickSpawnFX() {
        for (int point = 0; point < this.orbitalPoints; point++) {
            Vector3 pos = this.getOrbitAxis()
                    .perpendicular()
                    .normalize()
                    .multiply(this.getOrbitRadius())
                    .rotate(Math.toRadians(this.getRotationDegree(point)), this.getOrbitAxis())
                    .add(this.getOffset())
                    .add(this.getPos());

            this.spawnOrbitalParticle(pos);
        }
    }

    public abstract void spawnOrbitalParticle(Vector3 pos);

    private double getRotationDegree(int point) {
        double perc = ((double) (this.age % this.maxAge)) / ((double) this.maxAge);
        return (360F / this.orbitalPoints) * point + 360F * perc;
    }
}
