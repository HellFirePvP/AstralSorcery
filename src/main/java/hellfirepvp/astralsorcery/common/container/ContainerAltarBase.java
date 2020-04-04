/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarBase
 * Created by HellFirePvP
 * Date: 15.08.2019 / 15:54
 */
public abstract class ContainerAltarBase extends ContainerTileEntity<TileAltar> {

    private final PlayerInventory playerInv;
    private final TileInventory invHandler;

    protected ContainerAltarBase(TileAltar altar, @Nullable ContainerType<?> type, PlayerInventory inv, int windowId) {
        super(altar, type, windowId);
        this.playerInv = inv;
        this.invHandler = altar.getInventory();

        bindPlayerInventory(this.playerInv);
        bindAltarInventory(this.invHandler);
    }

    abstract void bindPlayerInventory(PlayerInventory plInventory);

    abstract void bindAltarInventory(TileInventory altarInventory);

    abstract Optional<ItemStack> handleCustomTransfer(PlayerEntity player, int index);

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            itemstack = slotStack.copy();

            Optional<ItemStack> stackOpt = this.handleCustomTransfer(playerIn, index);
            if (stackOpt.isPresent()) {
                return stackOpt.get();
            }

            if (index >= 0 && index < 27) {
                if (!this.mergeItemStack(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.mergeItemStack(slotStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity player) {
        BlockPos pos = this.getTileEntity().getPos();
        if (this.getTileEntity().getWorld().getTileEntity(pos) != this.getTileEntity()) {
            return false;
        } else {
            return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
        }
    }
}
