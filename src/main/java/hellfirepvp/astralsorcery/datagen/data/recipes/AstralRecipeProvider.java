/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes;

import hellfirepvp.astralsorcery.datagen.data.GeneratedRecipeBuffer;
import hellfirepvp.astralsorcery.datagen.data.recipes.altar.AltarRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.focal.FocalCombineRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.focal.FocalTransmutationRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.infusion.InfusionRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.lightwell.LightwellRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.liquid.LiquidInteractionRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.liquid.LiquidStarlightRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.lumen.LumenCrystallizationRecipeProvider;
import hellfirepvp.astralsorcery.datagen.data.recipes.lumen.LumenGenerationRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralRecipeProvider extends RecipeProvider {

    public AstralRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider provider) {
        recipeOutput = new GeneratedRecipeBuffer.BufferedRecipeOutput(recipeOutput, provider);

        AltarRecipeProvider.registerRecipes(recipeOutput);
        FocalCombineRecipeProvider.registerRecipes(recipeOutput);
        FocalTransmutationRecipeProvider.registerRecipes(recipeOutput);
        LumenGenerationRecipeProvider.registerRecipes(recipeOutput);
        LumenCrystallizationRecipeProvider.registerRecipes(recipeOutput);
        LightwellRecipeProvider.registerRecipes(recipeOutput);
        InfusionRecipeProvider.registerRecipes(recipeOutput);
        LiquidStarlightRecipeProvider.registerRecipes(recipeOutput);
        LiquidInteractionRecipeProvider.registerRecipes(recipeOutput);

        VanillaRecipeProvider.registerCraftingRecipes(recipeOutput);
        VanillaRecipeProvider.registerStonecuttingRecipes(recipeOutput);
        VanillaRecipeProvider.registerSmeltingRecipes(recipeOutput);
        VanillaRecipeProvider.registerSpecialRecipes(recipeOutput);
    }
}
