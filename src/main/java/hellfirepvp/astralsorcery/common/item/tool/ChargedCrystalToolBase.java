/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.item.crystal.ToolCrystalProperties;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChargedCrystalToolBase
 * Created by HellFirePvP
 * Date: 14.03.2017 / 12:38
 */
public interface ChargedCrystalToolBase {

    static Random chRand = new Random();

    @Nonnull
    public Item getInertVariant();

    public static ItemStack getAsInertVariant(ItemStack stack) {
        ToolCrystalProperties prop = getToolProperties(stack);
        ItemStack inert = new ItemStack(((ChargedCrystalToolBase) stack.getItem()).getInertVariant());
        applyToolProperties(inert, prop);
        if(stack.hasTagCompound()) {
            inert.setTagCompound(stack.getTagCompound().copy());
        }
        return inert;
    }

    public static boolean shouldRevert(ItemStack stack) {
        if (!Config.shouldChargedToolsRevert) return false;
        NBTTagCompound tag = NBTHelper.getPersistentData(stack);
        if (!tag.hasKey("chCount")) {
            tag.setInteger("chCount", 0);
        }
        int c = tag.getInteger("chCount");
        c++;
        tag.setInteger("chCount", c);
        if(c >= Config.revertStart) {
            return chRand.nextInt(Config.revertChance) == 0;
        } else {
            return false;
        }
    }

    public static void removeChargeRevertCounter(ItemStack stack) {
        NBTHelper.getPersistentData(stack).removeTag("chCount");
    }

    public static boolean tryRevertMainHand(EntityPlayer player, ItemStack stack) {
        if(shouldRevert(stack)) {
            ItemStack inert = getAsInertVariant(stack);
            removeChargeRevertCounter(inert);
            player.setHeldItem(EnumHand.MAIN_HAND, inert);
            return true;
        }
        return false;
    }

    public static void applyToolProperties(ItemStack stack, ToolCrystalProperties properties) {
        properties.writeToNBT(NBTHelper.getPersistentData(stack));
    }

    public static ToolCrystalProperties getToolProperties(ItemStack stack) {
        NBTTagCompound nbt = NBTHelper.getPersistentData(stack);
        return ToolCrystalProperties.readFromNBT(nbt);
    }

}
