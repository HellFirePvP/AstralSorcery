package hellfirepvp.astralsorcery.common.crafting;

import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IAccessibleRecipe
 * Created by HellFirePvP
 * Date: 06.10.2016 / 14:18
 */
public interface IAccessibleRecipe extends IRecipe {

    @Nullable
    public ItemStack getExpectedStack(int row, int column);

    @Nullable
    public ItemStack getExpectedStack(ShapedRecipeSlot slot);

}
