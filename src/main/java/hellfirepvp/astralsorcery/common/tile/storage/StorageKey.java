/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile.storage;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageKey
 * Created by HellFirePvP
 * Date: 01.12.2018 / 10:58
 */
public class StorageKey {

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

    public static StorageKey from(ItemStack stack) {
        return new StorageKey(stack);
    }

    public static StorageKey from(@Nonnull Item item, int meta) {
        return new StorageKey(item, meta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageKey that = (StorageKey) o;

        return (item.getHasSubtypes() || metadata == that.metadata) && item.getRegistryName().equals(that.item.getRegistryName());
    }

    @Override
    public int hashCode() {
        int result = item.getRegistryName().hashCode();
        if (item.getHasSubtypes()) {
            result = 31 * result + metadata;
        }
        return result;
    }

    @Nonnull
    public NBTTagCompound serialize() {
        NBTTagCompound keyTag = new NBTTagCompound();
        keyTag.setString("name", item.getRegistryName().toString());
        keyTag.setInteger("meta", metadata);
        return keyTag;
    }

    //If the item in question does no longer exist in the registry, return null.
    @Nullable
    public static StorageKey deserialize(NBTTagCompound nbt) {
        ResourceLocation rl = new ResourceLocation(nbt.getString("name"));
        Item i = ForgeRegistries.ITEMS.getValue(rl);
        if (i == null || i == Items.AIR) {
            return null;
        }
        int meta = nbt.getInteger("meta");
        return new StorageKey(i, meta);
    }
}
