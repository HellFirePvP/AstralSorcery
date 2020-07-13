/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container.slot;

import hellfirepvp.astralsorcery.common.item.base.IConstellationFocus;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SlotConstellationFocus
 * Created by HellFirePvP
 * Date: 15.08.2019 / 16:13
 */
public class SlotConstellationFocus extends SlotItemHandler {

    private final TileAltar altar;

    public SlotConstellationFocus(IItemHandler itemHandler, TileAltar altar, int xPosition, int yPosition) {
        super(itemHandler, 100, xPosition, yPosition);
        this.altar = altar;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof IConstellationFocus && ((IConstellationFocus) stack.getItem()).getFocusConstellation(stack) != null;
    }

    @Override
    public ItemStack getStack() {
        return this.altar.getFocusItem();
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        this.altar.setFocusItem(stack);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return true;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        this.altar.markForUpdate();
        return super.onTake(thePlayer, stack);
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack focus = this.altar.getFocusItem();
        this.altar.setFocusItem(ItemStack.EMPTY);
        return focus;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

}
