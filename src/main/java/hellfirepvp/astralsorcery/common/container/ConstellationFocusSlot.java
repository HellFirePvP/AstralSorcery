/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.container;

import hellfirepvp.astralsorcery.common.item.ItemConstellationFocus;
import hellfirepvp.astralsorcery.common.tile.TileAltar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationFocusSlot
 * Created by HellFirePvP
 * Date: 06.03.2017 / 14:56
 */
public class ConstellationFocusSlot extends SlotItemHandler {

    private final TileAltar ta;

    public ConstellationFocusSlot(IItemHandler itemHandler, TileAltar ta, int xPosition, int yPosition) {
        super(itemHandler, 100, xPosition, yPosition);
        this.ta = ta;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemConstellationFocus && ((ItemConstellationFocus) stack.getItem()).getFocusConstellation(stack) != null;
    }

    @Override
    public ItemStack getStack() {
        return ta.getFocusItem();
    }

    @Override
    public void putStack(@Nullable ItemStack stack) {
        ta.setFocusStack(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack) {
        super.onPickupFromSlot(playerIn, stack);
        ta.markForUpdate();
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        ItemStack focus = ta.getFocusItem();
        ta.setFocusStack(null);
        return focus;
    }

    @Override
    public boolean isHere(IInventory inv, int slotIn) {
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
