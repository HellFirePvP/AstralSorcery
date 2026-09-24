/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.lightwell;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightwellRecipeInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightwellRecipeInput extends CustomRecipeInput {

    private final ItemStack itemInput;
    private final FluidStack existingFluidOutput;

    private LightwellRecipeInput(ItemStack itemInput, FluidStack existingFluidOutput) {
        this.itemInput = itemInput;
        this.existingFluidOutput = existingFluidOutput;
    }

    public static LightwellRecipeInput of(ItemStack itemInput) {
        return new LightwellRecipeInput(itemInput, FluidStack.EMPTY);
    }

    public static LightwellRecipeInput of(ItemStack itemInput, FluidStack existingFluidOutput) {
        return new LightwellRecipeInput(itemInput, existingFluidOutput);
    }

    public ItemStack getItemInput() {
        return this.itemInput;
    }

    public FluidStack getExistingFluidOutput() {
        return this.existingFluidOutput;
    }

    @Override
    public boolean isEmpty() {
        return this.itemInput.isEmpty();
    }
}
