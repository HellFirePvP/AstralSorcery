/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.tile.altar.AltarType;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.SimpleAltarRecipe;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeGrid;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.AltarRecipeTypeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleAltarRecipeSerializer
 * Created by HellFirePvP
 * Date: 12.08.2019 / 19:45
 */
public class SimpleAltarRecipeSerializer extends CustomRecipeSerializer<SimpleAltarRecipe> {

    public SimpleAltarRecipeSerializer() {
        super(RecipeSerializersAS.SIMPLE_ALTAR_CRAFTING);
    }

    @Override
    public SimpleAltarRecipe read(ResourceLocation recipeId, JsonObject json) {
        int typeId = JSONUtils.getInt(json, "altar_type");
        AltarType type = MiscUtils.getEnumEntry(AltarType.class, typeId);
        int duration = JSONUtils.getInt(json, "duration");
        int starlightRequirement = JSONUtils.getInt(json, "starlight");

        AltarRecipeGrid grid = AltarRecipeGrid.deserialize(type, json);
        grid.validate(type);

        SimpleAltarRecipe recipe = new SimpleAltarRecipe(recipeId, type, duration, starlightRequirement, grid);
        if (JSONUtils.hasField(json, "recipe_class")) {
            ResourceLocation key = new ResourceLocation(JSONUtils.getString(json, "recipe_class"));
            recipe = AltarRecipeTypeHandler.convert(recipe, key);
            recipe.setCustomRecipeType(key);
        }

        if (JSONUtils.isJsonArray(json, "output")) {
            JsonArray outputArray = JSONUtils.getJsonArray(json, "output");
            for (int i = 0; i < outputArray.size(); i++) {
                recipe.addOutput(JsonHelper.getItemStack(outputArray.get(i), String.format("output[%s]", i)));
            }
        } else {
            recipe.addOutput(JsonHelper.getItemStack(json, "output"));
        }

        JsonObject recipeOptions = new JsonObject();
        if (JSONUtils.hasField(json, "options")) {
            recipeOptions = JSONUtils.getJsonObject(json, "options");
        }
        recipe.deserializeAdditionalJson(recipeOptions);

        if (JSONUtils.hasField(json, "focus_constellation")) {
            ResourceLocation key = new ResourceLocation(JSONUtils.getString(json, "focus_constellation"));
            IConstellation cst = RegistriesAS.REGISTRY_CONSTELLATIONS.getValue(key);
            if (cst == null) {
                throw new JsonSyntaxException("Unknown constellation " + key.toString());
            }
            recipe.setFocusConstellation(cst);
        }

        if (JSONUtils.hasField(json, "relay_inputs")) {
            JsonArray relayIngredients = JSONUtils.getJsonArray(json, "relay_inputs");
            for (int i = 0; i < relayIngredients.size(); i++) {
                JsonElement element = relayIngredients.get(i);
                Ingredient ingredient = Ingredient.deserialize(element);
                if (!ingredient.hasNoMatchingItems()) {
                    recipe.addRelayInput(ingredient);
                } else {
                    AstralSorcery.log.warn("Skipping relay_inputs[" + i + "] for recipe " + recipeId + " as the ingredient has no matching items!");
                    AstralSorcery.log.warn("Ingredient skipped: " + JSONUtils.toString(element));
                }
            }
        }

        if (JSONUtils.hasField(json, "effects")) {
            JsonArray effectNames = JSONUtils.getJsonArray(json, "effects");
            for (int i = 0; i < effectNames.size(); i++) {
                JsonElement element = effectNames.get(i);
                ResourceLocation effectKey = new ResourceLocation(JSONUtils.getString(element, "effects[" + i + "]"));
                AltarRecipeEffect effect = RegistriesAS.REGISTRY_ALTAR_EFFECTS.getValue(effectKey);
                if (effect == null) {
                    throw new JsonSyntaxException("No altar effect for name " + effectKey + "! (Found at: effects[" + i + "])");
                }
                recipe.addAltarEffect(effect);
            }
        }

        return recipe;
    }

    @Override
    public SimpleAltarRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
        return SimpleAltarRecipe.read(recipeId, buffer);
    }

    @Override
    public void write(JsonObject object, SimpleAltarRecipe recipe) {
        recipe.write(object);
    }

    @Override
    public void write(PacketBuffer buffer, SimpleAltarRecipe recipe) {
        recipe.write(buffer);
    }
}
