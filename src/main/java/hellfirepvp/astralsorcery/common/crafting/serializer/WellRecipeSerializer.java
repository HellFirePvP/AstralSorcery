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
import hellfirepvp.astralsorcery.common.crafting.recipe.WellLiquefaction;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

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
        Ingredient input = Ingredient.deserialize(JSONUtils.getJsonObject(json, "input"));
        String fluidKey = JSONUtils.getString(json, "output");
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidKey));
        if (fluid == null) {
            throw new JsonSyntaxException("Unknown fluid: " + fluidKey);
        }
        float productionMultiplier = JSONUtils.getFloat(json, "productionMultiplier");
        float shatterMultiplier = JSONUtils.getFloat(json, "shatterMultiplier");
        Color color = null;
        if (json.has("color")) {
            color = JsonHelper.getColor(json, "color");
        }
        return new WellLiquefaction(recipeId, input, fluid, color, productionMultiplier, shatterMultiplier);
    }

    @Override
    public WellLiquefaction read(ResourceLocation recipeId, PacketBuffer buffer) {
        Ingredient input = Ingredient.read(buffer);
        Fluid fluid = ByteBufUtils.readRegistryEntry(buffer);
        float shatter = buffer.readFloat();
        float production = buffer.readFloat();
        Color color = ByteBufUtils.readOptional(buffer, buf -> new Color(buf.readInt(), true));
        return new WellLiquefaction(recipeId, input, fluid, color, production, shatter);
    }

    @Override
    public void write(PacketBuffer buffer, WellLiquefaction recipe) {
        recipe.getInput().write(buffer);
        ByteBufUtils.writeRegistryEntry(buffer, recipe.getFluidOutput());
        buffer.writeFloat(recipe.getShatterMultiplier());
        buffer.writeFloat(recipe.getProductionMultiplier());
        ByteBufUtils.writeOptional(buffer, recipe.getCatalystColor(), (buf, color) -> buf.writeInt(color.getRGB()));
    }

    @Override
    public void write(JsonObject object, WellLiquefaction recipe) {
        object.add("input", recipe.getInput().serialize());
        object.addProperty("output", recipe.getFluidOutput().getRegistryName().toString());
        object.addProperty("productionMultiplier", recipe.getProductionMultiplier());
        object.addProperty("shatterMultiplier", recipe.getShatterMultiplier());
        object.addProperty("color", recipe.getCatalystColor() == null ? Color.WHITE.getRGB() : recipe.getCatalystColor().getRGB());
    }
}
