package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FilteredSlot
 * Created by HellFirePvP
 * Date: 22.11.2016 / 14:43
 */
public class ConstellationPaperSlot extends Slot {

    public ConstellationPaperSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return stack != null && stack.getItem() != null && stack.getItem() instanceof ItemConstellationPaper && ItemConstellationPaper.getConstellation(stack) != null;
    }

}
