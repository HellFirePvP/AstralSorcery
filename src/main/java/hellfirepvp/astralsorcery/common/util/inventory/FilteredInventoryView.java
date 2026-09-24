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

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FilteredInventoryView
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FilteredInventoryView extends InventoryView {

    private InputFilter inputFilter;
    private ExtractFilter extractFilter;

    protected FilteredInventoryView(int size,
                                    InventoryStackList contents,
                                    Set<Direction> applicableSides,
                                    Consumer<Integer> changeListener,
                                    BiFunction<Integer, ItemStack, Integer> stackSizeLimiter,
                                    InputFilter inputFilter,
                                    ExtractFilter extractFilter) {
        super(size, contents, applicableSides, changeListener, stackSizeLimiter);
        this.inputFilter = inputFilter;
        this.extractFilter = extractFilter;
    }

    public void bypassFilters(Consumer<FilteredInventoryView> run) {
        InputFilter in = this.inputFilter;
        ExtractFilter ex = this.extractFilter;
        this.inputFilter = InputFilter.NO_FILTER;
        this.extractFilter = ExtractFilter.NO_FILTER;
        try {
            run.accept(this);
        } finally {
            this.inputFilter = in;
            this.extractFilter = ex;
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!this.canInsertItem(slot, stack, this.getStackInSlot(slot))) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!this.canExtractItem(slot, amount, this.getStackInSlot(slot))) {
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }

    public boolean canInsertItem(int slot, ItemStack toAdd) {
        return this.canInsertItem(slot, toAdd, this.getStackInSlot(slot));
    }

    private boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
        return this.inputFilter == null || this.inputFilter.canInsert(slot, toAdd, existing);
    }

    public boolean canExtractItem(int slot, int amount) {
        return this.canExtractItem(slot, amount, this.getStackInSlot(slot));
    }

    private boolean canExtractItem(int slot, int amount, @Nonnull ItemStack existing) {
        return this.extractFilter == null || this.extractFilter.canExtract(slot, amount, existing);
    }

    @FunctionalInterface
    public interface InputFilter {

        InputFilter NO_FILTER = (slot, toAdd, existing) -> true;

        boolean canInsert(int slot, ItemStack toAdd, @Nonnull ItemStack existing);

        default InputFilter and(InputFilter other) {
            return (slot, toAdd, existing) ->
                    other.canInsert(slot, toAdd, existing) && this.canInsert(slot, toAdd, existing);
        }
    }

    @FunctionalInterface
    public interface ExtractFilter {

        ExtractFilter NO_FILTER = (slot, amount, existing) -> true;

        boolean canExtract(int slot, int amount, @Nonnull ItemStack existing);

        default ExtractFilter and(ExtractFilter other) {
            return (slot, amount, existing) ->
                    other.canExtract(slot, amount, existing) && this.canExtract(slot, amount, existing);
        }
    }
}
