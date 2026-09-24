/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.item.base.ItemCustom;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.util.ItemUtil;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkNullifierItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkNullifierItem extends ItemCustom {

    public PerkNullifierItem() {
        super(new Properties());
    }

    public static int getPerkNullifierCount(Player player) {
        return ItemUtil.findItemsInInventory(player, stack -> stack.is(ItemsAS.PERK_NULLIFIER)).values()
                .stream()
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    public static boolean consumePerkNullifier(Player player, boolean simulate) {
        Inventory playerInv = player.getInventory();
        for (int slot = 0; slot < playerInv.getContainerSize(); slot++) {
            ItemStack stack = playerInv.getItem(slot);
            if (stack.is(ItemsAS.PERK_NULLIFIER)) {
                if (!simulate) {
                    stack.shrink(1);
                    playerInv.setItem(slot, stack);
                }
                return true;
            }
        }
        return false;
    }
}
