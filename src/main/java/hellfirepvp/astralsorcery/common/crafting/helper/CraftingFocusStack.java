/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CraftingFocusStack
 * Created by HellFirePvP
 * Date: 25.09.2019 / 18:21
 */
public class CraftingFocusStack {

    private final int stackIndex;
    private final WrappedIngredient input;
    private final BlockPos at;

    public CraftingFocusStack(int stackIndex, WrappedIngredient input, BlockPos at) {
        this.stackIndex = stackIndex;
        this.input = input;
        this.at = at;
    }

    public CraftingFocusStack(CompoundNBT nbt) {
        this.stackIndex = nbt.getInt("stackIndex");
        this.input = WrappedIngredient.deserialize(nbt.getCompound("ingredient"));
        this.at = NBTHelper.readBlockPosFromNBT(nbt);
    }

    @Nullable
    public WrappedIngredient getInput() {
        return input;
    }

    public BlockPos getRealPosition() {
        return at;
    }

    public int getStackIndex() {
        return stackIndex;
    }

    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        NBTHelper.writeBlockPosToNBT(this.at, nbt);
        nbt.putInt("stackIndex", this.stackIndex);
        nbt.put("ingredient", this.input.serialize());
        return nbt;
    }

}
