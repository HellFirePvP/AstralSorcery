/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.slot;

import hellfirepvp.astralsorcery.common.container.ContainerTome;
import hellfirepvp.astralsorcery.common.item.ItemConstellationPaper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SlotConstellationPaper
 * Created by HellFirePvP
 * Date: 09.08.2019 / 21:33
 */
public class SlotConstellationPaper extends SlotItemHandler {

    private final ContainerTome listener;

    public SlotConstellationPaper(ContainerTome tome, IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.listener = tome;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemConstellationPaper && ((ItemConstellationPaper) stack.getItem()).getConstellation(stack) != null;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();

        listener.slotChanged();
    }
}
