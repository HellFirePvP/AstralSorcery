package hellfirepvp.astralsorcery.common.item.crystal.base;

import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public abstract class ItemRockCrystalBase extends Item {

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip);
    }

    public static ItemStack createRandomBaseCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomRock());
        return crystal;
    }

    public static ItemStack createRandomCelestialCrystal() {
        ItemStack crystal = new ItemStack(ItemsAS.celestialCrystal);
        CrystalProperties.applyCrystalProperties(crystal, CrystalProperties.createRandomCelestial());
        return crystal;
    }

}
