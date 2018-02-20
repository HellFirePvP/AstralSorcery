/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FilteredSlot
 * Created by HellFirePvP
 * Date: 22.11.2016 / 14:43
 */
public class ConstellationPaperSlot extends SlotItemHandler {

    private final ContainerJournal listener;

    public ConstellationPaperSlot(IItemHandler handle, ContainerJournal containerJournal, int index, int xPosition, int yPosition) {
        super(handle, index, xPosition, yPosition);
        this.listener = containerJournal;
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemConstellationPaper && ItemConstellationPaper.getConstellation(stack) != null;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();

        listener.slotChanged();
    }
}
