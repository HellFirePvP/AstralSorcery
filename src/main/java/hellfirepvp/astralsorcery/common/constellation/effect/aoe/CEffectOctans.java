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
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.tile.TileRitualPedestal;
import hellfirepvp.astralsorcery.common.util.ILocatable;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectOctans
 * Created by HellFirePvP
 * Date: 10.01.2017 / 18:34
 */
public class CEffectOctans extends CEffectPositionListGen<GenListEntries.CounterMaxListEntry> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 12;
    public static int maxFishingGrounds = 20;

    public static int minFishTickTime = 1000;
    public static int maxFishTickTime = 5000;

    public CEffectOctans(@Nullable ILocatable origin) {
        super(origin, Constellations.octans, "octans", searchRange, maxFishingGrounds, (world, pos) -> {
            IBlockState at = world.getBlockState(pos);
            return at.getBlock() instanceof BlockLiquid && at.getBlock().getMaterial(at).equals(Material.WATER) && at.getValue(BlockLiquid.LEVEL) == 0 && world.isAirBlock(pos.up());
        }, (pos) -> new GenListEntries.CounterMaxListEntry(pos, minFishTickTime + rand.nextInt(maxFishTickTime - minFishTickTime + 1)));
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        GenListEntries.CounterMaxListEntry entry = getRandomElementByChance(rand);
        if(entry != null) {
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(entry.getPos()))) {
                if(!verifier.isValid(world, entry.getPos())) {
                    removeElement(entry);
                    changed = true;
                } else {
                    do {
                        entry.counter++;
                        percStrength -= 0.1;
                    } while (rand.nextFloat() < percStrength);
                    changed = true;
                    if(entry.counter >= entry.maxCount) {
                        Vector3 dropLoc = new Vector3(entry.getPos()).add(0.5, 0.85, 0.5);
                        entry.maxCount = minFishTickTime + rand.nextInt(maxFishTickTime - minFishTickTime + 1);
                        entry.counter = 0;
                        LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
                        builder.withLuck(rand.nextInt(2) * rand.nextFloat());
                        for(ItemStack loot : world.getLootTableManager().getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING).generateLootForPools(rand, builder.build())) {
                            EntityItem ei = ItemUtils.dropItemNaturally(world, dropLoc.getX(), dropLoc.getY(), dropLoc.getZ(), loot);
                            ei.motionY = Math.abs(ei.motionY);
                        }
                    }
                    PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_WATER_FISH, entry.getPos());
                    PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, entry.getPos(), 8));
                }
            }
        }

        if(findNewPosition(world, pos)) changed = true;

        return changed;
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, IMinorConstellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 12, 1, 32, "Defines the radius (in blocks) in which the ritual will search for water ");
        maxFishingGrounds = cfg.getInt(getKey() + "Count", getConfigurationSection(), 20, 1, 4000, "Defines the amount of crops the ritual can cache at max. count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
        minFishTickTime = cfg.getInt(getKey() + "MinFishTickTime", getConfigurationSection(), 100, 20, Integer.MAX_VALUE, "Defines the minimum default tick-time until a fish may be fished by the ritual. gets reduced internally the more starlight was provided at the ritual.");
        maxFishTickTime = cfg.getInt(getKey() + "MaxFishTickTime", getConfigurationSection(), 500, 20, Integer.MAX_VALUE, "Defines the maximum default tick-time until a fish may be fished by the ritual. gets reduced internally the more starlight was provided at the ritual. Has to be bigger as the minimum time; if it isn't it'll be set to the minimum.");

        if(maxFishTickTime < minFishTickTime) {
            maxFishTickTime = minFishTickTime;
        }

    }

    @SideOnly(Side.CLIENT)
    public static void playParticles(PktParticleEvent event) {
        Vector3 at = event.getVec();
        EntityFXFacingParticle p = EffectHelper.genericFlareParticle(
                at.getX() + rand.nextFloat(),
                at.getY() + rand.nextFloat(),
                at.getZ() + rand.nextFloat());
        p.motion(0, 0.03 + rand.nextFloat() * 0.01, 0).setMaxAge(5 + rand.nextInt(5));
        p.scale(0.2F).setColor(Color.CYAN).gravity(-0.03);
    }

}
