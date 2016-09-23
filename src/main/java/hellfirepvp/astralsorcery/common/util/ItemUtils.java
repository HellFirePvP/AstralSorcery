package hellfirepvp.astralsorcery.common.util;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemUtils
 * Created by HellFirePvP
 * Date: 31.07.2016 / 17:51
 */
public class ItemUtils {

    private static final Random rand = new Random();

    public static EntityItem dropItem(World world, double x, double y, double z, ItemStack stack) {
        if(world.isRemote) return null;
        EntityItem ei = new EntityItem(world, x, y, z, stack);
        world.spawnEntityInWorld(ei);
        return ei;
    }
    public static EntityItem dropItemNaturally(World world, double x, double y, double z, ItemStack stack) {
        if(world.isRemote) return null;
        EntityItem ei = dropItem(world, x, y, z, stack);
        applyRandomDropOffset(ei);
        return ei;
    }

    private static void applyRandomDropOffset(EntityItem item) {
        item.motionX = rand.nextFloat() * 0.7F - 0.35D;
        item.motionY = rand.nextFloat() * 0.7F - 0.35D;
        item.motionZ = rand.nextFloat() * 0.7F - 0.35D;
    }

    public static void dropInventory(IInventory inv, World worldIn, BlockPos pos) {
        if(worldIn.isRemote) return;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(stack == null) continue;
            dropItemNaturally(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        }
    }

    public static void decrStackInInventory(ItemStack[] stacks, int slot) {
        if(slot < 0 || slot >= stacks.length) return;
        ItemStack st = stacks[slot];
        if(st == null) return;
        st.stackSize--;
        if(st.stackSize <= 0) {
            stacks[slot] = null;
        }
    }

    public static void decrStackInInventory(IInventory inv, int slot) {
        if(slot < 0 || slot >= inv.getSizeInventory()) return;
        ItemStack st = inv.getStackInSlot(slot);
        if(st == null) return;
        st.stackSize--;
        if(st.stackSize <= 0) {
            inv.setInventorySlotContents(slot, null);
        }
    }

    public static boolean tryPlaceItemInInventory(ItemStack stack, IInventory inventory) {
        return tryPlaceItemInInventory(stack, inventory, 0, inventory.getSizeInventory());
    }

    public static boolean tryPlaceItemInInventory(ItemStack stack, IInventory inventory, int start, int end) {
        ItemStack toAdd = stack.copy();
        if(!hasInventorySpace(toAdd, inventory, start, end)) return false;
        int max = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
        for (int i = start; i < end; i++) {
            if (inventory.isItemValidForSlot(i, stack)) {
                ItemStack in = inventory.getStackInSlot(i);
                if (in == null) {
                    int added = Math.min(stack.stackSize, max);
                    stack.stackSize -= added;
                    inventory.setInventorySlotContents(i, copyStackWithSize(stack, added));
                    return true;
                } else {
                    if (stackEqualsNonNBT(stack, in) && matchTags(stack, in)) {
                        int space = max-in.stackSize;
                        int added = Math.min(stack.stackSize, space);
                        stack.stackSize -= added;
                        inventory.getStackInSlot(i).stackSize += added;
                        if (stack.stackSize <= 0)
                            return true;
                    }
                }
            }
        }
        return stack.stackSize == 0;
    }

    public static boolean hasInventorySpace(ItemStack stack, IInventory inventory, int rangeMin, int rangeMax) {
        int size = stack.stackSize;
        int max = Math.min(inventory.getInventoryStackLimit(), stack.getMaxStackSize());
        for (int i = rangeMin; i < rangeMax && size > 0; i++) {
            if (inventory.isItemValidForSlot(i, stack)) {
                ItemStack in = inventory.getStackInSlot(i);
                if (in == null) {
                    size -= max;
                }
                else {
                    if (stackEqualsNonNBT(stack, in) && matchTags(stack, in)) {
                        int space = max-in.stackSize;
                        size -= space;
                    }
                }
            }
        }
        return size <= 0;
    }

    public static boolean stackEqualsNonNBT(ItemStack stack, ItemStack other) {
        if (stack == null && other == null)
            return true;
        if (stack == null || other == null || stack.getItem() == null || other.getItem() == null)
            return false;
        Item sItem = stack.getItem();
        Item oItem = other.getItem();
        if(sItem.getHasSubtypes() || oItem.getHasSubtypes()) {
            return sItem.equals(other.getItem()) &&
                    (stack.getItemDamage() == other.getItemDamage() ||
                    stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ||
                    other.getItemDamage() == OreDictionary.WILDCARD_VALUE);
        } else {
            return sItem.equals(other.getItem());
        }
    }

    public static ItemStack copyStackWithSize(ItemStack stack, int amount) {
        if(stack == null || stack.getItem() == null || amount <= 0) return null;
        ItemStack s = stack.copy();
        s.stackSize = amount;
        return s;
    }

    public static boolean matchTags(ItemStack stack, ItemStack other) {
        return ItemStack.areItemStackTagsEqual(stack, other);
    }

    public static boolean matchStacksStrict(ItemStack stack, ItemStack other) {
        return ItemStack.areItemStacksEqual(stack, other);
    }

    public static boolean matchStacks(ItemStack stack, ItemStack other) {
        if(!ItemStack.areItemsEqual(stack, other)) return false;
        return ItemStack.areItemStackTagsEqual(stack, other);
    }

    public static boolean matchesOreDict(String oreDictKey, ItemStack other) {
        List<ItemStack> stacks = OreDictionary.getOres(oreDictKey);
        for (ItemStack stack : stacks) {
            if(stack == null) continue;
            if(matchStacks(stack, other))
                return true;
        }
        return false;
    }

}
