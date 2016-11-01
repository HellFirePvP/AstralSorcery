package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.WorldMeltables;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionMap;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectFornax
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:35
 */
public class CEffectFornax extends CEffectPositionMap<CEffectPositionMap.EntryInteger> {

    public static final int MAX_SEARCH_RANGE = 12;
    public static final int MAX_MELT_COUNT = 40;

    public CEffectFornax() {
        super(Constellations.fornax, MAX_SEARCH_RANGE, MAX_MELT_COUNT, (world, pos) -> WorldMeltables.getMeltable(world, pos) != null);
    }

    @Override
    public EntryInteger provideNewPositionEntry(BlockPos pos) {
        return new EntryInteger();
    }

    @Override
    public void playClientEffect(World world, BlockPos pos, TileRitualPedestal pedestal, float percEffectVisibility, boolean extendedEffects) {}

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        if(doRandomOnPositions(world)) {
            int index = world.rand.nextInt(positions.size());
            BlockPos bp = new LinkedList<>(positions.keySet()).get(index);
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(bp))) {
                WorldMeltables melt = WorldMeltables.getMeltable(world, bp);
                if(melt == null) {
                    positions.remove(bp);
                    changed = true;
                } else {
                    EntryInteger entry = positions.get(bp);
                    entry.value++;
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_MELT_BLOCK, bp.getX(), bp.getY(), bp.getZ());
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, bp, 16));
                    if(entry.value >= melt.getMeltDuration()) {
                        world.setBlockState(bp, melt.getMeltResult());
                        positions.remove(bp);
                    }
                    changed = true;
                }
            }
        }

        if(super.playMainEffect(world, pos, percStrength, mayDoTraitEffect, possibleTraitEffect)) changed = true;

        return changed;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                at.getX() + rand.nextFloat(),
                at.getY() + 0.2,
                at.getZ() + rand.nextFloat());
        p.motion(0, 0.01 + rand.nextFloat() * 0.02, 0);
        p.scale(0.2F).setColor(Color.RED);
    }

}
