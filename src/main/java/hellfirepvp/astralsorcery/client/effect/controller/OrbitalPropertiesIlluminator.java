package hellfirepvp.astralsorcery.client.effect.controller;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileIlluminator;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
        World w = Minecraft.getMinecraft().theWorld;
        return w.provider.getDimension() == dim && w.getBlockState(thisPos).getBlock().equals(BlocksAS.blockIlluminator);
    }

    @Override
    public void doPointTickEffect(OrbitalEffectController ctrl, Vector3 pos) {
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                pos.getX(),
                pos.getY(),
                pos.getZ());
        p.setMaxAge(25);
        p.scale(0.1F).gravity(0.004);
        if(rand.nextInt(3) == 0) {
            p = EffectHelper.genericFlareParticle(
                    pos.getX(),
                    pos.getY(),
                    pos.getZ());
            p.motion((rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1),
                     (rand.nextFloat() * 0.01F) * (rand.nextBoolean() ? 1 : -1));
            p.setMaxAge(35);
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
