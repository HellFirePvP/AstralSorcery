/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.enchantment.CombinedEnchantmentModifiers;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DynamicEnchantmentEvent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DynamicEnchantmentEvent {

    public static class Add extends PlayerEvent {

        private final CombinedEnchantmentModifiers.Mutable newDynamicEnchantments;

        public Add(Player player) {
            super(player);
            this.newDynamicEnchantments = CombinedEnchantmentModifiers.of().mutable();
        }

        public CombinedEnchantmentModifiers.Mutable getDynamicEnchantments() {
            return this.newDynamicEnchantments;
        }
    }

    public static class Modify extends PlayerEvent {

        private final CombinedEnchantmentModifiers.Mutable dynamicEnchantments;

        public Modify(Player player, CombinedEnchantmentModifiers dynamicEnchantments) {
            super(player);
            this.dynamicEnchantments = dynamicEnchantments.mutable();
        }

        public CombinedEnchantmentModifiers.Mutable getDynamicEnchantments() {
            return this.dynamicEnchantments;
        }
    }
}
