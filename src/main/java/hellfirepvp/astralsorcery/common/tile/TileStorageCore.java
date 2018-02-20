/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.auxiliary.StorageNetworkHandler;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.ItemUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileStorageCore
 * Created by HellFirePvP
 * Date: 13.12.2017 / 12:22
 */
public class TileStorageCore extends TileEntityTick implements IStorageNetworkTile {

    private Map<StorageKey, StorageCache> contents = new HashMap<>();
    private TileStorageCore masterCore = null;

    @Override
    public void update() {
        super.update();


    }

    @Override
    public void onLoad() {
        super.onLoad();

        StorageNetworkHandler.getHandler(getWorld()).addCore(this);
    }

    @Override
    public void invalidate() {
        super.invalidate();

        StorageNetworkHandler.getHandler(getWorld()).removeCore(this);
    }

    @Override
    protected void onFirstTick() {}

    @Override
    public TileStorageCore getAssociatedCore() {
        return this;
    }

    private void checkMasterIntegrity() {

    }

    public void fillAndDiscardItemOwnership(TileStorageCore newMaster, StorageNetworkHandler.MappingChange out) {

    }

    public boolean tryStoreItemInto(IItemHandler inv, StorageKey key) {
        StorageCache stored = getStorage(key);
        if(stored != null) {
            boolean transfer = stored.transferInto(inv);
            if(stored.content.isEmpty()) {
                contents.remove(key);
            }
            return transfer;
        }
        return false;
    }

    public void tryStoreItemInStorage(ItemStack stack) {
        if(stack.isEmpty()) return;

        StorageKey key = new StorageKey(stack);
        StorageCache cache = getStorage(key);
        if (cache == null) {
            cache = new StorageCache();
            contents.put(key, cache);
        }
        cache.append(stack);
    }

    @Nullable
    public StorageCache getStorage(StorageKey key) {
        return contents.get(key);
    }

    @Override
    public void readSaveNBT(NBTTagCompound compound) {
        super.readSaveNBT(compound);
        this.contents.clear();

        NBTTagList list = compound.getTagList("contents", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound cmp = list.getCompoundTagAt(i);

            NBTTagCompound keyTag = cmp.getCompoundTag("key");
            StorageKey key = StorageKey.deserialize(keyTag);
            if(key != null) {
                StorageCache cache = new StorageCache();
                NBTTagList cacheContent = cmp.getTagList("cache", Constants.NBT.TAG_COMPOUND);
                for (int j = 0; j < cacheContent.tagCount(); j++) {
                    StoredItemStack stack = StoredItemStack.deserialize(cacheContent.getCompoundTagAt(j));
                    if(stack != null && stack.isValid()) {
                        cache.content.add(stack);
                    }
                }
                if(!cache.content.isEmpty()) {
                    this.contents.put(key, cache);
                }
            }
        }
    }

    @Override
    public void writeSaveNBT(NBTTagCompound compound) {
        super.writeSaveNBT(compound);

        NBTTagList list = new NBTTagList();
        for (Map.Entry<StorageKey, StorageCache> entry : contents.entrySet()) {
            NBTTagCompound cmp = new NBTTagCompound();

            cmp.setTag("key", entry.getKey().serialize());

            NBTTagList cacheContents = new NBTTagList();
            StorageCache cache = entry.getValue();
            for (StoredItemStack stack : cache.content) {
                if(stack.isValid()) {
                    cacheContents.appendTag(stack.serialize());
                }
            }
            cmp.setTag("cache", cacheContents);

            list.appendTag(cmp);
        }
        compound.setTag("contents", list);
    }

    public static class StorageCache {

        private LinkedList<StoredItemStack> content = new LinkedList<>();

        public int getAmount() {
            int i = 0;
            for (StoredItemStack s : content) {
                i += s.amount;
            }
            return i;
        }

        private void append(ItemStack stack) {
            for (StoredItemStack s : content) {
                if(combine(stack, s)) {
                    return;
                }
            }
            content.add(new StoredItemStack(stack));
        }

        private void mergeIntoThis(StorageCache source) {
            lblIn: for (StoredItemStack stack : source.content) {
                for (StoredItemStack thisStack : this.content) {
                    if(combineStorage(stack, thisStack)) {
                        continue lblIn;
                    }
                }
            }
        }

        private boolean combineStorage(StoredItemStack incomingStack, StoredItemStack other) {
            ItemStack typeContained = other.stack;
            ItemStack incomingType = incomingStack.stack;

            if (incomingType.getItem() != typeContained.getItem()) {
                return false;
            } else if (typeContained.hasTagCompound() ^ incomingType.hasTagCompound()) {
                return false;
            } else if (typeContained.hasTagCompound() && !typeContained.getTagCompound().equals(incomingType.getTagCompound())) {
                return false;
            } else if (typeContained.getItem().getHasSubtypes() && typeContained.getMetadata() != incomingType.getMetadata()) {
                return false;
            } else if (!incomingType.areCapsCompatible(typeContained)) {
                return false;
            } else {
                other.amount += incomingStack.amount;
                return true;
            }
        }

        private boolean combine(ItemStack incomingStack, StoredItemStack other) {
            ItemStack typeContained = other.stack;

            if (incomingStack.getItem() != typeContained.getItem()) {
                return false;
            } else if (typeContained.hasTagCompound() ^ incomingStack.hasTagCompound()) {
                return false;
            } else if (typeContained.hasTagCompound() && !typeContained.getTagCompound().equals(incomingStack.getTagCompound())) {
                return false;
            } else if (typeContained.getItem().getHasSubtypes() && typeContained.getMetadata() != incomingStack.getMetadata()) {
                return false;
            } else if (!incomingStack.areCapsCompatible(typeContained)) {
                return false;
            } else {
                other.amount += incomingStack.getCount();
                return true;
            }
        }

        private boolean transferInto(IItemHandler inv) {
            if (content.isEmpty()) return false;

            for (int i = 0; i < inv.getSlots(); i++) {
                for (StoredItemStack store : content) {
                    ItemStack sample = store.getSample();

                    ItemStack notInserted = inv.insertItem(i, sample, true);
                    if (!notInserted.isEmpty()) {
                        ItemStack added = inv.insertItem(i, sample, false);
                        store.amount -= (sample.getCount() - added.getCount());
                        if (!store.isValid()) {
                            content.remove(store);
                        }
                        return true;
                    }
                }
            }
            return false;
        }

    }

    public static class StoredItemStack {

        private ItemStack stack;
        private int amount;

        StoredItemStack(ItemStack stack) {
            this(stack, stack.getCount());
        }

        StoredItemStack(ItemStack stack, int amount) {
            this.stack = ItemUtils.copyStackWithSize(stack, 1);
            this.amount = amount;
        }

        private ItemStack getSample() {
            return ItemUtils.copyStackWithSize(stack, Math.min(stack.getMaxStackSize(), amount));
        }

        private boolean isValid() {
            return amount > 0;
        }

        private NBTTagCompound serialize() {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setTag("item", stack.serializeNBT());
            tag.setInteger("amount", amount);
            return tag;
        }

        @Nullable
        static StoredItemStack deserialize(NBTTagCompound cmp) {
            ItemStack stack = new ItemStack(cmp.getCompoundTag("item"));
            if (stack.isEmpty()) {
                return null;
            }
            int amount = cmp.getInteger("amount");
            return new StoredItemStack(stack, amount);
        }

    }

    public static class StorageKey {

        @Nonnull
        private final Item item;
        private final int metadata;

        public StorageKey(@Nonnull ItemStack stack) {
            this.item = stack.getItem();
            if (this.item.isDamageable()) {
                this.metadata = 0;
            } else {
                this.metadata = stack.getMetadata();
            }
        }

        private StorageKey(@Nonnull Item item, int metadata) {
            this.item = item;
            this.metadata = metadata;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StorageKey that = (StorageKey) o;

            return metadata == that.metadata && item.getRegistryName().equals(that.item.getRegistryName());
        }

        @Override
        public int hashCode() {
            int result = item.hashCode();
            result = 31 * result + metadata;
            return result;
        }

        private NBTTagCompound serialize() {
            NBTTagCompound keyTag = new NBTTagCompound();
            keyTag.setString("name", item.getRegistryName().toString());
            keyTag.setInteger("meta", metadata);
            return keyTag;
        }

        //If the item in question does no longer exist in the registry, return null.
        @Nullable
        static StorageKey deserialize(NBTTagCompound nbt) {
            ResourceLocation rl = new ResourceLocation(nbt.getString("name"));
            Item i = ForgeRegistries.ITEMS.getValue(rl);
            if(i == null || i == Items.AIR) {
                return null;
            }
            int meta = nbt.getInteger("meta");
            return new StorageKey(i, meta);
        }
    }

}
