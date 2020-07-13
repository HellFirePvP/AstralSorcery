/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tile;

import hellfirepvp.astralsorcery.common.tile.base.TileEntitySynchronized;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileInventoryFiltered
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:32
 */
public class TileInventoryFiltered extends TileInventory {

    private InputFilter inputFilter = null;
    private ExtractFilter extractFilter = null;

    public TileInventoryFiltered(@Nonnull TileEntitySynchronized tile,
                                 @Nonnull Supplier<Integer> slotCountProvider,
                                 Direction... applicableSides) {
        super(tile, slotCountProvider, applicableSides);
    }

    public TileInventoryFiltered(@Nonnull TileEntitySynchronized tile,
                                 @Nonnull Supplier<Integer> slotCountProvider,
                                 @Nullable Consumer<Integer> changeListener,
                                 Direction... applicableSides) {
        super(tile, slotCountProvider, changeListener, applicableSides);
    }

    protected TileInventoryFiltered(@Nonnull TileEntitySynchronized tile,
                                 @Nonnull Supplier<Integer> slotCountProvider,
                                 @Nullable Consumer<Integer> changeListener,
                                 @Nonnull Collection<Direction> applicableSides) {
        super(tile, slotCountProvider, changeListener, applicableSides);
    }

    public TileInventoryFiltered canInsert(InputFilter filter) {
        this.inputFilter = filter;
        return this;
    }

    public TileInventoryFiltered canExtract(ExtractFilter filter) {
        this.extractFilter = filter;
        return this;
    }

    @Override
    protected TileInventoryFiltered makeNewInstance() {
        TileInventoryFiltered inv = new TileInventoryFiltered(this.tile, this.slotCountProvider,
                this.changeListener, MiscUtils.copySet(this.applicableSides));
        inv.canInsert(this.inputFilter);
        inv.canExtract(this.extractFilter);
        return inv;
    }

    @Nonnull
    @Override
    public TileInventoryFiltered deserialize(CompoundNBT tag) {
        return (TileInventoryFiltered) super.deserialize(tag);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!canInsertItem(slot, stack, getStackInSlot(slot))) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!canExtractItem(slot, amount, getStackInSlot(slot))) {
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }

    public boolean canInsertItem(int slot, ItemStack toAdd) {
        return this.canInsertItem(slot, toAdd, this.getStackInSlot(slot));
    }

    private boolean canInsertItem(int slot, ItemStack toAdd, @Nonnull ItemStack existing) {
        return inputFilter == null || inputFilter.canInsert(slot, toAdd, existing);
    }

    public boolean canExtractItem(int slot, int amount) {
        return this.canExtractItem(slot, amount, this.getStackInSlot(slot));
    }

    private boolean canExtractItem(int slot, int amount, @Nonnull ItemStack existing) {
        return extractFilter == null || extractFilter.canExtract(slot, amount, existing);
    }

    public static interface InputFilter {

        public boolean canInsert(int slot, ItemStack toAdd, @Nonnull ItemStack existing);

    }


    public static interface ExtractFilter {

        public boolean canExtract(int slot, int amount, @Nonnull ItemStack existing);

    }

}
