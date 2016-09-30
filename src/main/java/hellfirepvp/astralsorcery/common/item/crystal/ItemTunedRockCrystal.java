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
 * Class: ItemTunedCrystal
 * Created by HellFirePvP
 * Date: 08.05.2016 / 22:08
 */
public class ItemTunedRockCrystal extends ItemTunedCrystalBase {

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_ROCK, 100, 100));
        applyConstellation(stack, Constellations.horologium);
        subItems.add(stack);

        /*stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_ROCK, 100, 100));
        applyConstellation(stack, Constellations.orion);
        applyTrait(stack, Constellations.phoenix);
        subItems.add(stack);*/
    }

}