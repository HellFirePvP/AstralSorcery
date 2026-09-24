/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.data.EnchantmentRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentsAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnchantmentsAS {

    public static final EnchantmentRegistryObject SCORCHING_HEAT = key("scorching_heat");

    private static EnchantmentRegistryObject key(String name) {
        return new EnchantmentRegistryObject(ResourceKey.create(Registries.ENCHANTMENT, AstralSorcery.key(name)));
    }
}
