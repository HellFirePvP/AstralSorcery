/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.recipe.RecipeChangeColor;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VanillaRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VanillaRecipeProvider extends RecipeProvider {

    private VanillaRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerCraftingRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemsAS.TOME)
                .pattern("A")
                .pattern("B")
                .define('A', ItemsAS.AQUAMARINE)
                .define('B', Items.BOOK)
                .unlockedBy("has_aquamarine", has(ItemsAS.AQUAMARINE))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ItemsAS.ASTROLABE)
                .pattern("GGG")
                .pattern("PAS")
                .pattern("GGG")
                .define('S', Items.SPYGLASS)
                .define('P', Tags.Items.GLASS_PANES)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('A', TagsAS.Items.GEMS_AQUAMARINE)
                .unlockedBy("has_spyglass", has(Items.SPYGLASS))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_RAW, 8)
                .pattern("MMM")
                .pattern("MCM")
                .pattern("MMM")
                .define('M', BlocksAS.MARBLE_RAW)
                .define('C', ItemTags.COALS)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsAS.PARCHMENT, 1)
                .pattern("A")
                .pattern("P")
                .define('A', TagsAS.Items.GEMS_AQUAMARINE)
                .define('P', Items.PAPER)
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(recipeOutput);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ItemsAS.GLASS_LENS, 1)
                .pattern(" A ")
                .pattern("AGA")
                .pattern(" A ")
                .define('A', TagsAS.Items.GEMS_AQUAMARINE)
                .define('G', Tags.Items.GLASS_PANES)
                .unlockedBy("has_glass", has(Tags.Items.GLASS_PANES))
                .save(recipeOutput);


        // -------------------- Decorative Block Variants --------------------
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_ARCH, 2)
                .pattern("MM")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_ARCH).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_BRICKS, 4)
                .pattern("MM")
                .pattern("MM")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_BRICKS).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_CHISELED, 4)
                .pattern(" M ")
                .pattern("M M")
                .pattern(" M ")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_CHISELED).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_ENGRAVED, 5)
                .pattern(" M ")
                .pattern("MMM")
                .pattern(" M ")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_ENGRAVED).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_PILLAR, 2)
                .pattern("M")
                .pattern("M")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_PILLAR).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_RUNED, 3)
                .pattern("MCM")
                .define('M', BlocksAS.MARBLE_RAW)
                .define('C', BlocksAS.MARBLE_CHISELED)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_RUNED).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_SLAB, 6)
                .pattern("MMM")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_SLAB).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_SLAB, 6)
                .pattern("MMM")
                .define('M', BlocksAS.MARBLE_BRICKS)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_SLAB).withPrefix("marble/").withSuffix("_from_bricks"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_STAIRS, 8)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', BlocksAS.MARBLE_RAW)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_STAIRS).withPrefix("marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.MARBLE_STAIRS, 8)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', BlocksAS.MARBLE_BRICKS)
                .unlockedBy("has_marble", has(ItemsAS.BLOCK_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.MARBLE_STAIRS).withPrefix("marble/").withSuffix("_from_bricks"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_ARCH, 2)
                .pattern("MM")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_ARCH).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_BRICKS, 4)
                .pattern("MM")
                .pattern("MM")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_BRICKS).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_CHISELED, 4)
                .pattern(" M ")
                .pattern("M M")
                .pattern(" M ")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_CHISELED).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_ENGRAVED, 5)
                .pattern(" M ")
                .pattern("MMM")
                .pattern(" M ")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_ENGRAVED).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_PILLAR, 2)
                .pattern("M")
                .pattern("M")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_PILLAR).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_RUNED, 3)
                .pattern("MCM")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .define('C', BlocksAS.SOOTY_MARBLE_CHISELED)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_RUNED).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_SLAB, 6)
                .pattern("MMM")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_SLAB).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_SLAB, 6)
                .pattern("MMM")
                .define('M', BlocksAS.SOOTY_MARBLE_BRICKS)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_SLAB).withPrefix("sooty_marble/").withSuffix("_from_bricks"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_STAIRS, 8)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', BlocksAS.SOOTY_MARBLE_RAW)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_STAIRS).withPrefix("sooty_marble/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.SOOTY_MARBLE_STAIRS, 8)
                .pattern("M  ")
                .pattern("MM ")
                .pattern("MMM")
                .define('M', BlocksAS.SOOTY_MARBLE_BRICKS)
                .unlockedBy("has_sooty_marble", has(ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.SOOTY_MARBLE_STAIRS).withPrefix("sooty_marble/").withSuffix("_from_bricks"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_PLANKS, 4)
                .requires(BlocksAS.INFUSED_WOOD_RAW)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_PLANKS).withPrefix("infused_wood"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_ARCH, 2)
                .pattern("WW")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_ARCH).withPrefix("infused_wood/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_COLUMN, 4)
                .pattern("W")
                .pattern("W")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_COLUMN).withPrefix("infused_wood/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_ENGRAVED, 4)
                .pattern(" W ")
                .pattern("W W")
                .pattern(" W ")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_ENGRAVED).withPrefix("infused_wood/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_ENRICHED, 5)
                .pattern(" W ")
                .pattern("WWW")
                .pattern(" W ")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_ENRICHED).withPrefix("infused_wood/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_INFUSED, 2)
                .pattern(" W ")
                .pattern("WAW")
                .pattern(" W ")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .define('A', TagsAS.Items.GEMS_AQUAMARINE)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_INFUSED).withPrefix("infused_wood/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_SLAB, 6)
                .pattern("WWW")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_SLAB).withPrefix("infused_wood/"));
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BlocksAS.INFUSED_WOOD_STAIRS, 6)
                .pattern("W  ")
                .pattern("WW ")
                .pattern("WWW")
                .define('W', BlocksAS.INFUSED_WOOD_PLANKS)
                .unlockedBy("has_infused_wood", has(ItemsAS.BLOCK_INFUSED_WOOD_RAW))
                .save(recipeOutput, RecipeBuilder.getDefaultRecipeId(BlocksAS.INFUSED_WOOD_STAIRS).withPrefix("infused_wood/"));
    }

    public static void registerStonecuttingRecipes(RecipeOutput recipeOutput) {
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_ARCH, ItemsAS.BLOCK_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_BRICKS, ItemsAS.BLOCK_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_CHISELED, ItemsAS.BLOCK_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_ENGRAVED, ItemsAS.BLOCK_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_PILLAR, ItemsAS.BLOCK_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_RUNED, ItemsAS.BLOCK_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_SLAB, ItemsAS.BLOCK_MARBLE_RAW, 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_MARBLE_STAIRS, ItemsAS.BLOCK_MARBLE_RAW);

        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_ARCH, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_BRICKS, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_CHISELED, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_ENGRAVED, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_PILLAR, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_RUNED, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_SLAB, ItemsAS.BLOCK_SOOTY_MARBLE_RAW, 2);
        stonecutterResultFromBase(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ItemsAS.BLOCK_SOOTY_MARBLE_STAIRS, ItemsAS.BLOCK_SOOTY_MARBLE_RAW);
    }

    public static void registerSmeltingRecipes(RecipeOutput recipeOutput) {
        oreSmelting(recipeOutput, List.of(ItemsAS.BLOCK_STARMETAL_ORE, ItemsAS.RAW_STARMETAL), RecipeCategory.MISC, ItemsAS.STARMETAL_INGOT, 0.7F, 200, "");
        oreBlasting(recipeOutput, List.of(ItemsAS.BLOCK_STARMETAL_ORE, ItemsAS.RAW_STARMETAL), RecipeCategory.MISC, ItemsAS.STARMETAL_INGOT, 0.7F, 100, "");
    }

    public static void registerSpecialRecipes(RecipeOutput recipeOutput) {
        SpecialRecipeBuilder.special(RecipeChangeColor.IlluminationWandChangeColor::new)
                .save(recipeOutput, RecipeTypesAS.ILLUMINATION_WAND_CHANGE_COLOR_SERIALIZER.getId());
        SpecialRecipeBuilder.special(RecipeChangeColor.CelestialGatewayChangeColor::new)
                .save(recipeOutput, RecipeTypesAS.CELESTIAL_GATEWAY_CHANGE_COLOR_SERIALIZER.getId());
    }
}
