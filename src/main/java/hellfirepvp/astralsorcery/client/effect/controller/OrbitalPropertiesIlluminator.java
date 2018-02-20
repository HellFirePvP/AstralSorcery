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
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalPropertiesIlluminator
 * Created by HellFirePvP
 * Date: 02.11.2016 / 00:20
 */
public class OrbitalPropertiesIlluminator implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect {

    private static final Random rand = new Random();

    private final BlockPos thisPos;
    private final int dim;

    public OrbitalPropertiesIlluminator(TileIlluminator tile) {
        this.thisPos = tile.getPos();
        this.dim = tile.getWorld().provider.getDimension();
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        World w = Minecraft.getMinecraft().world;
        return w.provider.getDimension() == dim && w.getBlockState(thisPos).getBlock().equals(BlocksAS.blockIlluminator);
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if(!Minecraft.isFancyGraphicsEnabled()) return;
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX(),
                pos.getY(),
                pos.getZ());
        p.setMaxAge(25);
        switch (rand.nextInt(3)) {
            case 0:
                p.setColor(Color.WHITE);
                break;
            case 1:
                p.setColor(new Color(0xFEFF9E));
                break;
            case 2:
                p.setColor(new Color(0xFFE539));
                break;
        }
        p.scale(0.1F).gravity(0.004);
        if(rand.nextInt(4) == 0) {
            p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion((rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
            p.setMaxAge(35);
            switch (rand.nextInt(2)) {
                case 0:
                    p.setColor(new Color(0xFEFF9E));
                    break;
                case 1:
                    p.setColor(new Color(0xFFE539));
                    break;
            }
            p.scale(0.15F);
        }
        /*if(rand.nextBoolean()) {
            p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion((rand.nextFloat() * 0.002F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.002F) * (rand.nextBoolean() ? 1 : -1),
                    (rand.nextFloat() * 0.002F) * (rand.nextBoolean() ? 1 : -1));
            p.setMaxAge(5);
            p.scale(0.15F);
        }*/
    }

}
