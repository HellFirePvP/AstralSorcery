package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockCelestialCollectorCrystal
 * Created by HellFirePvP
 * Date: 15.09.2016 / 18:53
 */
public class BlockCelestialCollectorCrystal extends BlockCollectorCrystalBase {

    public BlockCelestialCollectorCrystal() {
        super(Material.GLASS, MapColor.CYAN);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        ItemStack stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.fornax);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);

        stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.horologium);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);

        stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.fertilitas);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);

        stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.mineralis);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);

        stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.lucerna);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);

        stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.orion);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);

        stack = new ItemStack(itemIn);
        ItemCollectorCrystal.setConstellation(stack, Constellations.circinus);
        ItemCollectorCrystal.setType(stack, CollectorCrystalType.CELESTIAL_CRYSTAL);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE_CELESTIAL, 100, 100));
        list.add(stack);
    }

}
