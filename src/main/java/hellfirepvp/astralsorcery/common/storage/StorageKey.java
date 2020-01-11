/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageKey
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:45
 */
public class StorageKey {

    @Nonnull
    private final ItemStack stack;

    private StorageKey(@Nonnull ItemStack stack) {
        this.stack = stack;
    }

    public static StorageKey from(ItemStack stack) {
        return new StorageKey(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageKey that = (StorageKey) o;
        Item thisItem = this.stack.getItem();
        Item thatItem = that.stack.getItem();
        return Objects.equals(thisItem.getRegistryName(), thatItem.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stack.getItem().getRegistryName());
    }

    @Nonnull
    public CompoundNBT serialize() {
        CompoundNBT keyTag = new CompoundNBT();
        keyTag.putString("name", stack.getItem().getRegistryName().toString());
        return keyTag;
    }

    //If the item in question does no longer exist in the registry, return null.
    @Nullable
    public static StorageKey deserialize(CompoundNBT nbt) {
        ResourceLocation rl = new ResourceLocation(nbt.getString("name"));
        Item i = ForgeRegistries.ITEMS.getValue(rl);
        if (i == null || i == Items.AIR) {
            return null;
        }
        return new StorageKey(new ItemStack(i));
    }
}
