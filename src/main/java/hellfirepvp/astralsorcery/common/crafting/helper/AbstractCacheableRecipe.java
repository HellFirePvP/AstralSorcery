package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

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

    public abstract IRecipe make();

}
