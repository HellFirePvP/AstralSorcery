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
import hellfirepvp.astralsorcery.common.ingredient.IngredientBridge;
import hellfirepvp.astralsorcery.common.lib.types.AltarEffectsAS;
import hellfirepvp.astralsorcery.common.lumen.LumenStack;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.recipe.altar.effect.AltarEffect;
import hellfirepvp.astralsorcery.common.recipe.altar.output.AltarRecipeOutputModifier;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.data.CountIngredient;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarRecipeBuilder implements RecipeBuilder {

    private ResourceLocation recipeId = null;
    private String group = "";
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    private final TileAltar.AltarType requiredType;
    private final NonNullList<String> gridLines = NonNullList.withSize(3, "   ");
    private final NonNullList<String> relayLines = NonNullList.withSize(5, "     ");
    private final Map<Character, IngredientBridge> keys = new LinkedHashMap<>();
    private final List<ItemStack> outputs = new ArrayList<>();
    private BaseConstellation focusConstellation = null;
    private float baseFocusShatterChance = 0F;
    private int duration = 100;
    private boolean isOnlyNight = true;
    private boolean mayChain = false;
    private final Set<BaseConstellation> requiredStarlight = new LinkedHashSet<>();
    private final List<LumenStack> requiredLumen = new ArrayList<>();
    private final List<FluidStack> requiredFluids = new ArrayList<>();
    private final List<CountIngredient> requiredAdditionalInputs = new ArrayList<>();

    private final Set<AltarEffect> effects = new LinkedHashSet<>();
    private final List<AltarRecipeOutputModifier> outputModifiers = new ArrayList<>();

    private AltarRecipeBuilder(TileAltar.AltarType requiredType) {
        this.requiredType = requiredType;
        this.addDefaultEffects();
    }

    private void addDefaultEffects() {
        switch (this.requiredType) {
            case RADIANCE:
            case LUMINANCE:
            case RESONANCE:
                this.addEffect(AltarEffectsAS.DEFAULT_ALTAR_SPARKLE);
            case ILLUMINATION:
                this.addEffect(AltarEffectsAS.DEFAULT_CENTRAL_BEAM);
                this.addEffect(AltarEffectsAS.DEFAULT_LUMEN_INPUT);
                this.addEffect(AltarEffectsAS.DEFAULT_RELAY_INPUT);
        }
    }

    public static AltarRecipeBuilder builder(TileAltar.AltarType requiredType) {
        return new AltarRecipeBuilder(requiredType);
    }

    public AltarRecipeBuilder setRecipeId(ResourceLocation recipeId) {
        this.recipeId = recipeId;
        return this;
    }

    public AltarRecipeBuilder setRecipeId(DeferredHolder<?, ?> identifiable) {
        return this.setRecipeId(identifiable.getKey().location());
    }

    public AltarRecipeBuilder setRecipeId(DeferredHolder<?, ?> identifiable, String suffix) {
        return this.setRecipeId(NameUtil.suffixPath(identifiable.getKey().location(), suffix));
    }

    public AltarRecipeBuilder setGridLines(String line0, String line1, String line2) {
        this.gridLines.set(0, validateLine(line0, 3));
        this.gridLines.set(1, validateLine(line1, 3));
        this.gridLines.set(2, validateLine(line2, 3));
        return this;
    }

    public AltarRecipeBuilder setRelayLines(String line0, String line1, String line2, String line3, String line4) {
        this.relayLines.set(0, validateLine(line0, 5));
        this.relayLines.set(1, validateLine(line1, 5));
        this.relayLines.set(2, validateLine(line2, 5));
        this.relayLines.set(3, validateLine(line3, 5));
        this.relayLines.set(4, validateLine(line4, 5));
        return this;
    }

    private String validateLine(String line, int length) {
        if (line.length() != length) {
            throw new IllegalArgumentException("Altar pattern line must be exactly " + length + " characters long");
        }
        return line;
    }

    public AltarRecipeBuilder addInput(Character key, Ingredient input) {
        this.keys.put(key, IngredientBridge.of(input));
        return this;
    }

    public AltarRecipeBuilder addInput(Character key, ICustomIngredient input) {
        return this.addInput(key, input.toVanilla());
    }

    public AltarRecipeBuilder addInput(Character key, ItemStack stack) {
        return this.addInput(key, Ingredient.of(stack));
    }

    public AltarRecipeBuilder addInput(Character key, TagKey<Item> itemTag) {
        return this.addInput(key, Ingredient.of(itemTag));
    }

    public AltarRecipeBuilder addInput(Character key, Item item) {
        return this.addInput(key, new ItemStack(item));
    }

    public AltarRecipeBuilder addInput(Character key, ItemLike item) {
        return this.addInput(key, new ItemStack(item));
    }

    public AltarRecipeBuilder addInput(Character key, FluidStack input) {
        return this.addInput(key, SizedFluidIngredient.of(input));
    }

    public AltarRecipeBuilder addInput(Character key, SizedFluidIngredient input) {
        this.keys.put(key, IngredientBridge.of(input));
        return this;
    }

    public AltarRecipeBuilder addOutput(ItemStack stack) {
        this.outputs.add(stack.copy());
        return this;
    }

    public AltarRecipeBuilder addOutput(Item item) {
        return this.addOutput(new ItemStack(item));
    }

    public AltarRecipeBuilder addOutput(ItemLike item) {
        return this.addOutput(new ItemStack(item));
    }

    public AltarRecipeBuilder addFocusConstellation(BaseConstellation constellation) {
        this.focusConstellation = constellation;
        return this;
    }

    public AltarRecipeBuilder setBaseFocusShatterChance(float chance) {
        this.baseFocusShatterChance = chance;
        return this;
    }

    public AltarRecipeBuilder setDuration(int tickTime) {
        this.duration = tickTime;
        return this;
    }

    public AltarRecipeBuilder setOnlyNight(boolean onlyNight) {
        this.isOnlyNight = onlyNight;
        return this;
    }

    public AltarRecipeBuilder mayChain() {
        this.mayChain = true;
        return this;
    }

    public AltarRecipeBuilder addRequiredStarlight(BaseConstellation constellation) {
        this.requiredStarlight.add(constellation);
        return this;
    }

    public AltarRecipeBuilder addRequiredLumen(LumenStack lumen) {
        this.requiredLumen.add(lumen);
        return this;
    }

    public AltarRecipeBuilder addRequiredFluid(FluidStack fluid) {
        this.requiredFluids.add(fluid);
        return this;
    }

    public AltarRecipeBuilder addRequiredAdditionalInput(int count, ItemLike... items) {
        return this.addRequiredAdditionalInput(count, Ingredient.of(items));
    }

    public AltarRecipeBuilder addRequiredAdditionalInput(int count, ItemStack... stacks) {
        this.requiredAdditionalInputs.add(new CountIngredient(Ingredient.of(stacks), count));
        return this;
    }

    public AltarRecipeBuilder addRequiredAdditionalInput(int count, TagKey<Item> itemTag) {
        this.requiredAdditionalInputs.add(new CountIngredient(Ingredient.of(itemTag), count));
        return this;
    }

    public AltarRecipeBuilder addRequiredAdditionalInput(int count, Ingredient ingredient) {
        this.requiredAdditionalInputs.add(new CountIngredient(ingredient, count));
        return this;
    }

    public AltarRecipeBuilder addRequiredAdditionalInput(int count, ICustomIngredient ingredient) {
        return this.addRequiredAdditionalInput(count, ingredient.toVanilla());
    }

    public AltarRecipeBuilder addEffect(AltarEffect effect) {
        this.effects.add(effect);
        return this;
    }

    public AltarRecipeBuilder addEffect(Supplier<? extends AltarEffect> effectSupplier) {
        return this.addEffect(effectSupplier.get());
    }

    public AltarRecipeBuilder addOutputModifier(AltarRecipeOutputModifier modifier) {
        this.outputModifiers.add(modifier);
        return this;
    }

    @Override
    public AltarRecipeBuilder unlockedBy(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    @Override
    public AltarRecipeBuilder group(@Nullable String groupName) {
        this.group = groupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.outputs.stream().findFirst().map(ItemStack::getItem).orElse(Items.AIR);
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceLocation id) {
        id = this.recipeId != null ? this.recipeId : AstralSorcery.key(id.getPath());
        Advancement.Builder advancementBuilder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        this.criteria.forEach(advancementBuilder::addCriterion);

        if (this.mayChain &&
                (!this.requiredAdditionalInputs.isEmpty() ||
                        !this.requiredFluids.isEmpty() ||
                        !this.requiredLumen.isEmpty())) {
            throw new IllegalStateException("Altar recipes that may chain cannot have additional inputs, fluids or lumen requirements.");
        }

        AltarRecipeGrid grid = AltarRecipeGrid.create(this.keys, this.gridLines, this.relayLines);
        AltarRecipe recipe = new AltarRecipe(
                this.requiredType,
                grid,
                this.outputs,
                Optional.ofNullable(this.focusConstellation),
                this.baseFocusShatterChance,
                this.duration,
                this.isOnlyNight,
                this.mayChain,
                this.requiredStarlight,
                this.requiredLumen,
                this.requiredFluids,
                this.requiredAdditionalInputs,
                this.effects,
                this.outputModifiers);
        recipe.setGroup(this.group);
        recipeOutput.accept(id.withPrefix("altar/"), recipe, advancementBuilder.build(id.withPrefix("recipes/")));
    }
}
