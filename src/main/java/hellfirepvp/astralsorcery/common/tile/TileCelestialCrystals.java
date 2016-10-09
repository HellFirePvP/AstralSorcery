package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.common.block.BlockCelestialCrystals;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSpawnWorldParticles;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFirework;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialCrystals
 * Created by HellFirePvP
 * Date: 15.09.2016 / 00:13
 */
public class TileCelestialCrystals extends TileSkybound {
    //Just in case you wonder. i do have a reason to control growth in the TileEntity other than just in the block itself.

    private static final Random rand = new Random();

    public int getGrowth() {
        return worldObj.getBlockState(getPos()).getValue(BlockCelestialCrystals.STAGE);
    }

    @Override
    public void update() {
        super.update();

        if(!worldObj.isRemote) {
            double mul = 1;
            IBlockState downState = worldObj.getBlockState(getPos().down());
            if(downState.getBlock() == BlocksAS.customOre &&
                    downState.getValue(BlockCustomOre.ORE_TYPE) == BlockCustomOre.OreType.STARMETAL) {
                mul *= 0.8;

                if(rand.nextInt(4000) == 0) {
                    worldObj.setBlockState(getPos().down(), Blocks.IRON_ORE.getDefaultState());
                }
            }
            tryGrowth(mul);
        } else {
            IBlockState downState = worldObj.getBlockState(getPos().down());
            if(downState.getBlock() == BlocksAS.customOre &&
                    downState.getValue(BlockCustomOre.ORE_TYPE) == BlockCustomOre.OreType.STARMETAL) {
                playStarmetalOreParticles();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playStarmetalOreParticles() {
        Color c = new Color(130, 0, 255);
        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;
        Particle p = pm.spawnEffectParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(),
                pos.getX()        + rand.nextFloat(),
                pos.down().getY() + rand.nextFloat(),
                pos.getZ()        + rand.nextFloat(),
                0,
                rand.nextFloat() * 0.2,
                0);

        if(p != null && p instanceof ParticleFirework.Spark) {
            p.field_190017_n = false;
            p.setRBGColorF(c.getRed() / 255F, c.getGreen() / 255F, c.getBlue() / 255F);
            p.setAlphaF(80F / 255F);
        }

    }

    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        BlockPos at = event.getPos();
        int id = 19;
        id ^= at.getX();
        id ^= at.getY();
        id ^= at.getZ();
        EffectHandler.getInstance().registerFX(new EntityFXCrystalBurst(id, at.getX() + 0.5, at.getY() + 0.2, at.getZ() + 0.5, 1.5F));
    }

    @Override
    protected void onFirstTick() {}

    public void grow() {
        IBlockState current = worldObj.getBlockState(getPos());
        int stage = current.getValue(BlockCelestialCrystals.STAGE);
        if(stage < 4) {
            IBlockState next = BlocksAS.celestialCrystals.getStateFromMeta(stage + 1);
            worldObj.setBlockState(getPos(), next);
        }
    }

    public void tryGrowth(double mul) {
        int r = 24000;
        if(doesSeeSky()) {
            double dstr = CelestialHandler.calcDaytimeDistribution(worldObj);
            if(dstr > 0) {
                Collection<Constellation> activeConstellations =
                        ((DataActiveCelestials) SyncDataHolder.getDataClient(SyncDataHolder.DATA_CONSTELLATIONS)).getActiveConstellations();
                if(activeConstellations.contains(Constellations.mineralis)) {
                    r = 4200;
                } else {
                    r = 9500;
                }
                r *= (0.5 + ((1 - dstr) * 0.5));
            }
        }
        r *= Math.abs(mul);

        if(worldObj.rand.nextInt(r) == 0) {
            grow();
        }
    }
}
