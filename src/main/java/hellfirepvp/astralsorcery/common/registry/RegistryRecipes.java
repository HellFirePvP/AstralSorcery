package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.RecipeSorter;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipes
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:49
 */
public class RegistryRecipes {

    public static void init() {
        RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

        CraftingManager manager = CraftingManager.getInstance();
        FurnaceRecipes furnace = FurnaceRecipes.instance();

        //Testing
        manager.addRecipe(new ShapedLightProximityRecipe(BlocksAS.blockAltar,
                "MMM", "MTM", "M M",
                'M', new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                'T', Blocks.CRAFTING_TABLE));

        furnace.addSmeltingRecipe(new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal()),
                new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.STARMETAL_INGOT.getItemMeta()), 2F);
    }

}
