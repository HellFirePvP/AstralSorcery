/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.controller.RenderOffsetControllerFornax;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.MeltInteraction;
import hellfirepvp.astralsorcery.common.base.WorldMeltables;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectFornax
 * Created by HellFirePvP
 * Date: 20.04.2017 / 21:20
 */
public class CEffectFornax extends CEffectPositionListGen<WorldMeltables.ActiveMeltableEntry> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;
    public static double failChance = 0;

    public static int searchRange = 12;
    public static int maxCount = 40;
    public static double meltDurationDivisor = 1;

    public CEffectFornax(@Nullable ILocatable origin) {
        super(origin, Constellations.fornax, "fornax", maxCount, (world, pos) -> WorldMeltables.getMeltable(world, pos) != null, WorldMeltables.ActiveMeltableEntry::new);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 4 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY(),
                    pos.getZ() + rand.nextFloat() * 4 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.motion(0, rand.nextFloat() * 0.02 + 0.015, 0).gravity(0.004);
            p.setRenderOffsetController(new RenderOffsetControllerFornax()).enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(0.25F).setColor(new Color(234, 59, 0)).setMaxAge(rand.nextInt(10) + 20);
        }
        if(rand.nextBoolean()) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    pos.getX() + rand.nextFloat() * 2 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY(),
                    pos.getZ() + rand.nextFloat() * 2 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            p.gravity(0.004);
            p.enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(0.25F).setColor(new Color(234, 59, 0)).setMaxAge(rand.nextInt(10) + 20);
        }
    }

    @Override
    public boolean playEffect(World world, BlockPos pos, float percStrength, ConstellationEffectProperties modified, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        if(modified.isCorrupted()) {
            boolean changed = false;
            double searchRange = modified.getSize();
            double offX = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offY = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offZ = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            BlockPos at = pos.add(offX, offY, offZ);
            IBlockState state = world.getBlockState(at);
            if(state.getBlock().equals(Blocks.WATER) &&
                    state.getBlock() instanceof BlockStaticLiquid) {
                if(state.getValue(BlockStaticLiquid.LEVEL) == 0) {
                    if (world.setBlockState(at, Blocks.PACKED_ICE.getDefaultState())) {
                        changed = true;
                    }
                }
            } else if(state.getBlock().equals(Blocks.LAVA) &&
                    state.getBlock() instanceof BlockStaticLiquid) {
                if(state.getValue(BlockStaticLiquid.LEVEL) == 0) {
                    if (world.setBlockState(at, Blocks.OBSIDIAN.getDefaultState())) {
                        changed = true;
                    }
                }
            } else if(state.getBlock().equals(Blocks.FIRE)) {
                if (world.setBlockToAir(at)) {
                    changed = true;
                }
            } else if(state.getBlock() instanceof BlockFluidBase) {
                if(state.getValue(BlockFluidBase.LEVEL) == 0) {
                    IBlockState generate = Blocks.STONE.getDefaultState();
                    Fluid f = ((BlockFluidBase) state.getBlock()).getFluid();
                    if(f != null) {
                        if(f.getTemperature(world, at) <= 200) {
                            generate = Blocks.PACKED_ICE.getDefaultState();
                        } else if(f.getTemperature(world, at) >= 500) {
                            generate = Blocks.OBSIDIAN.getDefaultState();
                        }
                    }
                    if (world.setBlockState(at, generate)) {
                        changed = true;
                    }
                }
            } else if (state.getBlock().isAir(state, world, at) && state.getBlock().isReplaceable(world, at)) {
                if (world.setBlockState(at, Blocks.ICE.getDefaultState())) {
                    changed = true;
                }
            }
            return changed;
        } else {
            boolean changed = false;
            WorldMeltables.ActiveMeltableEntry entry = getRandomElementByChance(rand);
            if(entry != null) {
                BlockPos bp = entry.getPos();
                if(MiscUtils.isChunkLoaded(world, new ChunkPos(bp))) {
                    if(!entry.isValid(world, true)) {
                        removeElement(entry);
                        changed = true;
                    } else {
                        entry.counter++;
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_MELT_BLOCK, bp.getX(), bp.getY(), bp.getZ());
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, bp, 16));
                        MeltInteraction melt = entry.getMeltable(world);
                        if(melt.isMeltable(world, bp, world.getBlockState(bp)) && entry.counter >= (melt.getMeltTickDuration() / meltDurationDivisor)) {
                            if(failChance > 0 && rand.nextFloat() <= failChance) {
                                world.setBlockToAir(bp);
                            } else {
                                melt.placeResultAt(world, bp);
                                for (EnumFacing f : EnumFacing.VALUES) {
                                    BlockPos test = bp.offset(f);
                                    if(findNewPositionAt(world, pos, test, modified) && rand.nextBoolean()) {
                                        break;
                                    }
                                }
                            }
                            removeElement(entry);
                        }
                        changed = true;
                    }
                }
            }

            if(findNewPosition(world, pos, modified)) changed = true;

            return changed;
        }
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        for (int i = 0; i < 5; i++) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                    at.getX() + rand.nextFloat(),
                    at.getY() + 0.2,
                    at.getZ() + rand.nextFloat());
            p.motion(0, 0.016 + rand.nextFloat() * 0.02, 0);
            p.scale(0.2F).setColor(Color.RED);
        }
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectFornax.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 12, 1, 32, "Defines the radius (in blocks) in which the ritual will search for valid blocks to start to melt.");
        maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 40, 1, 4000, "Defines the amount of block-positions the ritual can cache and melt at max count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        failChance = cfg.getFloat(getKey() + "FailChance", getConfigurationSection(), 0F, 0F, 1F, "Defines the chance (0% to 100% -> 0.0 to 1.0) if the block will be replaced with air instead of being properly melted into something.");
        meltDurationDivisor = cfg.getFloat(getKey() + "Divisor", getConfigurationSection(), 1, 0.0001F, 200F, "Defines a multiplier used to determine how long it needs to melt a block. normal duration * durationMultiplier = actual duration");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
