/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.builder;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeBuilder;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeTypeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 11:14
 */
public class SimpleAltarRecipeBuilder<T extends SimpleAltarRecipe> extends CustomRecipeBuilder<T> {

    private T recipe;

    private SimpleAltarRecipeBuilder(T recipe) {
        this.recipe = recipe;
    }

    public static TypedRecipeBuilder<SimpleAltarRecipe> builder() {
        return new TypedRecipeBuilder<>(null);
    }

    public static <T extends SimpleAltarRecipe> TypedRecipeBuilder<T> ofType(AltarRecipeTypeHandler.Type<T> type) {
        return new TypedRecipeBuilder<>(type);
    }

    public SimpleAltarRecipeBuilder<T> setInputs(AltarRecipeGrid.Builder gridBuilder) {
        this.recipe.setInputs(gridBuilder.build());
        return this;
    }

    public SimpleAltarRecipeBuilder<T> setFocusConstellation(IConstellation focusConstellation) {
        this.recipe.setFocusConstellation(focusConstellation);
        return this;
    }

    public SimpleAltarRecipeBuilder<T> addRelayInput(Tag<Item> tag) {
        return this.addRelayInput(Ingredient.fromTag(tag));
    }

    public SimpleAltarRecipeBuilder<T> addRelayInput(IItemProvider item) {
        return this.addRelayInput(Ingredient.fromItems(item));
    }

    public SimpleAltarRecipeBuilder<T> addRelayInput(Ingredient ingredient) {
        this.recipe.addRelayInput(ingredient);
        return this;
    }

    public SimpleAltarRecipeBuilder<T> addAltarEffect(AltarRecipeEffect effect) {
        this.recipe.addAltarEffect(effect);
        return this;
    }

    public SimpleAltarRecipeBuilder<T> setStarlightRequirement(float percentOfAltarBar) {
        this.recipe.setStarlightRequirement((int) (this.recipe.getAltarType().getStarlightCapacity() * MathHelper.clamp(percentOfAltarBar, 0F, 1F)));
        return this;
    }

    public SimpleAltarRecipeBuilder<T> setStarlightRequirement(int requiredStarlight) {
        this.recipe.setStarlightRequirement(requiredStarlight);
        return this;
    }

    public SimpleAltarRecipeBuilder<T> multiplyDuration(float duration) {
        this.recipe.setDuration((int) (this.recipe.getAltarType().getDefaultAltarCraftingDuration() * duration));
        return this;
    }

    public SimpleAltarRecipeBuilder<T> setDuration(int duration) {
        this.recipe.setDuration(duration);
        return this;
    }

    public SimpleAltarRecipeBuilder<T> addOutput(IItemProvider output) {
        this.addOutput(new ItemStack(output));
        return this;
    }

    public SimpleAltarRecipeBuilder<T> addOutput(ItemStack output) {
        this.recipe.addOutput(output);
        return this;
    }

    public SimpleAltarRecipeBuilder<T> modify(Consumer<T> recipeFn) {
        recipeFn.accept(this.recipe);
        return this;
    }

    @Nonnull
    @Override
    protected T validateAndGet() {
        AltarType type = this.recipe.getAltarType();
        this.recipe.getInputs().validate(type);

        if (!type.isThisGEThan(AltarType.RADIANCE)) {
            if (this.recipe.getRelayInputs().size() > 0) {
                throw new IllegalArgumentException("Cannot make a altar recipe require relay inputs, if the recipe isn't for a T4 altar or higher.");
            }
            if (this.recipe.getFocusConstellation() != null) {
                throw new IllegalArgumentException("Cannot make a altar recipe require a constellation focus, if the recipe isn't for a T4 altar or higher.");
            }
        }
        if (this.recipe.getDuration() <= 0) {
            throw new IllegalArgumentException("Cannot make a altar recipe with 0 or less ticks duration!");
        }
        if (this.recipe.getStarlightRequirement() > type.getStarlightCapacity()) {
            throw new IllegalArgumentException("Cannot make a recipe require more starlight than the capacity of the corresponding altar allows for.");
        }

        return this.recipe;
    }

    @Override
    protected CustomRecipeSerializer<T> getSerializer() {
        return (CustomRecipeSerializer<T>) RecipeSerializersAS.ALTAR_RECIPE_SERIALIZER;
    }

    public static class TypedRecipeBuilder<T extends SimpleAltarRecipe> {

        @Nullable
        private final AltarRecipeTypeHandler.Type<T> type;

        private TypedRecipeBuilder(@Nullable AltarRecipeTypeHandler.Type<T> type) {
            this.type = type;
        }

        public SimpleAltarRecipeBuilder<T> createRecipe(ForgeRegistryEntry<?> nameProvider, AltarType altarType) {
            return this.createRecipe(AstralSorcery.key(nameProvider.getRegistryName().getPath()), altarType);
        }

        public SimpleAltarRecipeBuilder<T> createRecipe(ResourceLocation recipeId, AltarType altarType) {
            SimpleAltarRecipe recipe = new SimpleAltarRecipe(recipeId, altarType);
            T converted;
            if (type == null) {
                converted = (T) AltarRecipeTypeHandler.DEFAULT.convert(recipe);
            } else {
                converted = this.type.convert(recipe);
                converted.setCustomRecipeType(this.type.getKey());
            }
            return new SimpleAltarRecipeBuilder<>(converted);
        }
    }
}
