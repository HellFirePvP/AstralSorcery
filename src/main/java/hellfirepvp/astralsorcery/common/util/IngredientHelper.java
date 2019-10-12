/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IngredientHelper
 * Created by HellFirePvP
 * Date: 11.10.2019 / 22:18
 */
public class IngredientHelper {

    public static ItemStack getRandomMatchingStack(Ingredient ingredient, long tick) {
        if (ingredient.hasNoMatchingItems()) {
            return ItemStack.EMPTY;
        }
        ItemStack[] stacks = ingredient.getMatchingStacks();
        int mod = (int) ((tick / 20L) % stacks.length);
        return stacks[MathHelper.clamp(mod, 0, stacks.length - 1)];
    }

}
