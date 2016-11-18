package hellfirepvp.astralsorcery.common.constellation.effect.aoe;

import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.CEffectPositionList;
import hellfirepvp.astralsorcery.common.constellation.effect.GenListEntries;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectMineralis
 * Created by HellFirePvP
 * Date: 03.11.2016 / 01:31
 */
public class CEffectMineralis extends CEffectPositionList {

    public static boolean enabled = true;
    public static double potencyMultiplier = 1;

    public static int searchRange = 14;
    public static int maxCount = 2;

    public CEffectMineralis() {
        super(Constellations.mineralis, "mineralis", searchRange, maxCount, (world, pos) -> {
            IBlockState state = world.getBlockState(pos);
            return state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).equals(BlockStone.EnumType.STONE);
        });
    }

    @Override
    public boolean playMainEffect(World world, BlockPos pos, float percStrength, boolean mayDoTraitEffect, @Nullable IMinorConstellation possibleTraitEffect) {
        if(!enabled) return false;
        percStrength *= potencyMultiplier;
        if(percStrength < 1) {
            if(world.rand.nextFloat() > percStrength) return false;
        }

        boolean changed = false;

        GenListEntries.SimpleBlockPosEntry entry = getRandomElementByChance(rand);
        if(entry != null) {
            BlockPos sel = entry.getPos();
            if(MiscUtils.isChunkLoaded(world, new ChunkPos(sel))) {
                if(verifier.isValid(world, sel)) {
                    ItemStack blockStack = OreTypes.getRandomOre(rand);
                    if(rand.nextInt(200_000) == 0) blockStack = new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal());
                    if(blockStack != null) {
                        world.setBlockState(sel, Block.getBlockFromItem(blockStack.getItem()).getStateFromMeta(blockStack.getItemDamage()));
                    }
                } else {
                    removeElement(entry);
                    changed = true;
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
        searchRange = cfg.getInt(getKey() + "Range", getConfigurationSection(), 14, 1, 32, "Defines the radius (in blocks) in which the ritual will search for cleanStone to generate ores into.");
        maxCount = cfg.getInt(getKey() + "Count", getConfigurationSection(), 2, 1, 4000, "Defines the amount of block-positions the ritual can cache at max count");
        enabled = cfg.getBoolean(getKey() + "Enabled", getConfigurationSection(), true, "Set to false to disable this ConstellationEffect.");
        potencyMultiplier = cfg.getFloat(getKey() + "PotencyMultiplier", getConfigurationSection(), 1.0F, 0.01F, 100F, "Set the potency multiplier for this ritual effect. Will affect all ritual effects and their efficiency.");
    }

}
