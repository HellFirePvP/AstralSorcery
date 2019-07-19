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
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientSerializer;
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
    public FluidIngredient parse(PacketBuffer buffer) {
        int size = buffer.readInt();
        List<CompatFluidStack> fluidStacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            fluidStacks.add(ByteBufUtils.readFluidStack(buffer));
        }
        return new FluidIngredient(fluidStacks);
    }

    @Override
    public FluidIngredient parse(JsonObject json) {
        ResourceLocation key = new ResourceLocation(JSONUtils.getString(json, "fluid"));
        if (!ForgeRegistries.FLUIDS.containsKey(key)) {
            throw new JsonSyntaxException("Unknown fluid '" + key + "'");
        }
        int amount = CompatFluidStack.BUCKET_VOLUME;
        if (json.has("amount")) {
            amount = JSONUtils.getInt(json, "amount");
        }
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(key);
        return new FluidIngredient(new CompatFluidStack(fluid, amount));
    }

    @Override
    public void write(PacketBuffer buffer, FluidIngredient ingredient) {
        List<CompatFluidStack> fluidStacks = ingredient.getFluids();
        buffer.writeInt(fluidStacks.size());
        for (CompatFluidStack fluidStack : fluidStacks) {
            ByteBufUtils.writeFluidStack(buffer, fluidStack);
        }
    }

}
