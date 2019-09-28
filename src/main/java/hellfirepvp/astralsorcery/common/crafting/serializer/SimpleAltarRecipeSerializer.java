/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.CustomAltarRecipeHandler;
import hellfirepvp.astralsorcery.common.crafting.recipe.altar.effect.AltarRecipeEffect;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.util.JsonHelper;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.List;

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
        AltarType type = AltarType.values()[MathHelper.clamp(typeId, 0, AltarType.values().length - 1)];
        int duration = JSONUtils.getInt(json, "duration");
        int starlightRequirement = JSONUtils.getInt(json, "starlight");
        ItemStack output = JsonHelper.getItemStack(json, "output");

        JsonArray pattern = JSONUtils.getJsonArray(json, "pattern");
        JsonObject keys = JSONUtils.getJsonObject(json, "key");
        AltarRecipeGrid grid = AltarRecipeGrid.deserialize(type, pattern, keys);

        SimpleAltarRecipe recipe = new SimpleAltarRecipe(recipeId, type, duration, starlightRequirement, output, grid);
        if (JSONUtils.hasField(json, "recipe_class")) {
            ResourceLocation key = new ResourceLocation(JSONUtils.getString(json, "recipe_class"));
            recipe = CustomAltarRecipeHandler.convert(recipe, key);
            recipe.setCustomRecipeType(key);
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
                    recipe.addTraitInputIngredient(ingredient);
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
        AltarType type = ByteBufUtils.readEnumValue(buffer, AltarType.class);
        int duration = buffer.readInt();
        int starlight = buffer.readInt();
        ItemStack output = ByteBufUtils.readItemStack(buffer);
        AltarRecipeGrid grid = AltarRecipeGrid.read(buffer);
        SimpleAltarRecipe recipe = new SimpleAltarRecipe(recipeId, type, duration, starlight, output, grid);

        ResourceLocation customType = ByteBufUtils.readOptional(buffer, ByteBufUtils::readResourceLocation);
        if (customType != null) {
            recipe = CustomAltarRecipeHandler.convert(recipe, customType);
            recipe.setCustomRecipeType(customType);
        }

        recipe.setFocusConstellation(ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry));
        ByteBufUtils.readList(buffer, Ingredient::read).forEach(recipe::addTraitInputIngredient);
        List<AltarRecipeEffect> effects = ByteBufUtils.readList(buffer, ByteBufUtils::readRegistryEntry);
        for (AltarRecipeEffect effect : effects) {
            recipe.addAltarEffect(effect);
        }
        recipe.readRecipeSync(buffer);
        return recipe;
    }

    @Override
    public void write(PacketBuffer buffer, SimpleAltarRecipe recipe) {
        ByteBufUtils.writeEnumValue(buffer, recipe.getAltarType());
        buffer.writeInt(recipe.getDuration());
        buffer.writeInt(recipe.getStarlightRequirement());
        ByteBufUtils.writeItemStack(buffer, recipe.getRecipeOutput());
        recipe.getInputs().write(buffer);

        ByteBufUtils.writeOptional(buffer, recipe.getCustomRecipeType(), ByteBufUtils::writeResourceLocation);

        ByteBufUtils.writeOptional(buffer, recipe.getFocusConstellation(), ByteBufUtils::writeRegistryEntry);
        ByteBufUtils.writeList(buffer, recipe.getTraitInputIngredients(), (buf, ingredient) -> ingredient.getIngredient().write(buf));
        ByteBufUtils.writeList(buffer, recipe.getCraftingEffects(), ByteBufUtils::writeRegistryEntry);
        recipe.writeRecipeSync(buffer);
    }
}
