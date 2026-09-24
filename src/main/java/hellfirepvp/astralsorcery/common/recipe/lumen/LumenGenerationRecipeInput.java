/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.lumen;

import hellfirepvp.astralsorcery.common.recipe.CustomRecipeInput;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenGenerationRecipeInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenGenerationRecipeInput extends CustomRecipeInput {

    private final ItemStack inputStack;

    private LumenGenerationRecipeInput(ItemStack inputStack) {
        this.inputStack = inputStack;
    }

    public static LumenGenerationRecipeInput create(ItemStack stack) {
        return new LumenGenerationRecipeInput(stack);
    }

    public ItemStack getInputStack() {
        return this.inputStack.copy();
    }

    @Override
    public boolean isEmpty() {
        return this.inputStack.isEmpty();
    }
}
