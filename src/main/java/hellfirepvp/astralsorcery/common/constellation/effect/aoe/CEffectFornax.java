package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.base.WorldMeltables;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectFornax
 * Created by HellFirePvP
 * Date: 01.11.2016 / 01:35
 */
public class CEffectFornax extends CEffectPositionListGen<WorldMeltables.ActiveMeltableEntry> {

    //public static final int MAX_SEARCH_RANGE = 12;
    //public static final int MAX_MELT_COUNT = 40;

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 12;
    public static int maxCount = 40;
    public static double meltDurationDivisor = 1;

    public CEffectFornax() {
        super(Constellations.fornax, "fornax", searchRange, maxCount, (world, pos) -> WorldMeltables.getMeltable(world, pos) != null, WorldMeltables.ActiveMeltableEntry::new);
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        WorldMeltables.ActiveMeltableEntry entry = getRandomElementByChance(rand);
        if(entry != null) {
            BlockPos bp = entry.getPos();
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(bp))) {
                if(entry.isValid(world, true)) {
                    removeElement(entry);
                    changed = true;
                } else {
                    entry.counter++;
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_MELT_BLOCK, bp.getX(), bp.getY(), bp.getZ());
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, bp, 16));
                    WorldMeltables melt = entry.getMeltable(world);
                    if(entry.counter >= (melt.getMeltDuration() / meltDurationDivisor)) {
                        world.setBlockState(bp, melt.getMeltResult());
                        removeElement(entry);
                    }
                    changed = true;
                }
            }
        }

        if(findNewPosition(world, pos)) changed = true;

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
        p.motion(0, 0.016 + rand.nextFloat() * 0.02, 0);
        p.scale(0.25F).setColor(Color.RED);
    }

    //TODO add failure chance option.

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 12, 1, 32, "Defines the radius (in blocks) in which the ritual will search for valid blocks to start to melt.");
        maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 40, 1, 4000, "Defines the amount of block-positions the ritual can cache and melt at max count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        meltDurationDivisor = cfg.getFloat(getKey() + "Divisor", getConfigurationSection(), 1, 0.0001F, 200F, "Defines a multiplier used to determine how long it needs to melt a block. normal duration * durationMultiplier = actual duration");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
