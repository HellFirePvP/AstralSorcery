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
 * Class: ContainerAltarAttenuation
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:18
 */
public class ContainerAltarAttenuation extends ContainerAltarBase {

    public ContainerAltarAttenuation(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    void bindAltarInventory() {
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new Slot(tileAltar,     xx, 141 + xx * 23, 63));
            addSlotToContainer(new Slot(tileAltar, 3 + xx, 141 + xx * 23, 86));
            addSlotToContainer(new Slot(tileAltar, 6 + xx, 141 + xx * 23, 108));
        }
        addSlotToContainer(new Slot(tileAltar,  9, 118,  40));
        addSlotToContainer(new Slot(tileAltar, 10, 210,  40));
        addSlotToContainer(new Slot(tileAltar, 11, 118, 131));
        addSlotToContainer(new Slot(tileAltar, 12, 210, 131));
    }

    @Override
    void bindPlayerInventory() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(this.playerInv, j + i * 9 + 9, 72 + j * 23, 176 + i * 23));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(this.playerInv, i, 72 + i * 23, 247));
        }
    }

}
