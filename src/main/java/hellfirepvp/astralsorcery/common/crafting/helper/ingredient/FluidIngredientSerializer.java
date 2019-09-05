/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

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
        ResourceLocation key = new ResourceLocation(JSONUtils.getString(json, "fluid"));
        if (!ForgeRegistries.FLUIDS.containsKey(key)) {
            throw new JsonSyntaxException("Unknown fluid '" + key + "'");
        }
        int amount = FluidAttributes.BUCKET_VOLUME;
        if (json.has("amount")) {
            amount = JSONUtils.getInt(json, "amount");
        }
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(key);
        return new FluidIngredient(new FluidStack(fluid, amount));
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
