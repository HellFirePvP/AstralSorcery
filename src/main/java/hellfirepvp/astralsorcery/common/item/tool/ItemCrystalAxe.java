/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCrystalAxe
 * Created by HellFirePvP
 * Date: 18.09.2016 / 20:36
 */
public class ItemCrystalAxe extends ItemCrystalToolBase {

    public ItemCrystalAxe() {
        super(3);
        setDamageVsEntity(11F);
        setAttackSpeed(-3F);
        setHarvestLevel("axe", 3);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
        CrystalProperties maxCelestial = CrystalProperties.getMaxCelestialProperties();
        ItemStack stack = new ItemStack(itemIn);
        setToolProperties(stack, ToolCrystalProperties.merge(maxCelestial, maxCelestial, maxCelestial));
        subItems.add(stack);
    }
}
