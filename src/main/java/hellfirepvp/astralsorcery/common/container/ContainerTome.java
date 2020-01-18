/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBaseItem;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.container.slot.SlotConstellationPaper;
import hellfirepvp.astralsorcery.common.container.slot.SlotUnclickable;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import hellfirepvp.astralsorcery.common.item.ItemTome;
import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerTome
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:14
 */
public class ContainerTome extends Container {

    private final ItemStack parentTome;
    private final int tomeIndex;

    public ContainerTome(int id, PlayerInventory plInventory, ItemStack tome, int tomeIndex) {
        super(ContainerTypesAS.TOME, id);
        this.parentTome = tome;
        this.tomeIndex = tomeIndex;
        buildPlayerSlots(plInventory);
        buildSlots(new InvWrapper(ItemTome.getTomeStorage(tome)));
    }

    private void buildPlayerSlots(PlayerInventory playerInv) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int index = j + i * 9 + 9;

                if (index == tomeIndex) {
                    addSlot(new SlotUnclickable(playerInv, index, 8 + j * 18, 84 + i * 18));
                } else {
                    addSlot(           new Slot(playerInv, index, 8 + j * 18, 84 + i * 18));
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            if (i == tomeIndex) {
                addSlot(new SlotUnclickable(playerInv, i, 8 + i * 18, 142));
            } else {
                addSlot(           new Slot(playerInv, i, 8 + i * 18, 142));
            }
        }
    }

    private void buildSlots(IItemHandler handle) {
        for (int i = 0; i < 3; i++) {
            for (int xx = 0; xx < 9; xx++) {
                addSlot(new SlotConstellationPaper(this, handle, (i * 9) + xx,8 + xx * 18, 13 + (i * 18)));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (!itemstack1.isEmpty() && itemstack1.getItem() instanceof ItemConstellationPaper && ((ItemConstellationPaper) itemstack1.getItem()).getConstellation(itemstack1) != null) {
                if (index >= 0 && index < 36) {
                    if (!this.mergeItemStack(itemstack1, 36, 63, false)) {
                        return ItemStack.EMPTY;
                    }
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
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public void slotChanged() {
        if (EffectiveSide.get().isServer()) {
            LinkedList<IConstellation> saveConstellations = new LinkedList<>();
            for (int i = 36; i < 63; i++) {
                ItemStack in = inventorySlots.get(i).getStack();
                if (in.isEmpty()) {
                    continue;
                }
                if (!(in.getItem() instanceof ConstellationBaseItem)) {
                    continue;
                }
                IConstellation c = ((ConstellationBaseItem) in.getItem()).getConstellation(in);
                if (c != null) {
                    saveConstellations.add(c);
                }
            }
            ItemTome.setStoredConstellations(parentTome, saveConstellations);
        }
    }
}
