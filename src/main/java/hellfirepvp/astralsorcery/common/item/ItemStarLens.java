package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.constellation.starmap.ActiveStarMap;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemStarLens
 * Created by HellFirePvP
 * Date: 16.03.2017 / 16:55
 */
public class ItemStarLens extends Item {

    public ItemStarLens() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {}

    public static ActiveStarMap getStarMap(ItemStack stack) {
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        return ActiveStarMap.deserialize(tag.getCompoundTag("map"));
    }

    public static void setStarMap(ItemStack stack, ActiveStarMap map) {
        NBTHelper.getPersistentData(stack).setTag("map", map.serialize());
    }

}
