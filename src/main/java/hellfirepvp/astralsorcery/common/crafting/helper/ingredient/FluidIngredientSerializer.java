/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidIngredientSerializer
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:39
 */
public class FluidIngredientSerializer implements IIngredientSerializer<FluidIngredient> {

    @Override
    public FluidIngredient parse(JsonObject json) {
        if (!json.has("fluid")) {
            throw new JsonSyntaxException("Expected an array at 'fluid' or a single fluid defined at key 'fluid'.");
        }

        List<FluidStack> foundFluids = new ArrayList<>();
        JsonElement element = json.get("fluid");
        if (element.isJsonArray()) {
            element.getAsJsonArray().forEach(e -> {
                if (e.isJsonObject()) {
                    JsonObject object = e.getAsJsonObject();
                    ResourceLocation key = new ResourceLocation(JSONUtils.getString(object, "fluid"));
                    if (!ForgeRegistries.FLUIDS.containsKey(key)) {
                        throw new JsonSyntaxException("Unknown fluid '" + key + "'");
                    }
                    int amount = FluidAttributes.BUCKET_VOLUME;
                    if (object.has("amount")) {
                        amount = JSONUtils.getInt(object, "amount");
                    }
                    Fluid fluid = ForgeRegistries.FLUIDS.getValue(key);
                    foundFluids.add(new FluidStack(fluid, amount));
                } else if (e.isJsonPrimitive()) {
                    ResourceLocation key = new ResourceLocation(JSONUtils.getString(element, "fluid"));
                    if (!ForgeRegistries.FLUIDS.containsKey(key)) {
                        throw new JsonSyntaxException("Unknown fluid '" + key + "'");
                    }
                    Fluid fluid = ForgeRegistries.FLUIDS.getValue(key);
                    foundFluids.add(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME));
                } else {
                    throw new JsonSyntaxException("Value at key 'fluid' has to be a fluid name or an array of fluid names or objects containing 'fluid'.");
                }
            });
        } else if (element.isJsonPrimitive()) {
            ResourceLocation key = new ResourceLocation(JSONUtils.getString(element, "fluid"));
            if (!ForgeRegistries.FLUIDS.containsKey(key)) {
                throw new JsonSyntaxException("Unknown fluid '" + key + "'");
            }
            int amount = FluidAttributes.BUCKET_VOLUME;
            if (json.has("amount")) {
                amount = JSONUtils.getInt(json, "amount");
            }
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(key);
            foundFluids.add(new FluidStack(fluid, amount));
        } else {
            throw new JsonSyntaxException("Value at key 'fluid' has to be a fluid name or an array of fluid names or objects containing 'fluid'.");
        }
        return new FluidIngredient(foundFluids);
    }

    @Override
    public FluidIngredient parse(PacketBuffer buffer) {
        return new FluidIngredient(ByteBufUtils.readList(buffer, ByteBufUtils::readFluidStack));
    }

    @Override
    public void write(PacketBuffer buffer, FluidIngredient ingredient) {
        ByteBufUtils.writeList(buffer, ingredient.getFluids(), ByteBufUtils::writeFluidStack);
    }

}
