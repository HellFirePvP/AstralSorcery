/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.lumen;

import hellfirepvp.astralsorcery.common.component.LumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.builder.LumenCrystallizationRecipeBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
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
        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemTags.SAPLINGS), LumenAS.AEVITAS)
                .catalystShatterMultiplier(5F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.AEVITAS), LumenAS.AEVITAS, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.INGOTS), LumenAS.ARMARA)
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.ARMARA), LumenAS.ARMARA, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemTags.ARROWS), LumenAS.DISCIDIA)
                .catalystShatterMultiplier(3F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.DISCIDIA), LumenAS.DISCIDIA, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.STONES), LumenAS.EVORSIO)
                .catalystShatterMultiplier(5F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.EVORSIO), LumenAS.EVORSIO, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.FEATHERS), LumenAS.VICIO)
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.VICIO), LumenAS.VICIO, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        // ---------------------------------------------------------------------------

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemsAS.VIVID_POWDER), LumenAS.VIREL)
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.VIREL), LumenAS.VIREL, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemsAS.ILLUMINATION_POWDER), LumenAS.SOLYN)
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.SOLYN), LumenAS.SOLYN, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemsAS.NOCTURNAL_POWDER), LumenAS.NULLAE)
                .catalystShatterMultiplier(2F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.NULLAE), LumenAS.NULLAE, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.GEMS_QUARTZ), LumenAS.CALDOR)
                .catalystShatterMultiplier(3F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.CALDOR), LumenAS.CALDOR, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        // ---------------------------------------------------------------------------

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Items.ECHO_SHARD), LumenAS.HYLE)
                .catalystShatterMultiplier(3.5F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.HYLE), LumenAS.HYLE, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(Tags.Items.GEMS_AMETHYST), LumenAS.DYNAMIS)
                .catalystShatterMultiplier(4F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.DYNAMIS), LumenAS.DYNAMIS, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemsAS.RESONATING_GEM), LumenAS.AION)
                .catalystShatterMultiplier(3F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.AION), LumenAS.AION, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(ItemsAS.STARDUST), LumenAS.AKASHA)
                .catalystShatterMultiplier(3F)
                .save(recipeOutput);
        LumenCrystallizationRecipeBuilder.builder(lumenCrystal(LumenAS.AKASHA), LumenAS.AKASHA, "_crystal")
                .catalystShatterMultiplier(0F)
                .save(recipeOutput);

        // ---------------------------------------------------------------------------

        LumenCrystallizationRecipeBuilder.builder(Ingredient.of(TagsAS.Items.CRYSTAL), LumenAS.PRISMATIC)
                .catalystShatterMultiplier(3F)
                .save(recipeOutput);
    }
    
    private static Ingredient lumenCrystal(Holder<Lumen> type) {
        return DataComponentIngredient.of(false, DataComponentPredicate.builder()
                .expect(DataComponentsAS.LUMEN.get(), new LumenComponent(type))
                .build(), ItemsAS.LUMEN_CRYSTAL.asItem());
    }
}
