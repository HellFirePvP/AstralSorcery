package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.item.crystal.base.ItemRockCrystalBase;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCelestialCrystal
 * Created by HellFirePvP
 * Date: 15.09.2016 / 14:13
 */
public class ItemCelestialCrystal extends ItemRockCrystalBase {

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        subItems.add(stack);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return RegistryItems.rarityCelestial;
    }
}
