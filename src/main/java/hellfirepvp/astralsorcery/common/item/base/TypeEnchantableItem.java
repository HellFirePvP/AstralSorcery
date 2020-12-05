package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TypeEnchantableItem
 * Created by HellFirePvP
 * Date: 05.12.2020 / 17:47
 */
public interface TypeEnchantableItem {

    boolean canEnchantItem(ItemStack stack, EnchantmentType type);

}