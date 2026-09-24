/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightwellRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightwellRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private final Ingredient input;
    private final Fluid generatedFluid;
    private final String recipeIdSuffix;

    private ColorWrapper catalystColor = ColorWrapper.WHITE;
    private float productionMultiplier = 0.5F;
    private float shatterMultiplier = 10F;

    private LightwellRecipeBuilder(Ingredient input, Fluid generatedFluid, String recipeIdSuffix) {
        this.input = input;
        this.generatedFluid = generatedFluid;
        this.recipeIdSuffix = recipeIdSuffix;
    }

    public static LightwellRecipeBuilder builder(Ingredient input, Fluid generatedFluid) {
        return new LightwellRecipeBuilder(input, generatedFluid, "");
    }

    public static LightwellRecipeBuilder builder(Ingredient input, Fluid generatedFluid, String recipeIdSuffix) {
        return new LightwellRecipeBuilder(input, generatedFluid, recipeIdSuffix);
    }

    public LightwellRecipeBuilder color(ColorWrapper catalystColor) {
        this.catalystColor = catalystColor;
        return this;
    }

    public LightwellRecipeBuilder productionMultiplier(float productionMultiplier) {
        this.productionMultiplier = productionMultiplier;
        return this;
    }

    public LightwellRecipeBuilder shatterMultiplier(float shatterMultiplier) {
        this.shatterMultiplier = shatterMultiplier;
        return this;
    }

    @Override
    public LightwellRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public LightwellRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        ResourceLocation id = BuiltInRegistries.FLUID.getKey(this.generatedFluid);
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

        LightwellRecipe recipe = new LightwellRecipe(
                this.catalystColor,
                this.input,
                this.generatedFluid,
                this.productionMultiplier,
                this.shatterMultiplier
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(id.withPrefix("lightwell/"), recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
