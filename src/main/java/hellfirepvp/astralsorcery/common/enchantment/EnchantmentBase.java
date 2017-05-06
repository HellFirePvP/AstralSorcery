package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentBase
 * Created by HellFirePvP
 * Date: 06.05.2017 / 10:46
 */
public class EnchantmentBase extends Enchantment {

    protected EnchantmentBase(String unlocName, Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
        setName(unlocName);
    }

}
