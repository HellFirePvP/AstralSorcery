/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.recipe.infusion.InfusionRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InfusionRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InfusionRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private final Ingredient itemInput;
    private final Fluid fluidInput;
    private final ItemStack output;

    private int duration = 200;

    private float consumptionChance = 0.05F;
    private boolean consumeMultipleFluids = false;
    private boolean acceptChaliceInput = true;

    private InfusionRecipeBuilder(Ingredient itemInput, Fluid fluidInput, ItemStack output) {
        this.itemInput = itemInput;
        this.fluidInput = fluidInput;
        this.output = output;
    }

    public static InfusionRecipeBuilder builder(Ingredient itemInput, Fluid fluidInput, ItemStack output) {
        return new InfusionRecipeBuilder(itemInput, fluidInput, output);
    }

    public static InfusionRecipeBuilder builder(Ingredient itemInput, Supplier<? extends Fluid> fluidInput, ItemStack output) {
        return new InfusionRecipeBuilder(itemInput, fluidInput.get(), output);
    }

    public InfusionRecipeBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public InfusionRecipeBuilder setConsumptionChance(float consumptionChance) {
        this.consumptionChance = consumptionChance;
        return this;
    }

    public InfusionRecipeBuilder setConsumeMultipleFluids(boolean consumeMultipleFluids) {
        this.consumeMultipleFluids = consumeMultipleFluids;
        return this;
    }

    public InfusionRecipeBuilder setAcceptChaliceInput(boolean acceptChaliceInput) {
        this.acceptChaliceInput = acceptChaliceInput;
        return this;
    }

    @Override
    public RecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.output.getItem();
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        id = AstralSorcery.key(id.getPath());
        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        InfusionRecipe recipe = new InfusionRecipe(
                this.itemInput,
                this.fluidInput,
                this.duration,
                this.output,
                this.consumptionChance,
                this.consumeMultipleFluids,
                this.acceptChaliceInput
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(id.withPrefix("infusion/"), recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
