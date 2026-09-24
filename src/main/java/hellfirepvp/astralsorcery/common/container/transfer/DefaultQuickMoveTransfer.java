/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.transfer;

import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DefaultQuickMoveTransfer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface DefaultQuickMoveTransfer {

    default AbstractContainerMenu self() {
        return MiscUtil.cast(this);
    }

    default ItemStack defaultQuickMoveStack(Player player, int index) {
        AbstractContainerMenu self = self();

        ItemStack stack = ItemStack.EMPTY;
        Slot slot = self.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack contained = slot.getItem();
            stack = contained.copy();

            Optional<ItemStack> customTransferResult = this.customTransfer(contained, index);
            if (customTransferResult.isPresent()) return ItemStack.EMPTY;

            if (!contained.isEmpty() && index >= 0 && index < 36) {
                if (this.defaultMoveItemStackTo(contained, 36, self.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (index >= 0 && index < 27) {
                if (!this.defaultMoveItemStackTo(contained, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.defaultMoveItemStackTo(contained, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.defaultMoveItemStackTo(contained, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (contained.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, contained);
        }
        return stack;
    }

    default Optional<ItemStack> customTransfer(ItemStack toMove, int transferIndex) {
        return Optional.empty();
    }

    default boolean defaultMoveItemStackTo(ItemStack toMove, int startIndex, int endIndex, boolean reverseDirection) {
        AbstractContainerMenu self = self();

        boolean transferred = false;
        int slotIndex = startIndex;
        if (reverseDirection) {
            slotIndex = endIndex - 1;
        }

        if (toMove.isStackable()) {
            while (!toMove.isEmpty() && (reverseDirection ? slotIndex >= startIndex : slotIndex < endIndex)) {
                Slot slot = self.slots.get(slotIndex);
                ItemStack contained = slot.getItem();
                if (this.canQuickTransferItemToSlot(toMove, slot, true)) {
                    int newTargetCount = contained.getCount() + toMove.getCount();
                    int slotMaxStackSize = slot.getMaxStackSize(contained);
                    if (newTargetCount <= slotMaxStackSize) {
                        toMove.setCount(0);
                        contained.setCount(newTargetCount);
                        slot.setChanged();
                        transferred = true;
                    } else if (contained.getCount() < slotMaxStackSize) {
                        toMove.shrink(slotMaxStackSize - contained.getCount());
                        contained.setCount(slotMaxStackSize);
                        slot.setChanged();
                        transferred = true;
                    }
                }

                if (reverseDirection) {
                    slotIndex--;
                } else {
                    slotIndex++;
                }
            }
        }

        if (!toMove.isEmpty()) {
            if (reverseDirection) {
                slotIndex = endIndex - 1;
            } else {
                slotIndex = startIndex;
            }

            while (reverseDirection ? slotIndex >= startIndex : slotIndex < endIndex) {
                Slot slot = self.slots.get(slotIndex);
                if (this.canQuickTransferItemToSlot(toMove, slot, false)) {
                    int slotMaxStackSize = slot.getMaxStackSize(toMove);
                    slot.setByPlayer(toMove.split(Math.min(toMove.getCount(), slotMaxStackSize)));
                    slot.setChanged();
                    transferred = true;
                    break;
                }

                if (reverseDirection) {
                    slotIndex--;
                } else {
                    slotIndex++;
                }
            }
        }

        return transferred;
    }

    default boolean canQuickTransferItemToSlot(ItemStack stack, Slot targetSlot, boolean onlyTransferToFilledSlots) {
        if (onlyTransferToFilledSlots) {
            return targetSlot.hasItem() &&
                    stack.isStackable() &&
                    ItemStack.isSameItemSameComponents(stack, targetSlot.getItem());
        }
        return (targetSlot.hasItem() && stack.isStackable() && ItemStack.isSameItemSameComponents(stack, targetSlot.getItem())) ||
                (!targetSlot.hasItem() && targetSlot.mayPlace(stack));
    }
}
