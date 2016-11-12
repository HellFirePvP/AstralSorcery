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

        addSlotToContainer(new Slot(tileAltar, 13, 102,  11));
        addSlotToContainer(new Slot(tileAltar, 14, 138,  11));

        addSlotToContainer(new Slot(tileAltar, 15,  84,  29));
        addSlotToContainer(new Slot(tileAltar, 16, 156,  29));

        addSlotToContainer(new Slot(tileAltar, 17, 84,   65));
        addSlotToContainer(new Slot(tileAltar, 18, 156,  65));

        addSlotToContainer(new Slot(tileAltar, 19, 102,  83));
        addSlotToContainer(new Slot(tileAltar, 20, 138,  83));
    }

}
