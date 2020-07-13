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
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileInventory
 * Created by HellFirePvP
 * Date: 30.06.2019 / 21:20
 */
public class TileInventory extends ItemStackHandler implements Iterable<ItemStack> {

    protected final TileEntitySynchronized tile;
    protected final Consumer<Integer> changeListener;
    protected final Supplier<Integer> slotCountProvider;
    protected Set<Direction> applicableSides = new HashSet<>();

    public TileInventory(@Nonnull TileEntitySynchronized tile,
                         @Nonnull Supplier<Integer> slotCountProvider,
                         Direction... applicableSides) {
        this(tile, slotCountProvider, null, applicableSides);
    }

    public TileInventory(@Nonnull TileEntitySynchronized tile,
                         @Nonnull Supplier<Integer> slotCountProvider,
                         @Nullable Consumer<Integer> changeListener,
                         Direction... applicableSides) {
        this(tile, slotCountProvider, changeListener, Arrays.asList(applicableSides));
    }

    protected TileInventory(@Nonnull TileEntitySynchronized tile,
                            @Nonnull Supplier<Integer> slotCountProvider,
                            @Nullable Consumer<Integer> changeListener,
                            @Nonnull Collection<Direction> applicableSides) {
        super(slotCountProvider.get());
        this.tile = tile;
        this.changeListener = changeListener;
        this.slotCountProvider = slotCountProvider;
        this.applicableSides.addAll(applicableSides);
    }

    protected TileInventory makeNewInstance() {
        return new TileInventory(this.tile, this.slotCountProvider, this.changeListener, MiscUtils.copySet(this.applicableSides));
    }

    @Nonnull
    public TileInventory deserialize(CompoundNBT tag) {
        this.deserializeNBT(tag);
        if (this.getSlots() != this.slotCountProvider.get()) {
            TileInventory newInv = makeNewInstance();
            for (int i = 0; i < Math.min(this.getSlots(), newInv.getSlots()); i++) {
                ItemStack old = this.getStackInSlot(i);
                old = ItemUtils.copyStackWithSize(old, old.getCount());
                newInv.setStackInSlot(i, old);
            }
            return newInv;
        }
        return this;
    }

    @Nonnull
    public CompoundNBT serialize() {
        return this.serializeNBT();
    }

    private boolean hasHandlerForSide(@Nullable Direction facing) {
        return facing == null || applicableSides.contains(facing);
    }

    public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
        return hasHandlerForSide(facing) && CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == capability;
    }

    public LazyOptional<TileInventory> getCapability() {
        return LazyOptional.of(() -> this);
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        if (changeListener != null) {
            changeListener.accept(slot);
        }
        tile.markForUpdate();
    }

    public void clearInventory() {
        for (int i = 0; i < getSlots(); i++) {
            setStackInSlot(i, ItemStack.EMPTY);
            onContentsChanged(i);
        }
    }

    @Override
    @Nonnull
    public Iterator<ItemStack> iterator() {
        return this.stacks.iterator();
    }
}
