package hellfirepvp.astralsorcery.common.tile.base;

import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileReceiverBaseInventoried
 * Created by HellFirePvP
 * Date: 21.09.2016 / 23:34
 */
public abstract class TileReceiverBaseInventory extends TileReceiverBase implements IInventoryBase {

    protected ItemStack[] inv;

    public TileReceiverBaseInventory() {
        this(0);
    }

    public TileReceiverBaseInventory(int inventorySize) {
        this.inv = new ItemStack[inventorySize];
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        Arrays.fill(inv, null);
        int size = compound.getInteger("invSize");
        inv = new ItemStack[size];
        NBTTagCompound tag = compound.getCompoundTag("grid");
        for (int i = 0; i < size; i++) {
            if(tag.hasKey(String.valueOf(i))) {
                inv[i] = NBTHelper.getStack(tag, String.valueOf(i));
            }
        }
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);

        NBTTagCompound tag = new NBTTagCompound();
        for (int index = 0; index < inv.length; index++) {
            ItemStack i = inv[index];
            if (i != null) {
                NBTHelper.setStack(tag, String.valueOf(index), i);
            }
        }
        compound.setTag("grid", tag);
        compound.setInteger("invSize", inv.length);
    }

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlot(int index) {
        return inv[index];
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(inv, index, count);
        if (itemstack != null) {
            onInventoryChanged();
            markForUpdate();
        }
        return itemstack;
    }

    @Nullable
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = ItemStackHelper.getAndRemove(inv, index);
        if(stack != null) {
            onInventoryChanged();
        }
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        inv[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit()) {
            stack.stackSize = this.getInventoryStackLimit();
        }

        onInventoryChanged();
        markForUpdate();
    }

    public abstract String getInventoryName();

    @Override
    public String getName() {
        return getInventoryName();
    }

    @Override
    public void clear() {
        for (int i = 0; i < inv.length; ++i) {
            inv[i] = null;
        }

        onInventoryChanged();
        markForUpdate();
    }

    protected void onInventoryChanged() {}

}
