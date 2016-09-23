package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarDiscovery
 * Created by HellFirePvP
 * Date: 21.09.2016 / 14:06
 */
public class ContainerAltarDiscovery extends Container {

    public final InventoryPlayer playerInv;
    public final TileAltar tileAltar;

    public ContainerAltarDiscovery(InventoryPlayer playerInv, TileAltar tileAltar) {
        this.playerInv = playerInv;
        this.tileAltar = tileAltar;

        bindPlayerInventory();
        bindAltarInventory();
    }

    protected void bindAltarInventory() {
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new Slot(tileAltar,     xx, 87 + xx * 22, 15));
            addSlotToContainer(new Slot(tileAltar, 3 + xx, 87 + xx * 22, 36));
            addSlotToContainer(new Slot(tileAltar, 6 + xx, 87 + xx * 22, 58));
        }
    }

    protected void bindPlayerInventory() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(this.playerInv, j + i * 9 + 9, 21 + j * 22, 92 + i * 22));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(this.playerInv, i, 21 + i * 22, 161));
        }
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return null;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

}
