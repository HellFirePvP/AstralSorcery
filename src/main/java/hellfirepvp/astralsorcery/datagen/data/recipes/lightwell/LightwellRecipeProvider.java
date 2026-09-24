/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.lightwell;

import hellfirepvp.astralsorcery.common.lib.FluidsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.LightwellRecipeBuilder;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightwellRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightwellRecipeProvider extends RecipeProvider {

    private LightwellRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput) {
        LightwellRecipeBuilder.builder(Ingredient.of(ItemsAS.AQUAMARINE), FluidsAS.LIQUID_STARLIGHT.getSource().get(), "_aquamarine")
                .color(ColorWrapper.opaque(0x00A7FF))
                .productionMultiplier(0.35F)
                .shatterMultiplier(0.15F)
                .save(recipeOutput);
        LightwellRecipeBuilder.builder(Ingredient.of(ItemsAS.RESONATING_GEM), FluidsAS.LIQUID_STARLIGHT.getSource().get(), "_resonating_gem")
                .color(ColorWrapper.opaque(0x00A7FF))
                .productionMultiplier(0.5F)
                .shatterMultiplier(0.12F)
                .save(recipeOutput);

        LightwellRecipeBuilder.builder(Ingredient.of(TagsAS.Items.ROCK_CRYSTAL), FluidsAS.LIQUID_STARLIGHT.getSource().get(), "_rock_crystal")
                .color(ColorsAS.ROCK_CRYSTAL)
                .productionMultiplier(0.14F)
                .shatterMultiplier(0.15F)
                .save(recipeOutput);
        LightwellRecipeBuilder.builder(Ingredient.of(TagsAS.Items.CELESTIAL_CRYSTAL), FluidsAS.LIQUID_STARLIGHT.getSource().get(), "_celestial_crystal")
                .color(ColorsAS.CELESTIAL_CRYSTAL)
                .productionMultiplier(0.22F)
                .shatterMultiplier(0.15F)
                .save(recipeOutput);

        LightwellRecipeBuilder.builder(Ingredient.of(Items.MAGMA_BLOCK), Fluids.LAVA, "_magma_block")
                .color(ColorWrapper.opaque(0xFF350C))
                .productionMultiplier(0.8F)
                .shatterMultiplier(0.05F)
                .save(recipeOutput);
        LightwellRecipeBuilder.builder(Ingredient.of(Items.NETHERRACK), Fluids.LAVA, "_netherrack")
                .color(ColorWrapper.opaque(0xFF350C))
                .productionMultiplier(0.4F)
                .shatterMultiplier(0.04F)
                .save(recipeOutput);

        LightwellRecipeBuilder.builder(Ingredient.of(Items.ICE), Fluids.WATER, "_ice")
                .color(ColorWrapper.opaque(0x5369FF))
                .productionMultiplier(1F)
                .shatterMultiplier(0.04F)
                .save(recipeOutput);
        LightwellRecipeBuilder.builder(Ingredient.of(Items.PACKED_ICE), Fluids.WATER, "_packed_ice")
                .color(ColorWrapper.opaque(0x5369FF))
                .productionMultiplier(0.5F)
                .shatterMultiplier(0.01F)
                .save(recipeOutput);
        LightwellRecipeBuilder.builder(Ingredient.of(Items.BLUE_ICE), Fluids.WATER, "_blue_ice")
                .color(ColorWrapper.opaque(0x5369FF))
                .productionMultiplier(2.5F)
                .shatterMultiplier(0.1F)
                .save(recipeOutput);
    }
}
