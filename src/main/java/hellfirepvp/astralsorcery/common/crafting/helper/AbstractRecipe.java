package hellfirepvp.astralsorcery.common.crafting.helper;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRecipe
 * Created by HellFirePvP
 * Date: 10.08.2016 / 15:21
 */
public abstract class AbstractRecipe {

    private ItemStack output;

    public AbstractRecipe(ItemStack output) {
        this.output = output;
    }

    public ItemStack getOutput() {
        return output;
    }

    public abstract void register();

}
