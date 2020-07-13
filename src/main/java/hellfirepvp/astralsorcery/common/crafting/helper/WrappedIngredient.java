/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.util.IngredientHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

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
        return IngredientHelper.getRandomMatchingStack(this.getIngredient(), tick);
    }

}
