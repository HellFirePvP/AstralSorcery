package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.fx.EntityComplexFX;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.World;
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
    private double orbitRadius = 1;
    private Vector3 orbitAxis = Vector3.RotAxis.Y_AXIS;
    private Vector3 offset = new Vector3();

    public OrbitalEffectController(OrbitPointEffect effect, @Nullable OrbitPersistence persistence) {
        this.effect = effect;
        this.persistence = persistence;
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

    public OrbitalEffectController setOffset(Vector3 offset) {
        this.offset = offset;
        return this;
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

        scheduleEffects();
    }

    private void scheduleEffects() {
        Vector3 point = orbitAxis.clone().perpendicular().normalize().multiply(orbitRadius).rotate(Math.toRadians(getRotationDegree()), orbitAxis).add(offset);
        effect.doPointTickEffect(this, point);
    }

    public double getRotationDegree() {
        double perc = ((double) this.age) / ((double) this.maxAge);
        return 360D * perc;
    }

    @Override
    public void render(float pTicks) {}

    public static interface OrbitPersistence {

        public boolean canPersist(OrbitalEffectController controller);

    }

    public static interface OrbitPointEffect {

        public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos);

    }

}
