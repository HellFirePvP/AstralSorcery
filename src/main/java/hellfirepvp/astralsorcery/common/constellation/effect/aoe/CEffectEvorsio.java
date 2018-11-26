/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProperties;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectEvorsio
 * Created by HellFirePvP
 * Date: 26.07.2017 / 16:15
 */
public class CEffectEvorsio extends CEffectPositionListGen<BlockBreakAssist.BreakEntry> {

    private static BlockArray copyResizedPedestal = null;

    public static boolean enabled = true;
    public static float potencyMultiplier = 1F;
    public static int searchRange = 13;

    public CEffectEvorsio(@Nullable ILocatable origin) {
        super(origin, Constellations.evorsio, "evorsio", 2, (w, pos) -> isAllowedToBreak(origin, w, pos), (pos) -> null);
    }

    private static boolean isAllowedToBreak(@Nullable ILocatable origin, World world, BlockPos pos) {
        if(!MiscUtils.isChunkLoaded(world, pos)) return false;
        float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
        if(world.isAirBlock(pos) || hardness < 0 || hardness > 75) {
            return false;
        }
        if(origin != null && MiscUtils.isChunkLoaded(world, origin.getLocationPos())) {
            BlockPos originPedestal = origin.getLocationPos();
            TileRitualLink link = MiscUtils.getTileAt(world, originPedestal, TileRitualLink.class, true);
            if(link != null && link.getLinkedTo() != null && MiscUtils.isChunkLoaded(world, link.getLinkedTo())) {
                originPedestal = link.getLinkedTo();
            }
            TileRitualPedestal pedestal = MiscUtils.getTileAt(world, originPedestal, TileRitualPedestal.class, true);
            if(pedestal != null) {
                if(copyResizedPedestal == null) {
                    if(MultiBlockArrays.patternRitualPedestalWithLink != null) {
                        copyResizedPedestal = new BlockArray();
                        for (int i = 0; i < 5; i++) {
                            int finalI = i;
                            copyResizedPedestal.addAll(MultiBlockArrays.patternRitualPedestalWithLink, (p) -> p.add(0, finalI, 0));
                        }
                    }
                }
                if(copyResizedPedestal != null) {
                    if(copyResizedPedestal.hasBlockAt(pos.subtract(originPedestal))) {
                        return false;
                    }
                }
                return true;
            }
            // Critical state: Has a link leading into a nonexistent pedestal OR
            // is an unlinked anchor casting a ritual...
            return true;
        }
        return true;
    }

    @Override
    public BlockBreakAssist.BreakEntry newElement(World world, BlockPos at) {
        return new BlockBreakAssist.BreakEntry(0F, world, at, world.getBlockState(at));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {
        if(rand.nextInt(4) == 0) {
            Vector3 at = new Vector3(
                    pos.getX() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5,
                    pos.getY() + rand.nextFloat() * 2 + 0.5,
                    pos.getZ() + rand.nextFloat() * 5 * (rand.nextBoolean() ? 1 : -1) + 0.5);
            for (int i = 0; i < 15; i++) {
                EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
                p.gravity(0.004);
                p.scale(0.25F).setMaxAge(35 + rand.nextInt(10));
                Vector3 mot = new Vector3();
                MiscUtils.applyRandomOffset(mot, rand, 0.01F * rand.nextFloat() + 0.01F);
                p.motion(mot.getX(), mot.getY(), mot.getZ());
                switch (rand.nextInt(3)) {
                    case 0:
                    case 1:
                        p.setColor(Constellations.evorsio.getConstellationColor());
                        break;
                    case 2:
                        p.setColor(Constellations.armara.getConstellationColor());
                        break;
                    default:
                        break;
                }
            }
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
            double searchRange = modified.getSize();
            double offX = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offY = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            double offZ = -searchRange + world.rand.nextFloat() * (2 * searchRange + 1);
            BlockPos at = pos.add(offX, offY, offZ);
            if(!world.isAirBlock(at) && !world.getBlockState(at).getBlock().isReplaceable(world, at)) {
                return false;
            }
            IBlockState toSet = rand.nextBoolean() ? Blocks.DIRT.getDefaultState() : Blocks.STONE.getDefaultState();
            if(rand.nextInt(20) == 0) {
                ItemStack randOre = OreTypes.RITUAL_MINERALIS.getNonWeightedOre(rand);
                if(!randOre.isEmpty()) {
                    IBlockState state = ItemUtils.createBlockState(randOre);
                    if(state != null) {
                        toSet = state;
                    }
                }
            }
            TileRitualLink link = MiscUtils.getTileAt(world, pos, TileRitualLink.class, false);
            if(link != null) {
                if(!at.equals(pos)) {
                    if(world.setBlockState(at, toSet, 2)) {
                        world.playEvent(2001, at, Block.getStateId(toSet));
                        return true;
                    }
                }
            } else {
                TileRitualPedestal ped = MiscUtils.getTileAt(world, pos, TileRitualPedestal.class, false);
                if(ped != null) {
                    if(at.getZ() == pos.getZ() && at.getX() == pos.getX()) {
                        return false;
                    }
                    BlockArray ba = new BlockArray();
                    if(MultiBlockArrays.patternRitualPedestalWithLink != null) {
                        for (int i = 0; i < 5; i++) {
                            int finalI = i;
                            ba.addAll(MultiBlockArrays.patternRitualPedestalWithLink, (p) -> p.add(pos).add(0, finalI, 0));
                        }
                        if(!ba.hasBlockAt(at)) {
                            if(world.setBlockState(at, toSet, 2)) {
                                world.playEvent(2001, at, Block.getStateId(toSet));
                                return true;
                            }
                        }
                    }
                }
            }

        } else {
            if(world instanceof WorldServer && findNewPosition(world, pos, modified)) {
                BlockBreakAssist.BreakEntry be = getRandomElement(world.rand);
                if(be != null) {
                    removeElement(be);

                    boolean broken = false;
                    BlockDropCaptureAssist.startCapturing();
                    try {
                        broken = MiscUtils.breakBlockWithoutPlayer((WorldServer) world, be.getPos(), world.getBlockState(be.getPos()), true, true, true);
                    } finally {
                        NonNullList<ItemStack> captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                        captured.forEach((stack) -> ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, stack));
                    }
                    if (broken) {
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_BREAK_BLOCK, be.getPos());
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, be.getPos(), 16));
                    }
                }
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playBreakEffects(PktParticleEvent pktParticleEvent) {
        Vector3 at = pktParticleEvent.getVec().add(0.5, 0.5, 0.5);
        for (int i = 0; i < 15; i++) {
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(at.getX(), at.getY(), at.getZ());
            p.gravity(0.004);
            p.scale(0.25F).setMaxAge(35 + rand.nextInt(10));
            Vector3 mot = new Vector3();
            MiscUtils.applyRandomOffset(mot, rand, 0.01F * rand.nextFloat() + 0.01F);
            p.motion(mot.getX(), mot.getY(), mot.getZ());
            switch (rand.nextInt(3)) {
                case 0:
                    p.setColor(Color.WHITE);
                    break;
                case 1:
                    p.setColor(Constellations.evorsio.getConstellationColor());
                    break;
                case 2:
                    p.setColor(Constellations.armara.getConstellationColor());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public ConstellationEffectProperties provideProperties(int mirrorCount) {
        return new ConstellationEffectProperties(CEffectEvorsio.searchRange);
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), searchRange, 1, 32, "Defines the radius (in blocks) in which the ritual will search for blocks to break.");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), enabled, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), potencyMultiplier, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
