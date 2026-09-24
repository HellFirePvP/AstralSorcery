/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.focal;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.FocalCombineRecipeBuilder;
import hellfirepvp.astralsorcery.common.recipe.builder.FocalTransmutationRecipeBuilder;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

import static hellfirepvp.astralsorcery.common.util.TimeUtil.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalCombineRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalCombineRecipeProvider extends RecipeProvider {

    private FocalCombineRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput) {
        FocalCombineRecipeBuilder.builder()
                .duration(seconds(20))
                .color(ColorsAS.ROCK_CRYSTAL)
                .input(Ingredient.of(BlocksAS.MARBLE_RAW))
                .input(Ingredient.of(BlocksAS.MARBLE_RAW))
                .input(Ingredient.of(BlocksAS.MARBLE_RAW))
                .input(Ingredient.of(BlocksAS.MARBLE_RAW))
                .input(Ingredient.of(Blocks.CRAFTING_TABLE))
                .outputs(ItemsAS.BLOCK_ALTAR_ILLUMINATION)
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(seconds(20))
                .color(ColorWrapper.opaque(0x22BBFF))
                .input(Ingredient.of(BlocksAS.MARBLE_RAW))
                .input(Ingredient.of(BlocksAS.MARBLE_RAW))
                .input(Ingredient.of(ItemsAS.AQUAMARINE))
                .input(Ingredient.of(ItemsAS.AQUAMARINE))
                .input(Ingredient.of(Tags.Items.ENDER_PEARLS))
                .outputs(ItemsAS.WAND)
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(seconds(100))
                .color(ColorsAS.STARMETAL)
                .input(Ingredient.of(Items.RAW_IRON))
                .outputs(ItemsAS.RAW_STARMETAL)
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(seconds(25))
                .color(ColorsAS.STARMETAL)
                .input(Ingredient.of(ItemTags.IRON_ORES))
                .outputs(ItemsAS.BLOCK_STARMETAL_ORE)
                .save(recipeOutput);

        FocalCombineRecipeBuilder.builder()
                .duration(minutes(1))
                .color(ColorsAS.LUMEN_AEVITAS)
                .outputs(ItemsAS.BLOCK_HYACINTH)
                .input(Ingredient.of(ItemTags.SMALL_FLOWERS))
                .requiresConstellation(ConstellationsAS.AEVITAS.get())
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(minutes(1))
                .color(ColorsAS.LUMEN_VICIO)
                .outputs(ItemsAS.BLOCK_IRIS)
                .input(Ingredient.of(ItemTags.SMALL_FLOWERS))
                .requiresConstellation(ConstellationsAS.VICIO.get())
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(minutes(1))
                .color(ColorsAS.LUMEN_ARMARA)
                .outputs(ItemsAS.BLOCK_ORCHID)
                .input(Ingredient.of(ItemTags.SMALL_FLOWERS))
                .requiresConstellation(ConstellationsAS.ARMARA.get())
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(minutes(1))
                .color(ColorsAS.LUMEN_EVORSIO)
                .outputs(ItemsAS.BLOCK_PROTEA)
                .input(Ingredient.of(ItemTags.SMALL_FLOWERS))
                .requiresConstellation(ConstellationsAS.EVORSIO.get())
                .save(recipeOutput);
        FocalCombineRecipeBuilder.builder()
                .duration(minutes(1))
                .color(ColorsAS.LUMEN_DISCIDIA)
                .outputs(ItemsAS.BLOCK_THISTLE)
                .input(Ingredient.of(ItemTags.SMALL_FLOWERS))
                .requiresConstellation(ConstellationsAS.DISCIDIA.get())
                .save(recipeOutput);
    }
}
