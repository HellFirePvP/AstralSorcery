/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRecipeAccessor
 * Created by HellFirePvP
 * Date: 18.06.2017 / 16:17
 */
public abstract class AbstractRecipeAccessor extends AbstractRecipeData {

    public AbstractRecipeAccessor(@Nonnull ItemStack output) {
        super(output);
    }

    @Nullable
    abstract ItemHandle getExpectedStack(int row, int column);

    @Nullable
    abstract ItemHandle getExpectedStack(ShapedRecipeSlot slot);

    public static AbstractRecipeAccessor buildAccessorFor(IRecipe nativeRecipe) {
        return new AbstractRecipeAccessor(nativeRecipe.getRecipeOutput()) {
            @Nullable
            @Override
            ItemHandle getExpectedStack(int row, int column) {
                int index = row * 3 + column;
                if(index >= nativeRecipe.getIngredients().size()) {
                    return null;
                }
                return ItemHandle.of(nativeRecipe.getIngredients().get(index));
            }

            @Nullable
            @Override
            ItemHandle getExpectedStack(ShapedRecipeSlot slot) {
                int index = slot.getSlotID();
                if(index >= nativeRecipe.getIngredients().size()) {
                    return null;
                }
                return ItemHandle.of(nativeRecipe.getIngredients().get(index));
            }
        };
    }

}
