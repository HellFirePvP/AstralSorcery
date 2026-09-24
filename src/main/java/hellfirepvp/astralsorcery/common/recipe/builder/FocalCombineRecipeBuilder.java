/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalCombineRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalCombineRecipeBuilder implements RecipeBuilder {

    private String group = "";
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private int duration = 100;
    private ColorWrapper color = ColorWrapper.WHITE;
    private final List<Ingredient> inputs = new ArrayList<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private BaseConstellation requiredConstellation = null;

    private FocalCombineRecipeBuilder() {}

    public static FocalCombineRecipeBuilder builder() {
        return new FocalCombineRecipeBuilder();
    }

    public FocalCombineRecipeBuilder duration(int craftingTickTime) {
        this.duration = craftingTickTime;
        return this;
    }

    public FocalCombineRecipeBuilder color(ColorWrapper color) {
        this.color = color;
        return this;
    }

    public FocalCombineRecipeBuilder input(Ingredient input) {
        this.inputs.add(input);
        return this;
    }

    public FocalCombineRecipeBuilder outputs(ItemStack output) {
        this.outputs.add(output);
        return this;
    }

    public FocalCombineRecipeBuilder outputs(ItemLike output) {
        return this.outputs(new ItemStack(output));
    }

    public FocalCombineRecipeBuilder requiresConstellation(BaseConstellation constellation) {
        this.requiredConstellation = constellation;
        return this;
    }

    @Override
    public FocalCombineRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public FocalCombineRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.outputs.stream().findFirst().map(ItemStack::getItem).orElse(Items.AIR);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        id = AstralSorcery.key(id.getPath());
        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        FocalCombineRecipe recipe = new FocalCombineRecipe(
                this.duration,
                this.color,
                this.inputs,
                this.outputs,
                Optional.ofNullable(this.requiredConstellation)
        );
        recipe.setGroup(this.group);
        recipeOutput.accept(id.withPrefix("focal_combine/"), recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
