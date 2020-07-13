/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.nojson.freezing;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.block.Blocks;
import net.minecraftforge.fluids.FluidAttributes;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FluidFreezingRecipe
 * Created by HellFirePvP
 * Date: 30.11.2019 / 21:09
 */
public class FluidFreezingRecipe extends BlockFreezingRecipe {

    public FluidFreezingRecipe() {
        super(AstralSorcery.key("all_fluids_freezing"),
                (world, pos, state) -> state.getFluidState().isSource() &&
                        state.getFluidState().getBlockState().equals(state),
                (worldPos, state) -> {
                    FluidAttributes fAttr = state.getFluidState().getFluid().getAttributes();
                    if (fAttr.getTemperature(worldPos.getWorld(), worldPos) <= 300) {
                        return Blocks.ICE.getDefaultState();
                    } else if (fAttr.getTemperature(worldPos.getWorld(), worldPos) >= 500) {
                        return Blocks.OBSIDIAN.getDefaultState();
                    }
                    return state;
                });
    }
}
