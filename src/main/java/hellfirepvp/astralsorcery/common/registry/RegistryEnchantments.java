/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentNightVision;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.NIGHT_VISION;
import static hellfirepvp.astralsorcery.common.lib.EnchantmentsAS.SCORCHING_HEAT;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryEnchantments
 * Created by HellFirePvP
 * Date: 02.05.2020 / 12:43
 */
public class RegistryEnchantments {

    private RegistryEnchantments() {}

    /**
     * @see hellfirepvp.astralsorcery.common.loot.global.LootModifierScorchingHeat
     */
    public static void init() {
        NIGHT_VISION = register(new EnchantmentNightVision())
                .setRegistryName(AstralSorcery.key("night_vision"));
        SCORCHING_HEAT = register(new Enchantment(Enchantment.Rarity.VERY_RARE, EnchantmentType.DIGGER, new EquipmentSlotType[] { EquipmentSlotType.MAINHAND }) {})
                .setRegistryName(AstralSorcery.key("scorching_heat"));
    }

    private static <T extends Enchantment> T register(T effect) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effect);
        return effect;
    }

}
