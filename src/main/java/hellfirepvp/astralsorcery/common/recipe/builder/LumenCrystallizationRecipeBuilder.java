/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenCrystallizationRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenCrystallizationRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private final Ingredient input;
    private final Lumen lumen;
    private float catalystShatterMultiplier = 1F;
    private final String recipeIdSuffix;

    private int lumenConsumedPerOperation = 1600;

    private LumenCrystallizationRecipeBuilder(Ingredient input, Lumen lumen, String recipeIdSuffix) {
        this.input = input;
        this.lumen = lumen;
        this.recipeIdSuffix = recipeIdSuffix;
    }

    public static LumenCrystallizationRecipeBuilder builder(Ingredient input, Lumen lumen) {
        return builder(input, lumen, "");
    }

    public static LumenCrystallizationRecipeBuilder builder(Ingredient input, Lumen lumen, String recipeIdSuffix) {
        return new LumenCrystallizationRecipeBuilder(input, lumen, recipeIdSuffix);
    }

    public LumenCrystallizationRecipeBuilder lumenConsumedPerOperation(int lumenConsumedPerOperation) {
        if (lumenConsumedPerOperation >= 1900) {
            throw new IllegalArgumentException("Lumen consumed per crystallization must be less than 1900 as it may not otherwise reasonably generate a crystal due to storage being limited to 2000.");
        }
        this.lumenConsumedPerOperation = lumenConsumedPerOperation;
        return this;
    }

    public LumenCrystallizationRecipeBuilder catalystShatterMultiplier(float multiplier) {
        this.catalystShatterMultiplier = multiplier;
        return this;
    }

    @Override
    public LumenCrystallizationRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public LumenCrystallizationRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        ResourceLocation id = RegistriesAS.REGISTRY_LUMEN.getKey(this.lumen);
        this.save(recipeOutput, id.withSuffix(this.recipeIdSuffix));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        id = AstralSorcery.key(id.getPath());
        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        LumenCrystallizationRecipe recipe = new LumenCrystallizationRecipe(
                this.input,
                this.lumen,
                this.catalystShatterMultiplier,
                this.lumenConsumedPerOperation
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(id.withPrefix("lumen_crystallization/"), recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
