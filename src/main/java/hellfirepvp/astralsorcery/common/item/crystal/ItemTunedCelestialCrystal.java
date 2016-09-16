package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.item.crystal.base.ItemTunedCrystalBase;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCelestialCrystal
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:49
 */
public class ItemTunedCelestialCrystal extends ItemTunedCrystalBase {

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        applyConstellation(stack, Constellations.ara);
        subItems.add(stack);

        stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        applyConstellation(stack, Constellations.ara);
        applyTrait(stack, Constellations.draco);
        subItems.add(stack);
    }

}
