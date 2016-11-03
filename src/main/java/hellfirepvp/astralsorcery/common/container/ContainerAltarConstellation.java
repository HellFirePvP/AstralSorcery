package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarConstellation
 * Created by HellFirePvP
 * Date: 02.11.2016 / 14:42
 */
public class ContainerAltarConstellation extends ContainerAltarAttenuation {

    public ContainerAltarConstellation(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    void bindAltarInventory() {
        super.bindAltarInventory();

        addSlotToContainer(new Slot(tileAltar, 13, 141,  40));
        addSlotToContainer(new Slot(tileAltar, 14, 187,  40));

        addSlotToContainer(new Slot(tileAltar, 15, 118,  63));
        addSlotToContainer(new Slot(tileAltar, 16, 210,  63));

        addSlotToContainer(new Slot(tileAltar, 17, 118, 108));
        addSlotToContainer(new Slot(tileAltar, 18, 210, 108));

        addSlotToContainer(new Slot(tileAltar, 19, 141, 131));
        addSlotToContainer(new Slot(tileAltar, 20, 187, 131));
    }

}
