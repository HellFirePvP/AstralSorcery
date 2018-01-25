/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.item.base.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarBase
 * Created by HellFirePvP
 * Date: 16.10.2016 / 19:26
 */
public abstract class ContainerAltarBase extends Container {

    public final InventoryPlayer playerInv;
    public final TileAltar tileAltar;
    public final ItemStackHandler invHandler;
    private final int plSize;

    public ContainerAltarBase(InventoryPlayer playerInv, TileAltar tileAltar) {
        this.playerInv = playerInv;
        this.tileAltar = tileAltar;
        this.invHandler = tileAltar.getInventoryHandler();

        bindPlayerInventory();
        bindAltarInventory();

        this.plSize = playerInv.mainInventory.size();
    }

    abstract void bindPlayerInventory();

    abstract void bindAltarInventory();

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (this instanceof ContainerAltarTrait && index >= 0 && index < 36 &&
                    itemstack1.getItem() instanceof ItemConstellationFocus &&
                    ((ItemConstellationFocus) itemstack1.getItem()).getFocusConstellation(itemstack1) != null) {
                if (this.mergeItemStack(itemstack1, ((ContainerAltarTrait) this).focusSlot.slotNumber, ((ContainerAltarTrait) this).focusSlot.slotNumber + 1, false)) {
                    return itemstack;
                }
            }
            if (index >= 0 && index < 27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
