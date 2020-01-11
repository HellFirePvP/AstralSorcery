/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;

import hellfirepvp.astralsorcery.common.util.item.ItemComparator;
import hellfirepvp.astralsorcery.common.util.item.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StoredItemStack
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:51
 */
public class StoredItemStack {

    private ItemStack stack;
    private int amount;

    StoredItemStack(ItemStack stack) {
        this(stack, stack.getCount());
    }

    private StoredItemStack(ItemStack stack, int amount) {
        this.stack = ItemUtils.copyStackWithSize(stack, 1);
        this.amount = amount;
    }

    public ItemStack getTemplateStack() {
        return ItemUtils.copyStackWithSize(stack, Math.min(stack.getMaxStackSize(), amount));
    }

    public boolean removeAmount(int amount) {
        if (this.amount - amount < 0) {
            return false;
        }
        this.amount -= amount;
        return true;
    }

    public boolean isEmpty() {
        return this.amount <= 0;
    }

    public int getAmount() {
        return amount;
    }

    public boolean combineIntoThis(StoredItemStack other) {
        if (ItemComparator.compare(this.stack, other.stack, ItemComparator.Clause.Sets.ITEMSTACK_STRICT_NOAMOUNT)) {
            amount += other.amount;
            return true;
        }
        return false;
    }

    public boolean combineIntoThis(ItemStack other) {
        if (ItemComparator.compare(this.stack, other, ItemComparator.Clause.Sets.ITEMSTACK_STRICT_NOAMOUNT)) {
            amount += other.getCount();
            return true;
        }
        return false;
    }

    @Nonnull
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("item", stack.serializeNBT());
        tag.putInt("amount", amount);
        return tag;
    }

    @Nullable
    public static StoredItemStack deserialize(CompoundNBT cmp) {
        ItemStack stack = ItemStack.read(cmp.getCompound("item"));
        if (stack.isEmpty()) {
            return null;
        }
        int amount = cmp.getInt("amount");
        return new StoredItemStack(stack, amount);
    }

}