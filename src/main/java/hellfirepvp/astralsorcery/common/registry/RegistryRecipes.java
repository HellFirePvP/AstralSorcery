package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.crafting.ShapedLightProximityRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.CrystalToolRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TelescopeRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.RecipeSorter;

import static hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryRecipes
 * Created by HellFirePvP
 * Date: 31.07.2016 / 10:49
 */
public class RegistryRecipes {

    public static void init() {
        initVanillaRecipes();

        initAltarRecipes();
    }

    public static void initVanillaRecipes() {
        RecipeSorter.register("LightProximityCrafting", ShapedLightProximityRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");

        CraftingManager manager = CraftingManager.getInstance();
        FurnaceRecipes furnace = FurnaceRecipes.instance();

        manager.addRecipe(new ShapedLightProximityRecipe(BlocksAS.blockAltar,
                "MCM", "MTM", "M M",
                'C', ItemsAS.rockCrystal,
                'M', new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                'T', Blocks.CRAFTING_TABLE));
        manager.addRecipe(new ShapedLightProximityRecipe(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.GLASS_LENS.getItemMeta()),
                " G ", "GAG", " G ",
                'G', Blocks.GLASS_PANE,
                'A', new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta())));

        furnace.addSmeltingRecipe(new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.STARMETAL.ordinal()),
                new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.STARMETAL_INGOT.getItemMeta()), 2F);
    }

    public static void initAltarRecipes() {
        registerAltarRecipe(new TelescopeRecipe());

        registerDiscoveryRecipe(new ShapedRecipe(BlocksAS.blockAltar)
                .addPart(Blocks.CRAFTING_TABLE, ShapedRecipeSlot.CENTER)
                .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_CENTER)
                .addPart(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.RAW.ordinal()),
                        ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_RIGHT,
                        ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT,
                        ShapedRecipeSlot.LOWER_LEFT, ShapedRecipeSlot.LOWER_RIGHT));
        registerDiscoveryRecipe(new ShapedRecipe(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.GLASS_LENS.getItemMeta()))
                .addPart(Blocks.GLASS_PANE, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT, ShapedRecipeSlot.RIGHT, ShapedRecipeSlot.LOWER_CENTER)
                .addPart(new ItemStack(ItemsAS.craftingComponent, 1, ItemCraftingComponent.MetaType.AQUAMARINE.getItemMeta()), ShapedRecipeSlot.CENTER));

        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.UPPER_CENTER));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.rockCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT));

        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalSword)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.CENTER, ShapedRecipeSlot.UPPER_CENTER));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalShovel)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.UPPER_CENTER),
                ShapedRecipeSlot.UPPER_CENTER));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalPickaxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.UPPER_RIGHT));
        registerAltarRecipe(new CrystalToolRecipe(
                new ShapedRecipe(ItemsAS.crystalAxe)
                        .addPart(Items.STICK, ShapedRecipeSlot.LOWER_CENTER, ShapedRecipeSlot.CENTER)
                        .addPart(ItemsAS.celestialCrystal, ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT),
                ShapedRecipeSlot.UPPER_LEFT, ShapedRecipeSlot.UPPER_CENTER, ShapedRecipeSlot.LEFT));
    }

}
