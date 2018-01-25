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
import net.minecraftforge.items.SlotItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarConstellation
 * Created by HellFirePvP
 * Date: 02.11.2016 / 14:42
 */
public class ContainerAltarConstellation extends ContainerAltarAttunement {

    public ContainerAltarConstellation(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    void bindAltarInventory() {
        super.bindAltarInventory();

        addSlotToContainer(new SlotItemHandler(invHandler, 13, 102,  11));
        addSlotToContainer(new SlotItemHandler(invHandler, 14, 138,  11));

        addSlotToContainer(new SlotItemHandler(invHandler, 15,  84,  29));
        addSlotToContainer(new SlotItemHandler(invHandler, 16, 156,  29));

        addSlotToContainer(new SlotItemHandler(invHandler, 17, 84,   65));
        addSlotToContainer(new SlotItemHandler(invHandler, 18, 156,  65));

        addSlotToContainer(new SlotItemHandler(invHandler, 19, 102,  83));
        addSlotToContainer(new SlotItemHandler(invHandler, 20, 138,  83));
    }

}
