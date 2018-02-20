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
 * Class: ContainerAltarTrait
 * Created by HellFirePvP
 * Date: 06.03.2017 / 14:24
 */
public class ContainerAltarTrait extends ContainerAltarConstellation {

    protected ConstellationFocusSlot focusSlot;

    public ContainerAltarTrait(InventoryPlayer playerInv, TileAltar tileAltar) {
        super(playerInv, tileAltar);
    }

    @Override
    void bindAltarInventory() {
        super.bindAltarInventory();

        addSlotToContainer(new SlotItemHandler(invHandler, 21,  120,  11)); //Up center
        addSlotToContainer(new SlotItemHandler(invHandler, 22,  84,  47)); //Left center
        addSlotToContainer(new SlotItemHandler(invHandler, 23, 156,  47)); //Right center
        addSlotToContainer(new SlotItemHandler(invHandler, 24, 120,  83)); //Lower center

        this.focusSlot = new ConstellationFocusSlot(invHandler, tileAltar, 35, 11);
        addSlotToContainer(this.focusSlot); //Focus item, not accessible from slot index.
    }
}
