/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalEffectController
 * Created by HellFirePvP
 * Date: 01.11.2016 / 21:27
 */
@SideOnly(Side.CLIENT)
public class OrbitalEffectController extends EntityComplexFX {

    private final OrbitPointEffect effect;
    private final OrbitPersistence persistence;
    private final OrbitTickModifier tickModifier;
    private double orbitRadius = 1;
    private int tickOffset = 0;
    private Vector3 orbitAxis = Vector3.RotAxis.Y_AXIS;
    private Vector3 offset = new Vector3();

    public OrbitalEffectController(OrbitPointEffect effect, @Nullable OrbitPersistence persistence, @Nullable OrbitTickModifier tickModifier) {
        this.effect = effect;
        this.persistence = persistence;
        this.tickModifier = tickModifier;
        this.maxAge = 60;
    }

    public OrbitalEffectController setTicksPerRotation(int ticks) {
        this.maxAge = ticks;
        return this;
    }

    public OrbitalEffectController setOrbitRadius(double orbitRadius) {
        this.orbitRadius = orbitRadius;
        return this;
    }

    public OrbitalEffectController setOrbitAxis(Vector3 orbitAxis) {
        this.orbitAxis = orbitAxis;
        return this;
    }

    public OrbitalEffectController setTickOffset(int tickOffset) {
        this.tickOffset = tickOffset;
        return this;
    }

    public OrbitalEffectController setOffset(Vector3 offset) {
        this.offset = offset;
        return this;
    }

    public Vector3 getOffset() {
        return offset;
    }

    @Override
    public void tick() {
        super.tick();

        if(canRemove()) {
            if(persistence != null) {
                if(persistence.canPersist(this)){
                    age = 0;
                }
            }
        }

        if(tickModifier != null) {
            tickModifier.onTick(this);
        }

        scheduleEffects();
    }

    private void scheduleEffects() {
        Vector3 point = orbitAxis.clone().perpendicular().normalize().multiply(orbitRadius).rotate(Math.toRadians(getRotationDegree()), orbitAxis).add(offset);
        effect.doPointTickEffect(this, point);
    }

    public double getRotationDegree() {
        double perc = ((double) ((this.age + this.tickOffset) % this.maxAge)) / ((double) this.maxAge);
        return 360D * perc;
    }

    @Override
    public void render(float pTicks) {}

    public static interface OrbitPersistence {

        public boolean canPersist(OrbitalEffectController controller);

    }

    public static interface OrbitTickModifier {

        public void onTick(OrbitalEffectController controller);

    }

    public static interface OrbitPointEffect {

        public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos);

    }

}
