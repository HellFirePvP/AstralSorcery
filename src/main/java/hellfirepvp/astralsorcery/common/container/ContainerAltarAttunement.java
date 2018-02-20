/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarAttunement
 * Created by HellFirePvP
 * Date: 16.10.2016 / 17:18
 */
public class ContainerAltarAttunement extends ContainerAltarBase {

    public ContainerAltarAttunement(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    void bindAltarInventory() {
        for (int xx = 0; xx < 3; xx++) {
            addSlotToContainer(new SlotItemHandler(invHandler,     xx, 102 + xx * 18, 29));
            addSlotToContainer(new SlotItemHandler(invHandler, 3 + xx, 102 + xx * 18, 47));
            addSlotToContainer(new SlotItemHandler(invHandler, 6 + xx, 102 + xx * 18, 65));
        }
        addSlotToContainer(new SlotItemHandler(invHandler,  9, 84,  11));
        addSlotToContainer(new SlotItemHandler(invHandler, 10, 156, 11));
        addSlotToContainer(new SlotItemHandler(invHandler, 11, 84,  83));
        addSlotToContainer(new SlotItemHandler(invHandler, 12, 156, 83));
    }

    @Override
    void bindPlayerInventory() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(this.playerInv, j + i * 9 + 9, 48 + j * 18, 120 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(this.playerInv, i, 48 + i * 18, 178));
        }
    }

}
