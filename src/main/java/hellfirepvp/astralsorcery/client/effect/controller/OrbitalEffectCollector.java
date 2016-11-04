package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: OrbitalEffectCollector
 * Created by HellFirePvP
 * Date: 04.11.2016 / 01:58
 */
public class OrbitalEffectCollector implements OrbitalEffectController.OrbitPersistence, OrbitalEffectController.OrbitPointEffect {

    private static final Random rand = new Random();

    private final BlockPos thisPos;
    private final int dim;

    public OrbitalEffectCollector(TileCollectorCrystal tile) {
        this.thisPos = tile.getPos();
        this.dim = tile.getWorld().provider.getDimension();
    }

    @Override
    public boolean canPersist(OrbitalEffectController controller) {
        World w = Minecraft.getMinecraft().theWorld;
        return w.provider.getDimension() == dim && w.getBlockState(thisPos).getBlock().equals(BlocksAS.blockIlluminator);
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        if(rand.nextInt(3) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.setMaxAge(25);
            p.setColor(new Color(70, 50, 255));
            p.scale(0.15F).gravity(0.008);
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
            p.scale(0.15F).setColor(new Color(160, 160, 255));
        }
    }

}
