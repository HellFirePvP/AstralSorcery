/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.liquid;

import hellfirepvp.astralsorcery.common.ingredient.HasStoredLumenIngredient;
import hellfirepvp.astralsorcery.common.ingredient.IsLumenBindableIngredient;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.recipe.builder.LiquidStarlightRecipeBuilder;
import hellfirepvp.astralsorcery.common.recipe.liquid.output.*;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidStarlightRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LiquidStarlightRecipeProvider extends RecipeProvider {

    private LiquidStarlightRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput) {
        LiquidStarlightRecipeBuilder.builder("infused_wood", new CountIngredient(Ingredient.of(ItemTags.LOGS), 1))
                .addOutputModifier(LiquidStarlightOutputDropItem.create(ItemsAS.BLOCK_INFUSED_WOOD_RAW.toStack()))
                .duration(30)
                .randomAdditionalDuration(0)
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("merge_crystals",
                        new CountIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1),
                        List.of(new CountIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1)))
                .addOutputModifier(LiquidStarlightOutputMergeCrystal.getInstance())
                .duration(60)
                .randomAdditionalDuration(20)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("form_crystal_cluster",
                        new CountIngredient(Ingredient.of(ItemsAS.STARDUST), 1),
                        List.of(new CountIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1)))
                .addOutputModifier(LiquidStarlightOutputFormCrystalCluster.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("form_gem_crystal_cluster",
                        new CountIngredient(Ingredient.of(ItemsAS.ILLUMINATION_POWDER), 1),
                        List.of(new CountIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1)))
                .addOutputModifier(LiquidStarlightOutputFormGemCrystalCluster.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("grow_crystal_size",
                        new CountIngredient(Ingredient.of(TagsAS.Items.CRYSTAL), 1))
                .addOutputModifier(LiquidStarlightOutputGrowSize.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);

        LiquidStarlightRecipeBuilder.builder("bind_lumen",
                        new CountIngredient(Ingredient.of(ItemsAS.LUMEN_CRYSTAL), 1),
                        List.of(
                                new CountIngredient(Ingredient.of(ItemsAS.STARDUST), 1),
                                new CountIngredient(IsLumenBindableIngredient.INSTANCE.toVanilla(), 1)
                        ))
                .addOutputModifier(LiquidStarlightOutputBindLumen.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);
        LiquidStarlightRecipeBuilder.builder("fill_lumen",
                        new CountIngredient(Ingredient.of(ItemsAS.LUMEN_CRYSTAL), 1),
                        List.of(new CountIngredient(HasStoredLumenIngredient.INSTANCE.toVanilla(), 1)))
                .addOutputModifier(LiquidStarlightOutputFillLumen.getInstance())
                .duration(80)
                .randomAdditionalDuration(40)
                .consumesLiquid()
                .doesntConsumeInputs()
                .save(recipeOutput);

    }
}
