package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockCelestialCrystals;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
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
        if(rand.nextInt(5) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX()        + rand.nextFloat(),
                    pos.down().getY() + rand.nextFloat(),
                    pos.getZ()        + rand.nextFloat());
            p.motion(0, rand.nextFloat() * 0.05, 0);
            p.scale(0.2F);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void breakParticles(PktParticleEvent event) {
        BlockPos at = event.getVec().toBlockPos();
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
