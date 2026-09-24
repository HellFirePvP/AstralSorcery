/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.infusion;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InfusionRecipeInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InfusionRecipeInput extends CustomRecipeInput {

    private final Map<BlockPos, FluidState> fluidInputs = new HashMap<>();
    private final Level level;
    private final ItemStack itemInput;

    private InfusionRecipeInput(ItemStack itemInput, Level level, Map<BlockPos, FluidState> fluidInputs) {
        this.itemInput = itemInput;
        this.level = level;
        this.fluidInputs.putAll(fluidInputs);
    }

    public static InfusionRecipeInput of(ItemStack itemInput, Level level, Map<BlockPos, FluidState> fluidInputs) {
        return new InfusionRecipeInput(itemInput, level, fluidInputs);
    }

    public Map<BlockPos, FluidState> getFluidInputs() {
        return Collections.unmodifiableMap(this.fluidInputs);
    }

    public Level getLevel() {
        return this.level;
    }

    public ItemStack getItemInput() {
        return this.itemInput.copy();
    }

    @Override
    public boolean isEmpty() {
        return this.itemInput.isEmpty();
    }
}
