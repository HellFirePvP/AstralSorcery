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
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenGenerationRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenGenerationRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private final Ingredient input;
    private final Lumen producedLumen;
    private final String recipeIdSuffix;

    private int producedLumenAmount = 1;
    private float productionAttemptMultiplier = 1F;
    private float attemptStarlightConsumption = 1F;
    private float catalystShatterMultiplier = 1F;
    private final Map<Lumen, Integer> lumenCombinationInputs = new LinkedHashMap<>();

    private LumenGenerationRecipeBuilder(Ingredient input, Lumen producedLumen, String recipeIdSuffix) {
        this.input = input;
        this.producedLumen = producedLumen;
        this.recipeIdSuffix = recipeIdSuffix;
    }

    public static LumenGenerationRecipeBuilder builder(Ingredient input, Lumen producedLumen) {
        return builder(input, producedLumen, "");
    }

    public static LumenGenerationRecipeBuilder builder(Ingredient input, Lumen producedLumen, String recipeIdSuffix) {
        return new LumenGenerationRecipeBuilder(input, producedLumen, recipeIdSuffix);
    }

    public LumenGenerationRecipeBuilder producedLumenAmount(int amount) {
        this.producedLumenAmount = amount;
        return this;
    }

    public LumenGenerationRecipeBuilder productionAttemptMultiplier(float multiplier) {
        this.productionAttemptMultiplier = multiplier;
        return this;
    }

    public LumenGenerationRecipeBuilder attemptStarlightConsumption(float consumption) {
        this.attemptStarlightConsumption = consumption;
        return this;
    }

    public LumenGenerationRecipeBuilder catalystShatterMultiplier(float multiplier) {
        this.catalystShatterMultiplier = multiplier;
        return this;
    }

    public LumenGenerationRecipeBuilder addLumenCombinationInput(Lumen lumen, int amount) {
        this.lumenCombinationInputs.put(lumen, amount);
        return this;
    }

    @Override
    public LumenGenerationRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public LumenGenerationRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return Items.AIR;
    }

    @Override
    public void save(RecipeOutput recipeOutput) {
        ResourceLocation id = RegistriesAS.REGISTRY_LUMEN.getKey(this.producedLumen);
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

        LumenGenerationRecipe recipe = new LumenGenerationRecipe(
                this.input,
                this.producedLumen,
                this.producedLumenAmount,
                this.productionAttemptMultiplier,
                this.attemptStarlightConsumption,
                this.catalystShatterMultiplier,
                this.lumenCombinationInputs
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(id.withPrefix("lumen/"), recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
