/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IInventoryBase
 * Created by HellFirePvP
 * Date: 21.09.2016 / 14:07
 */
public interface IInventoryBase extends IInventory {

    @Override
    default public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    default public void openInventory(EntityPlayer player) {}

    @Override
    default public void closeInventory(EntityPlayer player) {}

    @Override
    default public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    default public int getField(int id) {
        return 0;
    }

    @Override
    default public void setField(int id, int value) {}

    @Override
    default public int getFieldCount() {
        return 0;
    }

    @Override
    default public boolean hasCustomName() {
        return false;
    }

    @Override
    default public ITextComponent getDisplayName() {
        return new TextComponentTranslation(getName());
    }

}
