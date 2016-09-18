package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalShovel
 * Created by HellFirePvP
 * Date: 18.09.2016 / 20:34
 */
public class ItemCrystalShovel extends ItemCrystalToolBase {

    public ItemCrystalShovel() {
        setDamageVsEntity(3F);
        setAttackSpeed(-1.5F);
        setHarvestLevel("shovel", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
        ItemStack stack = new ItemStack(itemIn);
        setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial));
        subItems.add(stack);
    }

}
