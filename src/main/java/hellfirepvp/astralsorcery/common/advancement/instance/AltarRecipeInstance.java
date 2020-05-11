/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.advancement.instance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.advancement.AltarCraftTrigger;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AltarRecipeInstance
 * Created by HellFirePvP
 * Date: 11.05.2020 / 20:28
 */
public class AltarRecipeInstance extends CriterionInstance {

    private Set<ResourceLocation> recipeNames = new HashSet<>();
    private List<Ingredient> recipeOutputs = new ArrayList<>();

    private AltarRecipeInstance(ResourceLocation id) {
        super(id);
    }

    public static AltarRecipeInstance craftRecipe(ResourceLocation... recipeIds) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        instance.recipeNames.addAll(Arrays.asList(recipeIds));
        return instance;
    }

    public static AltarRecipeInstance craftRecipe(SimpleAltarRecipe... recipes) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        Arrays.asList(recipes).forEach(recipe -> instance.recipeNames.add(recipe.getId()));
        return instance;
    }

    public static AltarRecipeInstance withOutput(IItemProvider... outputs) {
        return withOutput(Ingredient.fromItems(outputs));
    }

    public static AltarRecipeInstance withOutput(ItemStack... outputs) {
        return withOutput(Ingredient.fromStacks(outputs));
    }

    public static AltarRecipeInstance withOutput(Tag<Item>... outputs) {
        return withOutput(Arrays.stream(outputs).map(Ingredient::fromTag).collect(Collectors.toList()));
    }

    public static AltarRecipeInstance withOutput(Ingredient... outputs) {
        return withOutput(Arrays.asList(outputs));
    }

    public static AltarRecipeInstance withOutput(List<Ingredient> outputs) {
        AltarRecipeInstance instance = new AltarRecipeInstance(AltarCraftTrigger.ID);
        instance.recipeOutputs.addAll(outputs);
        return instance;
    }

    @Override
    public JsonElement serialize() {
        JsonObject out = new JsonObject();
        if (!this.recipeNames.isEmpty()) {
            JsonArray names = new JsonArray();
            for (ResourceLocation name : this.recipeNames) {
                names.add(name.toString());
            }
            out.add("recipeNames", names);
        }
        if (!this.recipeOutputs.isEmpty()) {
            JsonArray outputs = new JsonArray();
            for (Ingredient output : this.recipeOutputs) {
                outputs.add(output.serialize());
            }
            out.add("recipeOutputs", outputs);
        }
        return out;
    }

    public static AltarRecipeInstance deserialize(ResourceLocation id, JsonObject json) {
        AltarRecipeInstance instance = new AltarRecipeInstance(id);
        JsonArray recipeNames = JSONUtils.getJsonArray(json, "recipeNames", new JsonArray());
        for (int idx = 0; idx < recipeNames.size(); idx++) {
            JsonElement element = recipeNames.get(idx);
            String key = JSONUtils.getString(element, String.format("recipeNames[%s]", idx));
            instance.recipeNames.add(new ResourceLocation(key));
        }
        for (JsonElement element : JSONUtils.getJsonArray(json, "recipeOutputs", new JsonArray())) {
            instance.recipeOutputs.add(Ingredient.deserialize(element));
        }
        return instance;
    }

    public boolean test(SimpleAltarRecipe recipe, ItemStack output) {
        if (this.recipeNames.isEmpty() && this.recipeOutputs.isEmpty()) {
            return true;
        }
        ResourceLocation recipeName = recipe.getId();
        if (this.recipeNames.contains(recipeName)) {
            return true;
        }
        for (Ingredient i : this.recipeOutputs) {
            if (i.test(output)) {
                return true;
            }
        }
        return false;
    }
}
