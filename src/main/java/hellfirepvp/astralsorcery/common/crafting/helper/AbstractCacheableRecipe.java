package hellfirepvp.astralsorcery.common.crafting.helper;

import hellfirepvp.astralsorcery.common.crafting.IAccessibleRecipe;
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
    public abstract ItemStack getExpectedStack(int row, int column);

    @Nullable
    public abstract ItemStack getExpectedStack(ShapedRecipeSlot slot);

}
