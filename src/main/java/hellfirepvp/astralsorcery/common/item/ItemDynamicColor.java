package hellfirepvp.astralsorcery.common.item;

import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemDynamicColor
 * Created by HellFirePvP
 * Date: 06.12.2016 / 14:26
 */
public interface ItemDynamicColor {

    public int getColorForItemStack(ItemStack stack, int tintIndex);

}
