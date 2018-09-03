/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.controller.orbital;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.tile.TileStructController;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalEffectConduit
 * Created by HellFirePvP
 * Date: 17.08.2018 / 22:09
 */
public class OrbitalEffectConduit implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect, OrbitalEffectController.OrbitTickModifier {

    private static final Random rand = new Random();

    private final float finalRadius;

    public OrbitalEffectConduit(float finalRad) {
        this.finalRadius = finalRad;
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        return false;
    }

    @Override
    public void onTick(OrbitalEffectController controller) {
        float percentage = ((float) controller.getAge()) / ((float) controller.getMaxAge());
        controller.setOrbitRadius(this.finalRadius * percentage);
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if(!Minecraft.isFancyGraphicsEnabled()) return;

        float percentage = ((float) ctrl.getAge()) / ((float) ctrl.getMaxAge());

        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX(),
                pos.getY(),
                pos.getZ());
        p.setMaxAge(15);
        p.scale(0.2F);
        p.setColor(new Color(0x3C00FF));
        if (rand.nextInt(4) == 0) {
            p.setColor(Color.WHITE);
        }
        p.gravity(0.008);

        p = EffectHelper.genericFlareParticle(
                pos.getX(),
                pos.getY(),
                pos.getZ());
        p.motion((rand.nextFloat() * 0.025F) * percentage * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.025F) * percentage * (rand.nextBoolean() ? 1 : -1),
                (rand.nextFloat() * 0.025F) * percentage * (rand.nextBoolean() ? 1 : -1));
        p.setMaxAge(25);
        p.scale(0.15F);
        p.setColor(new Color(0x3C00FF).brighter());
        if (rand.nextInt(4) == 0) {
            p.setColor(Color.WHITE);
        }
    }
}
