/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tree.perk.socket;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: GemSocketItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface GemSocketItem {

    /**
     * Called when an itemstack is inserted into the given socket perk.
     * Note: This will only occur if {@link #canInsert} ended up returning true.
     * Note: only called on {@link LogicalSide#SERVER}
     *
     * @param stack
     * @param perk
     * @param sPlayer
     * @param progress
     */
    default <T extends GemSocketPerk> void onInsert(ItemStack stack, T perk, ServerPlayer sPlayer, PlayerProgress progress) {}

    /**
     * Called when the itemstack is removed from the socket perk.
     * Note: only called on {@link LogicalSide#SERVER}
     *
     * @param stack
     * @param perk
     * @param sPlayer
     * @param progress
     */
    default <T extends GemSocketPerk> void onExtract(ItemStack stack, T perk, ServerPlayer sPlayer, PlayerProgress progress) {}

    /**
     * Test if this itemstack can be inserted into a gem socket.
     * Note: Gem items can always be taken off, regardless of what this returns.
     * Note: Called on both server and clientside.
     *
     * @param stack the gem stack
     * @param perk
     * @param player
     * @param progress
     * @param side
     * @return if the gem socket can be *inserted*
     */
    default <T extends GemSocketPerk> boolean canInsert(ItemStack stack, T perk, Player player, PlayerProgress progress, LogicalSide side) {
        return !this.getModifiers(stack, perk, player, side).isEmpty();
    }

    /**
     * Gets the set of modifiers this gem provides when socketed.
     * The modifiers themselves should never change for the given item.
     *
     * @param stack
     * @param perk
     * @param player
     * @param side
     * @return the list of modifiers
     */
    default <T extends GemSocketPerk> List<DynamicAttributeModifier> getModifiers(ItemStack stack, T perk, Player player, LogicalSide side) {
        return stack.getOrDefault(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY).modifiers();
    }

    /**
     * Add additional text to the perk's tooltip.
     *
     * @param stack
     * @param perk
     * @param tooltip
     */
    default <T extends GemSocketPerk> void addTooltip(ItemStack stack, T perk, List<MutableComponent> tooltip) {}
}
