/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item;

import hellfirepvp.astralsorcery.common.constellation.starmap.ActiveStarMap;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemStarLens
 * Created by HellFirePvP
 * Date: 18.03.2017 / 19:55
 */
public class ItemStarLens extends Item {

    public ItemStarLens() {
        setMaxStackSize(1);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {}

    public static ActiveStarMap getStarMap(ItemStack stack) {
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        return ActiveStarMap.deserialize(tag.getCompoundTag("map"));
    }

    public static void setStarMap(ItemStack stack, ActiveStarMap map) {
        NBTHelper.getPersistentData(stack).setTag("map", map.serialize());
    }

}
