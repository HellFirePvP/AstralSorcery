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
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.FocalTransmutationRecipeBuilder;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

import java.util.concurrent.CompletableFuture;

import static hellfirepvp.astralsorcery.common.util.TimeUtil.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalTransmutationRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalTransmutationRecipeProvider extends RecipeProvider {

    private FocalTransmutationRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput) {
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(20))
                .color(ColorsAS.STARMETAL)
                .outputs(BlocksAS.STARMETAL_ORE.get(), 1)
                .input(BlockPredicate.matchesTag(BlockTags.IRON_ORES))
                .inputDisplay(Blocks.IRON_ORE)
                .save(recipeOutput);

        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(30))
                .color(ColorsAS.LUMEN_AEVITAS)
                .outputs(BlocksAS.HYACINTH.get(), 1)
                .input(BlockPredicate.matchesTag(BlockTags.SMALL_FLOWERS))
                .inputDisplay(ItemTags.SMALL_FLOWERS)
                .requiresConstellation(ConstellationsAS.AEVITAS.get())
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(30))
                .color(ColorsAS.LUMEN_VICIO)
                .outputs(BlocksAS.IRIS.get(), 1)
                .input(BlockPredicate.matchesTag(BlockTags.SMALL_FLOWERS))
                .inputDisplay(ItemTags.SMALL_FLOWERS)
                .requiresConstellation(ConstellationsAS.VICIO.get())
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(30))
                .color(ColorsAS.LUMEN_ARMARA)
                .outputs(BlocksAS.ORCHID.get(), 1)
                .input(BlockPredicate.matchesTag(BlockTags.SMALL_FLOWERS))
                .inputDisplay(ItemTags.SMALL_FLOWERS)
                .requiresConstellation(ConstellationsAS.ARMARA.get())
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(30))
                .color(ColorsAS.LUMEN_EVORSIO)
                .outputs(BlocksAS.PROTEA.get(), 1)
                .input(BlockPredicate.matchesTag(BlockTags.SMALL_FLOWERS))
                .inputDisplay(ItemTags.SMALL_FLOWERS)
                .requiresConstellation(ConstellationsAS.EVORSIO.get())
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(30))
                .color(ColorsAS.LUMEN_DISCIDIA)
                .outputs(BlocksAS.THISTLE.get(), 1)
                .input(BlockPredicate.matchesTag(BlockTags.SMALL_FLOWERS))
                .inputDisplay(ItemTags.SMALL_FLOWERS)
                .requiresConstellation(ConstellationsAS.DISCIDIA.get())
                .save(recipeOutput);

        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(40))
                .color(ColorsAS.DYE_LIGHT_GRAY)
                .outputs(Blocks.CLAY, 1)
                .inputDisplay(Items.SAND)
                .input(BlockPredicate.matchesTag(BlockTags.SAND))
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(40))
                .color(ColorsAS.DYE_LIGHT_BLUE)
                .outputs(Blocks.SEA_LANTERN, 1)
                .inputDisplay(Items.GLOWSTONE)
                .input(BlockPredicate.matchesBlocks(Blocks.GLOWSTONE))
                .save(recipeOutput);

        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(20))
                .color(ColorsAS.DYE_YELLOW)
                .outputs(Blocks.END_STONE, 1)
                .inputDisplay(Items.SANDSTONE)
                .input(BlockPredicate.matchesBlocks(Blocks.SANDSTONE))
                .requiresFocusedStarlight()
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(seconds(40))
                .color(ColorsAS.DYE_LIME)
                .outputs(Blocks.EMERALD_ORE, 1)
                .inputDisplay(Items.DIAMOND_ORE)
                .input(BlockPredicate.matchesBlocks(Blocks.DIAMOND_ORE))
                .requiresFocusedStarlight()
                .save(recipeOutput);
        FocalTransmutationRecipeBuilder.builder()
                .duration(minutes(2))
                .color(ColorsAS.DYE_WHITE)
                .outputs(Blocks.CAKE, 1)
                .inputDisplay(Items.PUMPKIN)
                .input(BlockPredicate.matchesBlocks(Blocks.PUMPKIN))
                .requiresFocusedStarlight()
                .save(recipeOutput);
    }
}
