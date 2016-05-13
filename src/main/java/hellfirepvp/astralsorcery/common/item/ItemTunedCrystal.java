package hellfirepvp.astralsorcery.common.item;

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
import org.lwjgl.input.Keyboard;

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
        applyCrystalProperties(stack, new CrystalProperties(CrystalProperties.MAX_SIZE, 100, 100));
        applyConstellation(stack, Constellations.orion);
        subItems.add(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        boolean shDown = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
        List<String> tTip = new LinkedList<String>();
        addTooltip(stack, tTip, shDown);
        tooltip.addAll(tTip);
        if (tTip.size() == 0 || tTip.size() == 1) return;
        Constellation c = getConstellation(stack);
        if (c != null) {
            tooltip.add(TextFormatting.GRAY + I18n.translateToLocal("crystal.attuned") + " " + TextFormatting.BLUE + I18n.translateToLocal(c.getName()));
        }
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