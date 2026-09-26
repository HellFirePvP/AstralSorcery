/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.lumen;

import hellfirepvp.astralsorcery.common.component.ArtifactTypeComponent;
import hellfirepvp.astralsorcery.common.item.LumenCrystalItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.LumenCrystallizationRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystallizationRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystallizationRecipeProvider extends RecipeProvider {

    private LumenCrystallizationRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput) {
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemTags.SAPLINGS), LumenAS.AEVITAS.get())
                .catalystShatterMultiplier(5F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(LumenCrystalItem.getCrystal(LumenAS.AEVITAS)), LumenAS.AEVITAS.get(), "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.INGOTS), LumenAS.ARMARA.get())
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(LumenCrystalItem.getCrystal(LumenAS.ARMARA)), LumenAS.ARMARA.get(), "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemTags.ARROWS), LumenAS.DISCIDIA.get())
                .catalystShatterMultiplier(3F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(LumenCrystalItem.getCrystal(LumenAS.DISCIDIA)), LumenAS.DISCIDIA.get(), "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.STONES), LumenAS.EVORSIO.get())
                .catalystShatterMultiplier(5F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(LumenCrystalItem.getCrystal(LumenAS.EVORSIO)), LumenAS.EVORSIO.get(), "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.FEATHERS), LumenAS.VICIO.get())
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(LumenCrystalItem.getCrystal(LumenAS.VICIO)), LumenAS.VICIO.get(), "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(TagsAS.Items.CRYSTAL), LumenAS.PRISMATIC.get())
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
    }
}
