/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RecipeTypesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.focal.drop.FocalCombineRecipe;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationCraftingInput;
import hellfirepvp.astralsorcery.common.recipe.focal.place.FocalTransmutationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipe;
import hellfirepvp.astralsorcery.common.recipe.lightwell.LightwellRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipe;
import hellfirepvp.astralsorcery.common.recipe.liquid.LiquidStarlightRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenCrystallizationRecipeInput;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipe;
import hellfirepvp.astralsorcery.common.recipe.lumen.LumenGenerationRecipeInput;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RecipeFinder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RecipeFinder {

    private final Level level;
    private final RecipeManager mgr;

    private RecipeFinder(Level level, RecipeManager mgr) {
        this.level = level;
        this.mgr = mgr;
    }

    @OnlyIn(Dist.CLIENT)
    public static RecipeFinder clientFinder() {
        Level level = Minecraft.getInstance().level;
        if (level == null) throw new IllegalArgumentException("Cannot resolve recipes outside of a level.");
        return of(level);
    }

    public static RecipeFinder of(Level level) {
        return of(level, level.getRecipeManager());
    }

    public static RecipeFinder of(Level level, RecipeManager mgr) {
        return new RecipeFinder(level, mgr);
    }

    public Optional<? extends RecipeHolder<? extends AbstractCookingRecipe>> findSmeltingRecipe(ItemStack inputStack) {
        SingleRecipeInput input = new SingleRecipeInput(inputStack);
        return ObjectUtils.firstNonNull(
                this.mgr.getRecipeFor(RecipeType.SMELTING, input, this.level),
                this.mgr.getRecipeFor(RecipeType.BLASTING, input, this.level),
                this.mgr.getRecipeFor(RecipeType.SMOKING, input, this.level),
                this.mgr.getRecipeFor(RecipeType.CAMPFIRE_COOKING, input, this.level),
                Optional.empty());
    }

    public Optional<RecipeHolder<FocalTransmutationRecipe>> findFocalTransmutationRecipe(BaseConstellation cst, BlockPos transmutationPos, boolean isFocused) {
        FocalTransmutationCraftingInput input = new FocalTransmutationCraftingInput(cst, this.level, transmutationPos, isFocused);
        return this.mgr.getRecipeFor(RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE.get(), input, this.level);
    }

    public Optional<RecipeHolder<FocalCombineRecipe>> findFocalCombineRecipe(BaseConstellation cst, List<ItemEntity> combinableItems) {
        FocalCombineCraftingInput input = new FocalCombineCraftingInput(cst, this.level, combinableItems);
        return this.mgr.getRecipeFor(RecipeTypesAS.FOCAL_COMBINE_TYPE.get(), input, this.level);
    }

    public Optional<RecipeHolder<AltarRecipe>> findAltarRecipe(AltarCraftingInput input) {
        return this.mgr.getRecipeFor(RecipeTypesAS.ALTAR_CRAFTING_TYPE.get(), input, this.level);
    }

    public Optional<RecipeHolder<LumenGenerationRecipe>> findLumenGenerationRecipe(ItemStack inputStack, Lumen generatedLumen, boolean useCombinationRecipes) {
        LumenGenerationRecipeInput input = LumenGenerationRecipeInput.create(inputStack);
        if (generatedLumen.equals(LumenAS.NONE.get())) {
            return this.mgr.getRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get(), input, this.level).stream()
                    .filter(recipeHolder -> useCombinationRecipes != recipeHolder.value().getLumenCombinationInputs().isEmpty())
                    .findFirst();
        }
        return this.mgr.getRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get(), input, this.level).stream()
                .filter(recipeHolder -> useCombinationRecipes != recipeHolder.value().getLumenCombinationInputs().isEmpty())
                .filter(recipeHolder -> recipeHolder.value().getProducedLumen().equals(generatedLumen))
                .findFirst();
    }

    public Optional<RecipeHolder<LumenGenerationRecipe>> findLumenGenerationRecipeByOutput(Lumen generatedLumen) {
        return this.mgr.getAllRecipesFor(RecipeTypesAS.LUMEN_GENERATION_TYPE.get()).stream()
                .filter(recipeHolder -> recipeHolder.value().getProducedLumen().equals(generatedLumen))
                .findFirst();
    }

    public Optional<RecipeHolder<LightwellRecipe>> findLightwellRecipe(ItemStack inputStack, FluidStack existingFluid) {
        LightwellRecipeInput input = LightwellRecipeInput.of(inputStack, existingFluid);
        return this.mgr.getRecipeFor(RecipeTypesAS.LIGHTWELL_TYPE.get(), input, this.level);
    }

    public Optional<RecipeHolder<LiquidStarlightRecipe>> findLiquidStarlightRecipe(ItemEntity itemEntity) {
        LiquidStarlightRecipeInput input = LiquidStarlightRecipeInput.of(itemEntity);
        List<RecipeHolder<LiquidStarlightRecipe>> recipes = this.mgr.getRecipesFor(RecipeTypesAS.LIQUID_STARLIGHT_TYPE.get(), input, itemEntity.level());
        recipes = new ArrayList<>(recipes);
        if (recipes.isEmpty()) return Optional.empty();

        recipes.sort(Comparator.<RecipeHolder<LiquidStarlightRecipe>>comparingInt(holder -> holder.value().getOtherInputs().size())
                .thenComparing(RecipeHolder::id));
        return Optional.of(recipes.getFirst());
    }

    public Optional<RecipeHolder<LumenCrystallizationRecipe>> findCrystallizationRecipe(ItemStack stack) {
        LumenCrystallizationRecipeInput input = LumenCrystallizationRecipeInput.of(stack);
        return this.mgr.getRecipeFor(RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.get(), input, this.level);
    }

    public Optional<RecipeHolder<LumenCrystallizationRecipe>> findCrystallizationRecipe(Lumen lumen) {
        LumenCrystallizationRecipeInput input = LumenCrystallizationRecipeInput.of(lumen);
        return this.mgr.getRecipeFor(RecipeTypesAS.LUMEN_CRYSTALLIZATION_TYPE.get(), input, this.level);
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> findRecipe(RecipeType<T> type, ResourceLocation recipeId) {
        return this.mgr.getAllRecipesFor(type).stream()
                .filter(holder -> holder.id().equals(recipeId))
                .findFirst();
    }

    public <I extends RecipeInput, T extends Recipe<I>> Optional<ResourceLocation> getRecipeId(T recipe) {
        RecipeType<T> type = MiscUtil.cast(recipe.getType());
        return this.mgr.getAllRecipesFor(type).stream()
                .filter(holder -> holder.value() == recipe)
                .map(RecipeHolder::id)
                .findFirst();
    }
}
