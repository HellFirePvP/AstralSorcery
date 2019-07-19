/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper.ingredient;

import com.google.gson.JsonObject;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;

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
        return new FluidIngredient(); // TODO fluids
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
