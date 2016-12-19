package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalPropertiesAttunement
 * Created by HellFirePvP
 * Date: 16.12.2016 / 21:37
 */
public class OrbitalPropertiesAttunement implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect {

    private static final Random rand = new Random();

    private boolean mirrored;
    private int persistanceRequests = 5;

    public OrbitalPropertiesAttunement(boolean mirrored) {
        this.mirrored = mirrored;
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        persistanceRequests--;
        return persistanceRequests >= 0;
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX(),
                pos.getY(),
                pos.getZ());
        p.motion((rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
        p.setMaxAge(25);
        p.scale(0.3F).gravity(0.004);

        if(rand.nextBoolean()) {
            p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion(0, 0.03 + (rand.nextFloat() * 0.04F), 0);
            p.setMaxAge(35);
            p.scale(0.25F).gravity(0.004).setColor(Color.WHITE);
        }
        if(rand.nextBoolean()) {
            p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion(0, 0.03 + (rand.nextFloat() * 0.04F), 0);
            p.setMaxAge(35);
            p.scale(0.25F).gravity(0.004).setColor(Color.WHITE);
        }
    }

}
