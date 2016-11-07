package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalEffectLucerna
 * Created by HellFirePvP
 * Date: 07.11.2016 / 12:21
 */
public class OrbitalEffectLucerna implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect, OrbitalEffectController.OrbitTickModifier {

    private static final Random rand = new Random();

    private int count = 2 + rand.nextInt(2);

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        count--;
        return count > 0;
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if(!Minecraft.isFancyGraphicsEnabled()) return;
        if(rand.nextInt(2) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.setMaxAge(45);
            p.offset((rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
            p.setColor(new Color(255, 255, 127));
            p.scale(0.25F).gravity(0.008);
        }
        if(rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion((rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.025F) * (rand.nextBoolean() ? 1 : -1));
            p.setMaxAge(35);
            p.scale(0.25F).setColor(Color.WHITE);
        }
    }

    @Override
    public void onTick(OrbitalEffectController controller) {
        controller.getOffset().add(0, 0.05, 0);
    }

}
