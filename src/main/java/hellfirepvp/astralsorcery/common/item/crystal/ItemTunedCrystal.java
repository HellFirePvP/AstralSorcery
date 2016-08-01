package hellfirepvp.astralsorcery.common.item.crystal;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.util.ItemNBTHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCrystal
 * Created by HellFirePvP
 * Date: 08.05.2016 / 22:08
 */
public class ItemTunedCrystal extends ItemRockCrystalBase {

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        applyConstellation(stack, Constellations.orion);
        subItems.add(stack);

        stack = new ItemStack(this);
        CrystalProperties.applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        applyConstellation(stack, Constellations.orion);
        applyTrait(stack, Constellations.phoenix);
        subItems.add(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        List<String> tTip = new LinkedList<>();
        super.addInformation(stack, playerIn, tTip, advanced);
        tooltip.addAll(tTip);
        if (tTip.size() == 0 || tTip.size() == 1) return; //Returns if shift is not pressed.

        Constellation c = getConstellation(stack);
        if (c != null) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.attuned") + " " + TextFormatting.BLUE + I18n.translateToLocal(c.getName()));
        }
        Constellation tr = getTrait(stack);
        if(tr != null) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.trait") + " " + TextFormatting.BLUE + I18n.translateToLocal(tr.getName()));
        }
    }

    public static void applyTrait(ItemStack stack, Constellation trait) {
        if (!(stack.getItem() instanceof ItemTunedCrystal)) return;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        cmp.setString("trait", trait.getName());
    }

    public static Constellation getTrait(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystal)) return null;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        String strCName = cmp.getString("trait");
        return ConstellationRegistry.getConstellationByName(strCName);
    }

    public static void applyConstellation(ItemStack stack, Constellation constellation) {
        if (!(stack.getItem() instanceof ItemTunedCrystal)) return;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        cmp.setString("constellation", constellation.getName());
    }

    public static Constellation getConstellation(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystal)) return null;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        String strCName = cmp.getString("constellation");
        return ConstellationRegistry.getConstellationByName(strCName);
    }

}