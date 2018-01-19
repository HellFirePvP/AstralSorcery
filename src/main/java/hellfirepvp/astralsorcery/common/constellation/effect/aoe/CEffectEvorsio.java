/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystal;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualLink;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.*;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
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
        super(origin, Constellations.evorsio, "evorsio", searchRange, 2, (w, pos) -> isAllowedToBreak(origin, w, pos), (pos) -> null);
    }

    private static boolean isAllowedToBreak(@Nullable ILocatable origin, World world, BlockPos pos) {
        if(!MiscUtils.isChunkLoaded(world, pos)) return false;
        float hardness = world.getBlockState(pos).getBlockHardness(world, pos);
        if(world.isAirBlock(pos) || hardness < 0 || hardness > 75) {
            return false;
        }
        if(origin != null && MiscUtils.isChunkLoaded(world, origin.getPos())) {
            TileRitualPedestal pedestal = MiscUtils.getTileAt(world, origin.getPos(), TileRitualPedestal.class, true);
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
                    if(copyResizedPedestal.hasBlockAt(pos.subtract(origin.getPos()))) {
                        return false;
                    }
                }
            } else {
                TileRitualLink link = MiscUtils.getTileAt(world, origin.getPos(), TileRitualLink.class, true);
                if(link != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
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
                }
            }
        }
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        if(world instanceof WorldServer && findNewPosition(world, pos)) {
            BlockBreakAssist.BreakEntry be = getRandomElement(world.rand);
            if(be != null) {
                removeElement(be);
                BlockDropCaptureAssist.startCapturing();
                MiscUtils.breakBlockWithoutPlayer((WorldServer) world, be.getPos(), world.getBlockState(be.getPos()), true, true, true);
                NonNullList<ItemStack> captured = BlockDropCaptureAssist.getCapturedStacksAndStop();
                captured.forEach((stack) -> ItemUtils.dropItem(world, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, stack));
                PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_BREAK_BLOCK, be.getPos());
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, be.getPos(), 16));
            }
        }
        return false;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
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
            }
        }
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), searchRange, 1, 32, "Defines the radius (in blocks) in which the ritual will search for blocks to break.");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), enabled, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), potencyMultiplier, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
