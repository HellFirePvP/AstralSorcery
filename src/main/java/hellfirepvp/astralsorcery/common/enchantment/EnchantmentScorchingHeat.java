/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EnchantmentScorchingHeat
 * Created by HellFirePvP
 * Date: 07.03.2021 / 13:01
 */
public class EnchantmentScorchingHeat extends Enchantment {

    public EnchantmentScorchingHeat() {
        super(Enchantment.Rarity.VERY_RARE, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND });
    }

    @Override
    protected boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && ench != Enchantments.SILK_TOUCH;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return false;
    }
}
