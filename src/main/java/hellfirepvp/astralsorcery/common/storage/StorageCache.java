/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageCache
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:58
 */
public class StorageCache {

    private Map<StorageKey, List<StoredItemStack>> content = Maps.newHashMap();

    public int getTotalItemCount() {
        int i = 0;
        for (List<StoredItemStack> stacks : content.values()) {
            for (StoredItemStack stack : stacks) {
                i += stack.getAmount();
            }
        }
        return i;
    }

    public int getItemCount(StorageKey key) {
        int i = 0;
        for (StoredItemStack s : content.getOrDefault(key, Lists.newArrayList())) {
            i += s.getAmount();
        }
        return i;
    }

    public boolean add(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        StorageKey key = StorageKey.from(stack);
        for (StoredItemStack s : content.computeIfAbsent(key, stKey -> Lists.newArrayList())) {
            if (s.combineIntoThis(stack)) {
                return true;
            }
        }
        content.get(key).add(new StoredItemStack(stack));
        return true;
    }

    private void mergeIntoThis(StorageCache otherStorage) {
        for (StorageKey otherKey : otherStorage.content.keySet()) {
            List<StoredItemStack> thisStorage = this.content.computeIfAbsent(otherKey, stKey -> Lists.newArrayList());
            List<StoredItemStack> notMerged = Lists.newArrayList();

            lblIn:
            for (StoredItemStack toMerge : otherStorage.content.get(otherKey)) {
                for (StoredItemStack thisItem : thisStorage) {
                    if (thisItem.combineIntoThis(toMerge)) {
                        continue lblIn;
                    }
                }
                notMerged.add(toMerge);
            }
            thisStorage.addAll(notMerged);
        }
    }

    public boolean attemptTransferInto(StorageKey key, IItemHandler inv, int slot, boolean simulate) {
        if (content.isEmpty()) return false;

        List<StoredItemStack> stacks = this.content.get(key);
        if (stacks == null || stacks.isEmpty()) {
            return false;
        }

        for (StoredItemStack stack : stacks) {
            ItemStack sample = stack.getTemplateStack();
            int amountToRemove = sample.getCount();

            ItemStack notInserted = inv.insertItem(slot, sample, simulate);
            int addedCount = amountToRemove - notInserted.getCount();

            if (addedCount > 0) {
                if (!simulate) {
                    if (!stack.removeAmount(addedCount)) {
                        return false;
                    }
                    if (stack.isEmpty()) {
                        stacks.remove(stack);
                    }
                }

                return true;
            }
        }
        return false;
    }

    //True if anything has been transferred.
    public boolean attemptTransferInto(StorageKey key, IItemHandler inv, boolean simulate) {
        if (content.isEmpty()) return false;

        List<StoredItemStack> stacks = this.content.get(key);
        if (stacks == null || stacks.isEmpty()) {
            return false;
        }

        boolean change = false;
        lblSlots:
        for (int i = 0; i < inv.getSlots(); i++) {
            for (StoredItemStack stack : stacks) {
                ItemStack sample = stack.getTemplateStack();
                int amountToRemove = sample.getCount();

                ItemStack notInserted = inv.insertItem(i, sample, simulate);
                int addedCount = amountToRemove - notInserted.getCount();

                if (addedCount > 0) {
                    change = true;
                }

                if (!simulate) {
                    if (!stack.removeAmount(addedCount)) {
                        return false;
                    }
                    if (stack.isEmpty()) {
                        stacks.remove(stack);
                        continue lblSlots;
                    }
                }
            }
        }
        return change;
    }

    public void writeToNBT(CompoundNBT tag) {
        ListNBT content = new ListNBT();

        for (StorageKey key : this.content.keySet()) {
            CompoundNBT itemStorage = new CompoundNBT();
            itemStorage.put("storageKey", key.serialize());

            ListNBT items = new ListNBT();
            for (StoredItemStack stack : this.content.get(key)) {
                items.add(stack.serialize());
            }
            itemStorage.put("items", items);
        }

        tag.put("content", content);
    }

    public void readFromNBT(CompoundNBT tag) {
        this.content.clear();

        ListNBT content = tag.getList("content", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < content.size(); i++) {
            CompoundNBT itemStorage = content.getCompound(i);
            StorageKey key = StorageKey.deserialize(itemStorage.getCompound("storageKey"));
            if (key == null) {
                continue;
            }

            ListNBT items = itemStorage.getList("items", Constants.NBT.TAG_COMPOUND);
            List<StoredItemStack> stacks = new ArrayList<>(items.size());
            for (int j = 0; j < items.size(); j++) {
                StoredItemStack stack = StoredItemStack.deserialize(items.getCompound(j));
                if (stack != null) {
                    stacks.add(stack);
                }
            }
            this.content.put(key, stacks);
        }
    }

}
