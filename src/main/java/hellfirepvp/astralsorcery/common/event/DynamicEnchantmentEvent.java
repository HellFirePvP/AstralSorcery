/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicEnchantmentEvent
 * Created by HellFirePvP
 * Date: 11.08.2019 / 20:39
 */
public class DynamicEnchantmentEvent {

    //The event to ADD new dynamic enchantments
    @Cancelable
    public static class Add extends Event {

        private List<DynamicEnchantment> enchantmentsToApply = new LinkedList<>();
        private final ItemStack itemStack;
        private final PlayerEntity resolvedPlayer;

        public Add(ItemStack itemStack, @Nonnull PlayerEntity player) {
            this.itemStack = itemStack;
            this.resolvedPlayer = player;
        }

        public ItemStack getEnchantedItemStack() {
            return itemStack;
        }

        @Nonnull
        public PlayerEntity getResolvedPlayer() {
            return resolvedPlayer;
        }

        public List<DynamicEnchantment> getEnchantmentsToApply() {
            return enchantmentsToApply;
        }
    }

    //The event to MODIFY or REACT to previously defined/added dynamic enchantments + enchantments
    @Cancelable
    public static class Modify extends Event {

        private List<DynamicEnchantment> enchantmentsToApply;
        private final ItemStack itemStack;
        private PlayerEntity resolvedPlayer;

        public Modify(ItemStack itemStack, List<DynamicEnchantment> enchantmentsToApply, @Nonnull PlayerEntity resolvedPlayer) {
            this.itemStack = itemStack;
            this.enchantmentsToApply = enchantmentsToApply;
            this.resolvedPlayer = resolvedPlayer;
        }

        @Nonnull
        public PlayerEntity getResolvedPlayer() {
            return resolvedPlayer;
        }

        public ItemStack getEnchantedItemStack() {
            return itemStack;
        }

        public List<DynamicEnchantment> getEnchantmentsToApply() {
            return enchantmentsToApply;
        }
    }
}
