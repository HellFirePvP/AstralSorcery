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
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.fluids.FluidStack;

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
        List<FluidStack> fluidStacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            fluidStacks.add(ByteBufUtils.readFluidStack(buffer));
        }
        return new FluidIngredient(fluidStacks);
    }

    @Override
    public FluidIngredient parse(JsonObject json) {
        return new FluidIngredient(); // TODO NOPE, not now.
    }

    @Override
    public void write(PacketBuffer buffer, FluidIngredient ingredient) {
        List<FluidStack> fluidStacks = ingredient.getFluids();
        buffer.writeInt(fluidStacks.size());
        for (FluidStack fluidStack : fluidStacks) {
            ByteBufUtils.writeFluidStack(buffer, fluidStack);
        }
    }

}
