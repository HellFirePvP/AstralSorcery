/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.serializer;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.crafting.helper.CustomRecipeSerializer;
import hellfirepvp.astralsorcery.common.crafting.recipe.LiquidInfusion;
import hellfirepvp.astralsorcery.common.lib.RecipeSerializersAS;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.JsonHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;

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
        FluidStack fluidInput = JsonHelper.getFluidStack(json.get("fluidInput"), "fluidInput");
        Ingredient input = CraftingHelper.getIngredient(json.get())
        return null;
    }

    @Override
    public LiquidInfusion read(ResourceLocation recipeId, PacketBuffer buffer) {
        Fluid fluidIn = ByteBufUtils.readRegistryEntry(buffer);
        Ingredient itemIn = Ingredient.read(buffer);
        ItemStack output = ByteBufUtils.readItemStack(buffer);
        float consumptionChance = buffer.readFloat();
        boolean consumeMultiple = buffer.readBoolean();
        boolean acceptChalice = buffer.readBoolean();
        return new LiquidInfusion(recipeId, fluidIn, itemIn, output, consumptionChance, consumeMultiple, acceptChalice);
    }

    @Override
    public void write(PacketBuffer buffer, LiquidInfusion recipe) {
        ByteBufUtils.writeRegistryEntry(buffer, recipe.getLiquidInput());
        recipe.getItemInput().write(buffer);
        ByteBufUtils.writeItemStack(buffer, recipe.getItemOutput());
        buffer.writeFloat(recipe.getConsumptionChance());
        buffer.writeBoolean(recipe.doesConsumeMultipleFluids());
        buffer.writeBoolean(recipe.acceptsChaliceInput());
    }
}
