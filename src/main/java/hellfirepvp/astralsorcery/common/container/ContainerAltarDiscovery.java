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
public class ContainerAltarDiscovery extends ContainerAltarBase {

    public ContainerAltarDiscovery(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    void bindAltarInventory() {
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new Slot(tileAltar,     xx, 62 + xx * 18, 11));
            addSlotToContainer(new Slot(tileAltar, 3 + xx, 62 + xx * 18, 29));
            addSlotToContainer(new Slot(tileAltar, 6 + xx, 62 + xx * 18, 47));
        }
    }

    @Override
    void bindPlayerInventory() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(this.playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(this.playerInv, i, 8 + i * 18, 142));
        }
    }

}
