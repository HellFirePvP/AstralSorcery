package hellfirepvp.astralsorcery.common.constellation.perk.impl;

import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerk;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCreationStoneEnrichment
 * Created by HellFirePvP
 * Date: 04.12.2016 / 16:01
 */
public class PerkCreationStoneEnrichment extends ConstellationPerk {

    private static final BlockStateCheck stoneCheck = new CleanStoneCheck();

    private static int enrichmentRadius = 3;
    private static int chanceToEnrich = 90;

    public PerkCreationStoneEnrichment() {
        super("CRE_ORES", Target.PLAYER_TICK);
    }

    @Override
    public void onPlayerTick(EntityPlayer player, Side side) {
        if(side == Side.SERVER) {
            if(rand.nextInt(chanceToEnrich) == 0) {
                BlockPos pos = player.getPosition().add(
                        rand.nextInt(enrichmentRadius * 2) - enrichmentRadius,
                        rand.nextInt(enrichmentRadius * 2) - enrichmentRadius,
                        rand.nextInt(enrichmentRadius * 2) - enrichmentRadius);
                if(stoneCheck.isStateValid(player.getEntityWorld(), pos, player.getEntityWorld().getBlockState(pos))) {
                    ItemStack blockStack = OreTypes.getRandomOre(rand);
                    //if(rand.nextInt(200_000) == 0) blockStack = new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal());
                    if(blockStack != null) {
                        Block b = Block.getBlockFromItem(blockStack.getItem());
                        if(b != null) {
                            player.getEntityWorld().setBlockState(pos, Block.getBlockFromItem(blockStack.getItem()).getStateFromMeta(blockStack.getItemDamage()));
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean hasConfigEntries() {
        return true;
    }

    @Override
    public void loadFromConfig(Configuration cfg) {
        enrichmentRadius = cfg.getInt(getKey() + "GenRadius", getConfigurationSection(), 3, 2, 50, "Defines the radius where a random position to generate a ore at is searched");
        chanceToEnrich = cfg.getInt(getKey() + "GenChance", getConfigurationSection(), 90, 10, 2000, "Sets the chance (Random.nextInt(chance) == 0) to try to see if a random stone next to the player should get turned into an ore");
    }

    private static class CleanStoneCheck implements BlockStateCheck {

        @Override
        public boolean isStateValid(World world, BlockPos pos, IBlockState state) {
            return state.getBlock() == Blocks.STONE && state.getValue(BlockStone.VARIANT).equals(BlockStone.EnumType.STONE);
        }

    }
}
