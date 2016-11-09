package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionListGen;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectCircinus
 * Created by HellFirePvP
 * Date: 09.11.2016 / 00:45
 */
public class CEffectCircinus extends CEffectPositionListGen<CropHelper.HarvestablePlant> {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 16;
    public static int maxCount = 200;

    public CEffectCircinus() {
        super(Constellations.circinus, "circinus", searchRange, maxCount, (world, pos) -> CropHelper.wrapHarvestablePlant(world, pos) != null, CropHelper.HarvestableWrapper::new);
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable Constellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;
        CropHelper.HarvestablePlant plant = getRandomElementByChance(rand);
        if(plant != null) {
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(plant.getPos()))) {
                if(!plant.isValid(world, true)) {
                    removeElement(plant);
                    changed = true;
                } else {
                    if(plant.canHarvest(world)) {
                        Vector3 dropLoc = new Vector3(plant.getPos()).add(0.5, 0.05, 0.5);
                        List<ItemStack> drops = plant.harvestDropsAndReplant(world, rand, rand.nextInt(4));
                        for (ItemStack stack : drops) {
                            ItemUtils.dropItemNaturally(world, dropLoc.getX(), dropLoc.getY(), dropLoc.getZ(), stack);
                        }
                        PktParticleEvent ev = new PktParticleEvent(PktParticleEvent.ParticleEventType.CE_CROP_INTERACT, plant.getPos());
                        PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(world, plant.getPos(), 8));
                    }
                }
            }
        }

        if(findNewPosition(world, pos)) changed = true;

        return changed;
    }

    @Override
    public CropHelper.HarvestablePlant newElement(World world, BlockPos at) {
        return CropHelper.wrapHarvestablePlant(world, at);
    }

    @Override
    public boolean playTraitEffect(World world, BlockPos pos, Constellation traitType, float traitStrength) {
        return false;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 16, 1, 32, "Defines the radius (in blocks) in which the ritual will search for valid crops.");
        maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 200, 1, 4000, "Defines the amount of crops the ritual can cache at max. count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
