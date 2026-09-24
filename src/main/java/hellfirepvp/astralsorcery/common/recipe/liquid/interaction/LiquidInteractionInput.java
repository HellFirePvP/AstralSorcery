/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.liquid.interaction;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LiquidInteractionInput
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class LiquidInteractionInput extends CustomRecipeInput {

    private final FluidStack fluidA;
    private final FluidStack fluidB;

    public LiquidInteractionInput(FluidStack fluidA, FluidStack fluidB) {
        this.fluidA = fluidA;
        this.fluidB = fluidB;
    }

    public FluidStack getFluidA() {
        return this.fluidA;
    }

    public FluidStack getFluidB() {
        return this.fluidB;
    }

    @Override
    public boolean isEmpty() {
        return this.fluidA.isEmpty() && this.fluidB.isEmpty();
    }
}
