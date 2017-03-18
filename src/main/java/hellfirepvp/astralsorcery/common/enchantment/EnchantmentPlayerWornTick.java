package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentPlayerWornTick
 * Created by HellFirePvP
 * Date: 15.03.2017 / 18:01
 */
public abstract class EnchantmentPlayerWornTick extends Enchantment {

    public EnchantmentPlayerWornTick(String name, Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot... slots) {
        super(rarityIn, typeIn, slots);
        setName(name);
    }

    public void onWornTick(boolean isClient, EntityPlayer base, int level) {}

}
