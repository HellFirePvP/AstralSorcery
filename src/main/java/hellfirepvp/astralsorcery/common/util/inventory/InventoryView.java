/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: InventoryView
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class InventoryView implements IItemHandlerModifiable, Iterable<ItemStack> {

    private final int size;
    private final InventoryStackList contents;
    private final Set<Direction> applicableSides;
    private final Consumer<Integer> changeListener;
    private final BiFunction<Integer, ItemStack, Integer> stackSizeLimiter;

    protected InventoryView(int size,
                            InventoryStackList contents,
                            Set<Direction> applicableSides,
                            Consumer<Integer> changeListener,
                            BiFunction<Integer, ItemStack, Integer> stackSizeLimiter) {
        this.size = size;
        this.contents = contents;
        this.applicableSides = applicableSides;
        this.changeListener = changeListener;
        this.stackSizeLimiter = stackSizeLimiter;
    }

    public int getSize() {
        return this.size;
    }

    @Override
    @Nonnull
    public Iterator<ItemStack> iterator() {
        return this.contents.iterator();
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.contents.setStackInSlot(slot, stack);
        this.onContentsChanged(slot);
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.contents.getStackInSlot(slot);
    }

    @Override
    public int getSlots() {
        return this.getSize();
    }

    protected void validateViewSize(int slot) {
        if (slot >= this.getSize()) {
            throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range - [0, " + this.getSize() + ")");
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        int insertable = this.stackSizeLimiter.apply(slot, stack);
        int leftOver = stack.getCount() - insertable;
        ItemStack toInsert = stack.copyWithCount(insertable);
        ItemStack notInserted = this.internalInsertItem(slot, toInsert, simulate);
        return stack.copyWithCount(leftOver + notInserted.getCount());
    }

    protected ItemStack internalInsertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!this.isItemValid(slot, stack)) return stack;
        this.validateViewSize(slot);

        ItemStack existing = this.getStackInSlot(slot);
        int transferLimit = Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing)) return stack;
            transferLimit -= existing.getCount();
        }

        if (transferLimit <= 0) return stack;

        boolean reachedLimit = stack.getCount() > transferLimit;
        if (!simulate) {
            if (existing.isEmpty()) {
                ItemStack insertedStack = reachedLimit ? stack.copyWithCount(transferLimit) : stack;
                this.setStackInSlot(slot, insertedStack);
            } else {
                existing.grow(reachedLimit ? transferLimit : stack.getCount());
            }
            this.onContentsChanged(slot);
        }

        return reachedLimit ? stack.copyWithCount(stack.getCount() - transferLimit) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return ItemStack.EMPTY;
        this.validateViewSize(slot);

        ItemStack existing = this.getStackInSlot(slot);
        if (existing.isEmpty()) return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.setStackInSlot(slot, ItemStack.EMPTY);
                this.onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.setStackInSlot(slot, existing.copyWithCount(existing.getCount() - toExtract));
                this.onContentsChanged(slot);
            }
            return existing.copyWithCount(toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.stackSizeLimiter.apply(slot, this.getStackInSlot(slot));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    public void clearInventory() {
        for (int i = 0; i < getSlots(); i++) {
            this.setStackInSlot(i, ItemStack.EMPTY);
            this.onContentsChanged(i);
        }
    }

    protected void onContentsChanged(int slot) {
        this.changeListener.accept(slot);
    }

    protected boolean hasHandlerForSide(@Nullable Direction facing) {
        return facing == null || this.applicableSides.contains(facing);
    }

    @Nullable
    public InventoryView getInventoryAccess(@Nullable Direction direction) {
        if (!this.hasHandlerForSide(direction)) {
            return null;
        }
        return this;
    }
}
