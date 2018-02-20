/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXCrystalBurst;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.BlockCelestialCrystals;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.WorldSkyHandler;
import hellfirepvp.astralsorcery.common.data.DataActiveCelestials;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.base.TileSkybound;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
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
    private static Vector3[] crystalEffectPositions = new Vector3[] {
            new Vector3(0.5, 0.5, 0.5)
    };

    public int getGrowth() {
        IBlockState state = world.getBlockState(getPos());
        if(!(state.getBlock() instanceof BlockCelestialCrystals)) return 0;
        return state.getValue(BlockCelestialCrystals.STAGE);
    }

    @Override
    public void update() {
        super.update();

        if(!world.isRemote) {
            double mul = 1;
            IBlockState downState = world.getBlockState(getPos().down());
            if(downState.getBlock() == BlocksAS.customOre &&
                    downState.getValue(BlockCustomOre.ORE_TYPE) == BlockCustomOre.OreType.STARMETAL) {
                mul *= 0.3;

                if(rand.nextInt(300) == 0) {
                    world.setBlockState(getPos().down(), Blocks.IRON_ORE.getDefaultState());
                }
            }
            tryGrowth(mul);
        } else {
            IBlockState downState = world.getBlockState(getPos().down());
            if(downState.getBlock() == BlocksAS.customOre &&
                    downState.getValue(BlockCustomOre.ORE_TYPE) == BlockCustomOre.OreType.STARMETAL) {
                playStarmetalOreParticles();
            }
            int stage = getGrowth();
            if(stage == 4) {
                playHarvestEffects();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void playHarvestEffects() {
        if(rand.nextInt(15) == 0) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + 0.3 + rand.nextFloat() * 0.4,
                    pos.getY()       + rand.nextFloat() * 0.1,
                    pos.getZ() + 0.3 + rand.nextFloat() * 0.4);
            p.motion(0, rand.nextFloat() * 0.05, 0);
            p.setColor(Color.WHITE);
            p.scale(0.2F);
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
        IBlockState current = world.getBlockState(getPos());
        int stage = current.getValue(BlockCelestialCrystals.STAGE);
        if(stage < 4) {
            IBlockState next = BlocksAS.celestialCrystals.getStateFromMeta(stage + 1);
            world.setBlockState(getPos(), next);
        }
    }

    public void tryGrowth(double mul) {
        int r = 24000;
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(world);
        if(doesSeeSky() && handle != null) {
            double dstr = ConstellationSkyHandler.getInstance().getCurrentDaytimeDistribution(world);
            if(dstr > 0) {
                Collection<IConstellation> activeConstellations =
                        ((DataActiveCelestials) SyncDataHolder.getDataClient(SyncDataHolder.DATA_CONSTELLATIONS)).getActiveConstellations(world.provider.getDimension());
                if(activeConstellations != null) {
                    r = 9500; //If this dim has sky handling active.
                }
                r *= (0.5 + ((1 - dstr) * 0.5));
            }
        }
        r *= Math.abs(mul);

        if(world.rand.nextInt(Math.max(r, 1)) == 0) {
            grow();
        }
    }
}
