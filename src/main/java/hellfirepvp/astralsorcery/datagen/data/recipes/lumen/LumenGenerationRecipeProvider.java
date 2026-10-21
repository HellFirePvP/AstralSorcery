/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.lumen;

import hellfirepvp.astralsorcery.common.component.ArtifactTypeComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.builder.LumenGenerationRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenGenerationRecipeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenGenerationRecipeProvider extends RecipeProvider {

    private LumenGenerationRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    public static void registerRecipes(RecipeOutput recipeOutput) {
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.BLOCK_HYACINTH), LumenAS.AEVITAS)
                .producedLumenAmount(4)
                .productionAttemptMultiplier(4F)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.BLOCK_IRIS), LumenAS.VICIO)
                .producedLumenAmount(4)
                .productionAttemptMultiplier(4F)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.BLOCK_ORCHID), LumenAS.ARMARA)
                .producedLumenAmount(4)
                .productionAttemptMultiplier(4F)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.BLOCK_PROTEA), LumenAS.EVORSIO)
                .producedLumenAmount(4)
                .productionAttemptMultiplier(4F)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.BLOCK_THISTLE), LumenAS.DISCIDIA)
                .producedLumenAmount(4)
                .productionAttemptMultiplier(4F)
                .save(recipeOutput);

        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.VIVID_POWDER), LumenAS.VIREL)
                .producedLumenAmount(3)
                .productionAttemptMultiplier(4F)
                .catalystShatterMultiplier(1.2F)
                .attemptStarlightConsumption(1.4F)
                .addLumenCombinationInput(LumenAS.AEVITAS, 9)
                .addLumenCombinationInput(LumenAS.VICIO, 5)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.ILLUMINATION_POWDER), LumenAS.SOLYN)
                .producedLumenAmount(3)
                .productionAttemptMultiplier(4F)
                .catalystShatterMultiplier(1.2F)
                .attemptStarlightConsumption(1.4F)
                .addLumenCombinationInput(LumenAS.AEVITAS, 5)
                .addLumenCombinationInput(LumenAS.ARMARA, 8)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(Tags.Items.GEMS_QUARTZ), LumenAS.CALDOR)
                .producedLumenAmount(3)
                .productionAttemptMultiplier(4F)
                .catalystShatterMultiplier(1.2F)
                .attemptStarlightConsumption(1.4F)
                .addLumenCombinationInput(LumenAS.EVORSIO, 6)
                .addLumenCombinationInput(LumenAS.ARMARA, 4)
                .addLumenCombinationInput(LumenAS.DISCIDIA, 10)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.NOCTURNAL_POWDER), LumenAS.NULLAE)
                .producedLumenAmount(3)
                .productionAttemptMultiplier(4F)
                .catalystShatterMultiplier(1.2F)
                .attemptStarlightConsumption(1.4F)
                .addLumenCombinationInput(LumenAS.EVORSIO, 5)
                .addLumenCombinationInput(LumenAS.VICIO, 7)
                .save(recipeOutput);

        LumenGenerationRecipeBuilder.builder(Ingredient.of(Items.ECHO_SHARD), LumenAS.HYLE)
                .producedLumenAmount(2)
                .productionAttemptMultiplier(3F)
                .catalystShatterMultiplier(1.5F)
                .attemptStarlightConsumption(2F)
                .addLumenCombinationInput(LumenAS.SOLYN, 5)
                .addLumenCombinationInput(LumenAS.VIREL, 4)
                .addLumenCombinationInput(LumenAS.ARMARA, 8)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.STARDUST), LumenAS.AKASHA)
                .producedLumenAmount(2)
                .productionAttemptMultiplier(3F)
                .catalystShatterMultiplier(1.5F)
                .attemptStarlightConsumption(2F)
                .addLumenCombinationInput(LumenAS.NULLAE, 4)
                .addLumenCombinationInput(LumenAS.CALDOR, 7)
                .addLumenCombinationInput(LumenAS.EVORSIO, 12)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(Tags.Items.GEMS_AMETHYST), LumenAS.DYNAMIS)
                .producedLumenAmount(2)
                .productionAttemptMultiplier(3F)
                .catalystShatterMultiplier(1.5F)
                .attemptStarlightConsumption(2F)
                .addLumenCombinationInput(LumenAS.SOLYN, 7)
                .addLumenCombinationInput(LumenAS.VIREL, 8)
                .addLumenCombinationInput(LumenAS.VICIO, 8)
                .save(recipeOutput);
        LumenGenerationRecipeBuilder.builder(Ingredient.of(ItemsAS.RESONATING_GEM), LumenAS.AION)
                .producedLumenAmount(2)
                .productionAttemptMultiplier(3F)
                .catalystShatterMultiplier(1.5F)
                .attemptStarlightConsumption(2F)
                .addLumenCombinationInput(LumenAS.NULLAE, 8)
                .addLumenCombinationInput(LumenAS.CALDOR, 5)
                .addLumenCombinationInput(LumenAS.DISCIDIA, 16)
                .save(recipeOutput);

        CompoundIngredient matchAnyArtifactShard = new CompoundIngredient(RegistriesAS.REGISTRY_ARTIFACT_TYPES.stream()
                .map(ArtifactTypeComponent::new)
                .map(typeCmp -> DataComponentIngredient.of(false, DataComponentsAS.ARTIFACT_TYPE, typeCmp, ItemsAS.ARTIFACT_SHARD))
                .toList());
        LumenGenerationRecipeBuilder.builder(matchAnyArtifactShard.toVanilla(), LumenAS.PRISMATIC)
                .producedLumenAmount(2)
                .productionAttemptMultiplier(2.5F)
                .catalystShatterMultiplier(2F)
                .attemptStarlightConsumption(3F)
                .addLumenCombinationInput(LumenAS.HYLE, 3)
                .addLumenCombinationInput(LumenAS.DYNAMIS, 3)
                .addLumenCombinationInput(LumenAS.AION, 3)
                .addLumenCombinationInput(LumenAS.AKASHA, 3)
                .save(recipeOutput);
    }
}
