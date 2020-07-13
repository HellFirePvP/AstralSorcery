/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.recipe.builder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleShapedRecipeBuilder
 * Created by HellFirePvP
 * Date: 07.03.2020 / 09:45
 */
public class SimpleShapedRecipeBuilder {

    private final ItemStack result;
    private final List<String> pattern = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();

    private String subDirectory = null;

    private SimpleShapedRecipeBuilder(IItemProvider result, int count) {
        this(new ItemStack(result.asItem(), count));
    }

    private SimpleShapedRecipeBuilder(ItemStack result) {
        this.result = result.copy();
    }

    public static SimpleShapedRecipeBuilder shapedRecipe(IItemProvider result) {
        return shapedRecipe(result, 1);
    }

    public static SimpleShapedRecipeBuilder shapedRecipe(IItemProvider result, int count) {
        return new SimpleShapedRecipeBuilder(result, count);
    }

    public SimpleShapedRecipeBuilder key(Character symbol, Tag<Item> tag) {
        return this.key(symbol, Ingredient.fromTag(tag));
    }

    public SimpleShapedRecipeBuilder key(Character symbol, IItemProvider item) {
        return this.key(symbol, Ingredient.fromItems(item));
    }

    public SimpleShapedRecipeBuilder key(Character symbol, Ingredient ingredientIn) {
        if (this.key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        } else if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(symbol, ingredientIn);
            return this;
        }
    }

    public SimpleShapedRecipeBuilder patternLine(String patternIn) {
        if (!this.pattern.isEmpty() && patternIn.length() != this.pattern.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.pattern.add(patternIn);
            return this;
        }
    }

    public SimpleShapedRecipeBuilder subDirectory(String dir) {
        this.subDirectory = dir;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumerIn) {
        this.build(consumerIn, ForgeRegistries.ITEMS.getKey(this.result.getItem()));
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, String save) {
        ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result.getItem());
        if ((new ResourceLocation(save)).equals(resourcelocation)) {
            throw new IllegalStateException("Shaped Recipe " + save + " should remove its 'save' argument");
        } else {
            this.build(consumerIn, new ResourceLocation(save));
        }
    }

    public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
        this.validate(id);
        String path = id.getPath();
        if (this.subDirectory != null && !this.subDirectory.isEmpty()) {
            path = this.subDirectory + "/" + path;
        }
        id = new ResourceLocation(id.getNamespace(), "shaped/" + path);
        consumerIn.accept(new Result(id, this.result, this.pattern, this.key));
    }

    private void validate(ResourceLocation id) {
        if (this.pattern.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + id + "!");
        } else {
            Set<Character> set = Sets.newHashSet(this.key.keySet());
            set.remove(' ');

            for(String s : this.pattern) {
                for(int i = 0; i < s.length(); ++i) {
                    char c0 = s.charAt(i);
                    if (!this.key.containsKey(c0) && c0 != ' ') {
                        throw new IllegalStateException("Pattern in recipe " + id + " uses undefined symbol '" + c0 + "'");
                    }

                    set.remove(c0);
                }
            }

            if (!set.isEmpty()) {
                throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + id);
            } else if (this.pattern.size() == 1 && this.pattern.get(0).length() == 1) {
                throw new IllegalStateException("Shaped recipe " + id + " only takes in a single item - should it be a shapeless recipe instead?");
            }
        }
    }

    public class Result implements IFinishedRecipe {

        private final ResourceLocation id;
        private final ItemStack result;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;

        public Result(ResourceLocation idIn, ItemStack resultIn, List<String> patternIn, Map<Character, Ingredient> keyIn) {
            this.id = idIn;
            this.result = resultIn;
            this.pattern = patternIn;
            this.key = keyIn;
        }

        public void serialize(JsonObject json) {
            JsonArray jsonarray = new JsonArray();
            for(String s : this.pattern) {
                jsonarray.add(s);
            }
            json.add("pattern", jsonarray);

            JsonObject keys = new JsonObject();
            for(Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                keys.add(String.valueOf(entry.getKey()), entry.getValue().serialize());
            }
            json.add("key", keys);

            json.add("result", JsonHelper.serializeItemStack(this.result));
        }

        public IRecipeSerializer<?> getSerializer() {
            return IRecipeSerializer.CRAFTING_SHAPED;
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
