package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.ItemNBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemRockCrystalBase
 * Created by HellFirePvP
 * Date: 08.05.2016 / 21:38
 */
public class ItemRockCrystalBase extends Item {

    private static final Random rand = new Random();

    public ItemRockCrystalBase() {
        setMaxStackSize(1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        subItems.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        CrystalProperties.addPropertyTooltip(CrystalProperties.getCrystalProperties(stack), tooltip);
    }

    public static ItemStack createRandomBaseCrystal() {
        int size = (rand.nextInt(CrystalProperties.MAX_SIZE)
                + rand.nextInt(CrystalProperties.MAX_SIZE)) / 2;
        int purity = ((rand.nextInt(101) + rand.nextInt(101)) + 2) / 2;

        CrystalProperties prop =
                new CrystalProperties(size, purity, rand.nextInt(31));
        ItemStack crystal = new ItemStack(ItemsAS.rockCrystal);
        CrystalProperties.applyCrystalProperties(crystal, prop);
        return crystal;
    }

}
