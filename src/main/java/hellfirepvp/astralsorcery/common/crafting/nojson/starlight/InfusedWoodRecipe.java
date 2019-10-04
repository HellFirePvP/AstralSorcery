package hellfirepvp.astralsorcery.common.crafting.nojson.starlight;

import hellfirepvp.astralsorcery.common.block.infusedwood.BlockInfusedWood;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.item.block.ItemBlockCustom;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.LogBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InfusedWoodRecipe
 * Created by HellFirePvP
 * Date: 01.10.2019 / 20:48
 */
public class InfusedWoodRecipe extends LiquidStarlightRecipe {

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<Ingredient> getInputForRender() {
        return Arrays.asList(Ingredient.fromStacks(new ItemStack(Items.OAK_LOG)));
    }

    @Override
    public boolean doesStartRecipe(ItemStack item) {
        return item.getItem().equals(Blocks.OAK_LOG.asItem());
    }

    @Override
    public boolean matches(ItemEntity trigger, World world, BlockPos at) {
        return true;
    }

    @Override
    public void doCraftTick(ItemEntity trigger, World world, BlockPos at) {
        if (!world.isRemote() && getAndIncrementCraftingTick(trigger) > 10) {
            if (consumeItemEntityInBlock(world, at, Blocks.OAK_LOG.asItem()) != null) {
                ItemUtils.dropItemNaturally(world, trigger.posX, trigger.posY, trigger.posZ, new ItemStack(BlocksAS.INFUSED_WOOD));
            }
        }
    }
}
