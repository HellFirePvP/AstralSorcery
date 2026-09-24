/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipe;
import hellfirepvp.astralsorcery.common.research.tome.TomePageRecipe;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ResolvingRecipeTypeRegistryObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GeneratedRecipeBuffer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class GeneratedRecipeBuffer {

    private static final Map<RecipeType<?>, List<RecipeReference<?>>> TRACKED_RECIPES = new HashMap<>();

    public static <T extends Recipe<?>> void addRecipe(ResolvingRecipeTypeRegistryObject<T> type, ResourceLocation id, T recipe) {
        addRecipe(type.get(), id, recipe, type.outputMatchProvider().apply(recipe));
    }

    public static <T extends Recipe<?>> void addRecipe(RecipeType<?> recipeType, ResourceLocation id, T recipe, ItemStack matchOutput) {
        TRACKED_RECIPES.computeIfAbsent(recipeType, t -> new ArrayList<>())
                .add(new RecipeReference<>(id, recipe, matchOutput));
    }

    public static TomePageRecipe findRecipe(RecipeType<?> type, ItemLike output) {
        return findRecipe(type, new ItemStack(output));
    }

    public static TomePageRecipe findRecipe(RecipeType<?> type, ItemStack output) {
        return BuiltInRegistries.RECIPE_TYPE.getResourceKey(type)
                .map(BuiltInRegistries.RECIPE_TYPE::getHolderOrThrow)
                .map(holder -> findRecipe(holder, output))
                .orElseThrow();
    }

    public static TomePageRecipe findRecipe(ResolvingRecipeTypeRegistryObject<?> type, ItemLike output) {
        return findRecipe(type.holder(), new ItemStack(output));
    }

    public static TomePageRecipe findRecipe(ResolvingRecipeTypeRegistryObject<?> type, ItemStack output) {
        return findRecipe(type.holder(), output);
    }

    public static TomePageRecipe findRecipe(Holder<RecipeType<?>> type, ItemLike output) {
        return findRecipe(type, new ItemStack(output));
    }

    public static TomePageRecipe findRecipe(Holder<RecipeType<?>> type, ItemStack output) {
        return find(type.value(), output)
                .map(ref -> TomePageRecipe.of(type.getKey(), ref.id()))
                .orElseThrow();
    }

    public static <T extends Recipe<?>> Optional<RecipeReference<T>> find(RecipeType<T> type, ItemLike output) {
        return find(type, new ItemStack(output));
    }

    public static <T extends Recipe<?>> Optional<RecipeReference<T>> find(RecipeType<T> type, ItemStack output) {
        return TRACKED_RECIPES.getOrDefault(type, Collections.emptyList()).stream()
                .filter(ref -> ItemStack.isSameItemSameComponents(ref.generatedOutput(), output))
                .findFirst()
                .map(MiscUtil::<RecipeReference<T>, RecipeReference<?>>cast);
    }

    public static class BufferedRecipeOutput implements RecipeOutput {

        private final RecipeOutput decorated;
        private final HolderLookup.Provider provider;

        public BufferedRecipeOutput(RecipeOutput decorated, HolderLookup.Provider provider) {
            this.decorated = decorated;
            this.provider = provider;
        }

        @Override
        public Advancement.Builder advancement() {
            return this.decorated.advancement();
        }

        @Override
        public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            this.decorated.accept(id, recipe, advancement, conditions);
            if (recipe instanceof CustomRecipe customRecipe) {
                addRecipe(customRecipe.getRecipeType(), id, recipe);
            } else if (recipe instanceof ShapedRecipe ||
                    recipe instanceof ShapelessRecipe ||
                    recipe instanceof AbstractCookingRecipe) {
                ItemStack result = recipe.getResultItem(this.provider).copy();
                addRecipe(recipe.getType(), id, recipe, result);
            }
        }
    }

    public record RecipeReference<T extends Recipe<?>>(ResourceLocation id, T recipe, ItemStack generatedOutput) {}
}
