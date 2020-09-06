/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipes.builder;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StoneCuttingRecipeBuilder
 * Created by HellFirePvP
 * Date: 22.08.2020 / 16:00
 */
public class StoneCuttingRecipeBuilder {

    private final Ingredient input;
    private final IItemProvider output;
    private final int count;

    private StoneCuttingRecipeBuilder(Ingredient input, IItemProvider output, int count) {
        this.input = input;
        this.output = output;
        this.count = count;
    }

    public static StoneCuttingRecipeBuilder stoneCuttingRecipe(Ingredient input, IItemProvider output) {
        return stoneCuttingRecipe(input, output, 1);
    }

    public static StoneCuttingRecipeBuilder stoneCuttingRecipe(Ingredient input, IItemProvider output, int count) {
        return new StoneCuttingRecipeBuilder(input, output, count);
    }

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.output.asItem()));
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        id = NameUtil.prefixPath(id, "stonecutting/");
        consumerIn.accept(new Result(id, this.input, this.output.asItem(), this.count));
    }

    public static class Result implements IFinishedRecipe {

        private final ResourceLocation id;
        private final Ingredient ingredient;
        private final Item result;
        private final int count;

        public Result(ResourceLocation id, Ingredient input, Item output, int count) {
            this.id = id;
            this.ingredient = input;
            this.result = output;
            this.count = count;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            jsonObject.add("ingredient", this.ingredient.serialize());
            jsonObject.addProperty("result", ForgeRegistries.ITEMS.getKey(this.result).toString());
            jsonObject.addProperty("count", this.count);
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return IRecipeSerializer.STONECUTTING;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return new ResourceLocation("");
        }
    }
}
