/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LiquidInfusionSerializer
 * Created by HellFirePvP
 * Date: 26.07.2019 / 21:30
 */
public class LiquidInfusionSerializer extends CustomRecipeSerializer<LiquidInfusion> {

    public LiquidInfusionSerializer() {
        super(RecipeSerializersAS.LIQUID_INFUSION);
    }

    @Override
    public LiquidInfusion read(ResourceLocation recipeId, JsonObject json) {
        ResourceLocation fluidKey = new ResourceLocation(JSONUtils.getString(json, "fluidInput"));
        Fluid fluidInput = ForgeRegistries.FLUIDS.getValue(fluidKey);
        if (fluidInput == null || fluidInput == Fluids.EMPTY) {
            throw new JsonSyntaxException("Unknown fluid: " + fluidKey);
        }

        Ingredient input = CraftingHelper.getIngredient(json.get("input"));
        ItemStack output = JsonHelper.getItemStack(json.get("output"), "output");
        float consumptionChance = JSONUtils.getFloat(json, "consumptionChance");
        int duration = JSONUtils.getInt(json, "duration");

        boolean consumeMultipleFluids = JSONUtils.getBoolean(json, "consumeMultipleFluids", false);
        boolean acceptChaliceInput = JSONUtils.getBoolean(json, "acceptChaliceInput", true);
        boolean copyNBTToOutputs = JSONUtils.getBoolean(json, "copyNBTToOutputs", false);
        return new LiquidInfusion(recipeId, duration, fluidInput, input, output, consumptionChance, consumeMultipleFluids, acceptChaliceInput, copyNBTToOutputs);
    }

    @Override
    public LiquidInfusion read(ResourceLocation recipeId, PacketBuffer buffer) {
        return LiquidInfusion.read(recipeId, buffer);
    }

    @Override
    public void write(JsonObject object, LiquidInfusion recipe) {
        recipe.write(object);
    }

    @Override
    public void write(PacketBuffer buffer, LiquidInfusion recipe) {
        recipe.write(buffer);
    }
}
