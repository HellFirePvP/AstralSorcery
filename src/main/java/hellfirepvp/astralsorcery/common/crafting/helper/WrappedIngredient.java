/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WrappedIngredient
 * Created by HellFirePvP
 * Date: 25.09.2019 / 18:19
 */
public class WrappedIngredient {

    private final Ingredient ingredient;

    public WrappedIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ItemStack getRandomMatchingStack(long tick) {
        ItemStack[] stacks = this.getIngredient().getMatchingStacks();
        int mod = (int) (tick % (stacks.length * 60));
        int index = MathHelper.clamp(stacks.length * (mod / (stacks.length * 60)), 0, stacks.length - 1);
        return stacks[index];
    }

}
