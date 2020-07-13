/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.container.slot.SlotConstellationFocus;
import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.lib.ContainerTypesAS;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import hellfirepvp.astralsorcery.common.util.tile.TileInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ContainerAltarTrait
 * Created by HellFirePvP
 * Date: 15.08.2019 / 16:07
 */
public class ContainerAltarTrait extends ContainerAltarBase {

    private SlotConstellationFocus focusSlot;

    public ContainerAltarTrait(TileAltar altar, PlayerInventory inv, int windowId) {
        super(altar, ContainerTypesAS.ALTAR_RADIANCE, inv, windowId);
    }

    @Override
    void bindPlayerInventory(PlayerInventory plInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(plInventory, j + i * 9 + 9, 48 + j * 18, 120 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(plInventory, i, 48 + i * 18, 178));
        }
    }

    @Override
    void bindAltarInventory(TileInventory altarInventory) {
        for (int yy = 0; yy < 5; yy++) {
            for (int xx = 0; xx < 5; xx++) {
                addSlot(new SlotItemHandler(altarInventory, yy * 5 + xx, 84 + xx * 18, 11 + yy * 18));
            }
        }

        this.focusSlot = new SlotConstellationFocus(altarInventory, this.getTileEntity(), 35, 11);
        addSlot(this.focusSlot); //Focus item, not accessible from slot index.
    }

    @Override
    Optional<ItemStack> handleCustomTransfer(PlayerEntity player, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index < 36 &&
                    slotStack.getItem() instanceof IConstellationFocus &&
                    ((IConstellationFocus) slotStack.getItem()).getFocusConstellation(slotStack) != null) {
                if (this.mergeItemStack(slotStack, this.focusSlot.slotNumber, this.focusSlot.slotNumber + 1, false)) {
                    return Optional.of(slotStack);
                }
            }
        }
        return Optional.empty();
    }
}
