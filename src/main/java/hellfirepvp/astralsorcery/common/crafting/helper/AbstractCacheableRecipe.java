/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractCacheableRecipe
 * Created by HellFirePvP
 * Date: 22.09.2016 / 16:01
 */
public abstract class AbstractCacheableRecipe extends AbstractRecipe {

    public AbstractCacheableRecipe(ItemStack output) {
        super(output);
    }

    public abstract IAccessibleRecipe make();

    @Nullable
    public abstract ItemHandle getExpectedStack(int row, int column);

    @Nullable
    public abstract ItemHandle getExpectedStack(ShapedRecipeSlot slot);

}
