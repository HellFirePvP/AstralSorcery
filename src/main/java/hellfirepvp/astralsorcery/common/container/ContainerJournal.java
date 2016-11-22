package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.item.ItemJournal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerJournal
 * Created by HellFirePvP
 * Date: 22.11.2016 / 14:33
 */
public class ContainerJournal extends Container {

    private final ItemStack parentJournal;

    public ContainerJournal(InventoryPlayer playerInv, ItemStack journal) {
        this.parentJournal = journal;
        IInventory inv = ItemJournal.getJournalStorage(journal);
        buildPlayerSlots(playerInv);
        buildSlots(inv);
    }

    private void buildPlayerSlots(InventoryPlayer playerInv) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(playerInv, i, 8 + i * 18, 142));
        }
    }

    private void buildSlots(IInventory in) {
        for (int xx = 0; xx < 9; xx++) {
            addSlotToContainer(new ConstellationPaperSlot(in,      xx, 8 + xx * 18, 13));
            addSlotToContainer(new ConstellationPaperSlot(in, 9 +  xx, 8 + xx * 18, 31));
            addSlotToContainer(new ConstellationPaperSlot(in, 18 + xx, 8 + xx * 18, 49));
        }
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index >= 0 && index < 27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return null;
                }
            } else if (index >= 27 && index < 36) {
                if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if(!playerIn.getEntityWorld().isRemote) {
            LinkedList<IConstellation> saveConstellations = new LinkedList<>();
            for (int i = 36; i < 63; i++) {
                ItemStack in = inventorySlots.get(i).getStack();
                if(in == null || in.getItem() == null) continue;
                IConstellation c = ItemConstellationPaper.getConstellation(in);
                if(c != null) {
                    saveConstellations.add(c);
                }
            }
            ItemJournal.setStoredConstellations(parentJournal, saveConstellations);
        }
    }

}
