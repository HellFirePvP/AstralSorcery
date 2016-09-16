package hellfirepvp.astralsorcery.common.item.crystal.base;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.crystal.ItemTunedRockCrystal;
import hellfirepvp.astralsorcery.common.util.nbt.ItemNBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemTunedCrystalBase
 * Created by HellFirePvP
 * Date: 15.09.2016 / 19:47
 */
public abstract class ItemTunedCrystalBase extends ItemRockCrystalBase {

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
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        cmp.setString("trait", trait.getName());
    }

    public static Constellation getTrait(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        NBTTagCompound cmp = ItemNBTHelper.getPersistentData(stack);
        String strCName = cmp.getString("trait");
        return ConstellationRegistry.getConstellationByName(strCName);
    }

    public static void applyConstellation(ItemStack stack, Constellation constellation) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return;

        constellation.writeToNBT(ItemNBTHelper.getPersistentData(stack));
    }

    public static Constellation getConstellation(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemTunedCrystalBase)) return null;

        return Constellation.readFromNBT(ItemNBTHelper.getPersistentData(stack));
    }

}
