/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.helper.ingredient.FluidIngredient;
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WellRecipeSerializer
 * Created by HellFirePvP
 * Date: 30.06.2019 / 23:29
 */
public class WellRecipeSerializer extends CustomRecipeSerializer<WellLiquefaction> {

    public WellRecipeSerializer() {
        super(RecipeSerializersAS.WELL_LIQUEFACTION);
    }

    @Override
    public WellLiquefaction read(ResourceLocation recipeId, JsonObject json) {
        String grp = JSONUtils.getString(json, "group", "");
        Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
        Ingredient output = Ingredient.deserialize(JSONUtils.getJsonObject(json, "output"));
        if (!(output instanceof FluidIngredient)) {
            throw new JsonParseException("Expected astralsorcery:fluid as 'output'!");
        }
        if (((FluidIngredient) output).getFluids().size() != 1) {
            throw new JsonParseException("Expected 'output' to be a single fluid, not multiple!");
        }
        FluidStack fluid = ((FluidIngredient) output).getFluids().get(0);
        float productionMultiplier = JSONUtils.getFloat(json, "productionMultiplier");
        float shatterMultiplier = JSONUtils.getFloat(json, "shatterMultiplier");
        Color color = null;
        if (json.has("color")) {
            color = JsonHelper.getColor(json, "color");
        }
        WellLiquefaction recipe = new WellLiquefaction(recipeId, input, fluid, color, productionMultiplier, shatterMultiplier);
        recipe.setGroup(grp);
        return recipe;
    }

    @Override
    public WellLiquefaction read(ResourceLocation recipeId, PacketBuffer buffer) {
        Ingredient input = Ingredient.read(buffer);
        FluidStack fluid = ByteBufUtils.readFluidStack(buffer);
        float shatter = buffer.readFloat();
        float production = buffer.readFloat();
        Color color = ByteBufUtils.readOptional(buffer, buf -> new Color(buf.readInt(), true));
        return new WellLiquefaction(recipeId, input, fluid, color, production, shatter);
    }

    @Override
    public void write(PacketBuffer buffer, WellLiquefaction recipe) {
        recipe.getInput().write(buffer);
        ByteBufUtils.writeFluidStack(buffer, recipe.getFluidOutput());
        buffer.writeFloat(recipe.getShatterMultiplier());
        buffer.writeFloat(recipe.getProductionMultiplier());
        ByteBufUtils.writeOptional(buffer, recipe.getCatalystColor(), (buf, color) -> buf.writeInt(color.getRGB()));
    }
}
