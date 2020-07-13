/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.builder;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.CookingRecipeSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ResultCookingRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 08:11
 */
//ItemStack (item + count) result sensitive version...
public class ResultCookingRecipeBuilder {

    private final ItemStack result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final CookingRecipeSerializer<?> recipeSerializer;

    private ResultCookingRecipeBuilder(ItemStack result, Ingredient ingredientIn, float experienceIn, int cookingTimeIn, CookingRecipeSerializer<?> serializer) {
        this.result = result.copy();
        this.ingredient = ingredientIn;
        this.experience = experienceIn;
        this.cookingTime = cookingTimeIn;
        this.recipeSerializer = serializer;
    }

    public static ResultCookingRecipeBuilder cookingRecipe(Ingredient ingredientIn, ItemStack result, float experienceIn, int cookingTimeIn, CookingRecipeSerializer<?> serializer) {
        return new ResultCookingRecipeBuilder(result, ingredientIn, experienceIn, cookingTimeIn, serializer);
    }

    public static ResultCookingRecipeBuilder blastingRecipe(Ingredient ingredientIn, ItemStack result, float experienceIn, int cookingTimeIn) {
        return cookingRecipe(ingredientIn, result, experienceIn, cookingTimeIn, IRecipeSerializer.BLASTING);
    }

    public static ResultCookingRecipeBuilder smeltingRecipe(Ingredient ingredientIn, ItemStack result, float experienceIn, int cookingTimeIn) {
        return cookingRecipe(ingredientIn, result, experienceIn, cookingTimeIn, IRecipeSerializer.SMELTING);
    }

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
        ResourceLocation itemKey = ForgeRegistries.ITEMS.getKey(this.result.getItem());
        ResourceLocation saveNameKey = new ResourceLocation(save);
        if (saveNameKey.equals(itemKey)) {
            throw new IllegalStateException("Recipe " + saveNameKey + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, saveNameKey);
        }
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), this.recipeSerializer.getRegistryName().getPath() + "/" + id.getPath());
        consumerIn.accept(new Result(id, this.ingredient, this.result, this.experience, this.cookingTime, this.recipeSerializer));
    }

    public static class Result implements IFinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final ItemStack result;
        private final float experience;
        private final int cookingTime;
        private final IRecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation idIn, Ingredient ingredientIn, ItemStack resultIn, float experienceIn, int cookingTimeIn, IRecipeSerializer<? extends AbstractCookingRecipe> serializerIn) {
            this.id = idIn;
            this.ingredient = ingredientIn;
            this.result = resultIn;
            this.experience = experienceIn;
            this.cookingTime = cookingTimeIn;
            this.serializer = serializerIn;
        }

        public void serialize(JsonObject json) {
            JsonObject itemResult = new JsonObject();
            itemResult.addProperty("item", this.result.getItem().getRegistryName().toString());
            itemResult.addProperty("count", this.result.getCount());

            json.add("ingredient", this.ingredient.serialize());
            json.add("result", itemResult);
            json.addProperty("experience", this.experience);
            json.addProperty("cookingtime", this.cookingTime);
        }

        public IRecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        public ResourceLocation getID() {
            return this.id;
        }

        @Nullable
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
        }
    }
}
