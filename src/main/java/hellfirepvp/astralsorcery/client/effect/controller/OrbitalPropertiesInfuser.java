/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.tile.TileStarlightInfuser;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalPropertiesInfuser
 * Created by HellFirePvP
 * Date: 11.12.2016 / 18:13
 */
public class OrbitalPropertiesInfuser implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect {

    private static final Random rand = new Random();

    private final TileStarlightInfuser infuser;
    private final boolean mirrored;

    public OrbitalPropertiesInfuser(TileStarlightInfuser infuser, boolean mirrored) {
        this.infuser = infuser;
        this.mirrored = mirrored;
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        return infuser.canCraft() && (mirrored ? infuser.getClientOrbitalCraftingMirror() : infuser.getClientOrbitalCrafting()) != null && infuser.getCraftingTask() != null;
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if(!Minecraft.isFancyGraphicsEnabled()) return;
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX(),
                pos.getY(),
                pos.getZ());
        p.motion((rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                 (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                 (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
        p.setMaxAge(25);
        p.scale(0.2F).gravity(0.004);

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
            p.scale(0.15F).gravity(0.004).setColor(Color.WHITE);
        }
    }

}
