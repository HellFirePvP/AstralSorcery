/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Multimap;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SwordSharpenHelper
 * Created by HellFirePvP
 * Date: 04.11.2016 / 17:41
 */
public class SwordSharpenHelper {

    public static final String NAME_MODIFIER = "swordsharp";

    public static final AttributeModifier MODIFIER_SHARPENED;

    //API hook.
    public static List<Class<?>> otherSharpenableSwordSuperClasses = new LinkedList<>();
    public static List<String> blacklistedSharpenableSwordClassNames = new LinkedList<>();

    public static boolean isSwordSharpened(@Nonnull ItemStack stack) {
        if(!isSharpenableItem(stack)) return false;
        return NBTHelper.getData(stack).getBoolean("sharp");
    }

    public static void setSwordSharpened(@Nonnull ItemStack stack) {
        if(!isSharpenableItem(stack)) return;
        NBTHelper.getData(stack).setBoolean("sharp", true);
    }

    public static boolean canBeSharpened(@Nonnull ItemStack stack) {
        if(stack.isEmpty()) return false;
        Item i = stack.getItem();
        if(blacklistedSharpenableSwordClassNames.contains(i.getClass().getName())) return false;

        if(stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe) return true;
        Class<?> itemClass = stack.getItem().getClass();
        for (Class<?> clazz : otherSharpenableSwordSuperClasses) {
            if(clazz.isAssignableFrom(itemClass)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSharpenableItem(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemAxe);
    }

    public static void applySharpenModifier(@Nonnull ItemStack stack, EntityEquipmentSlot slot, Multimap<String, AttributeModifier> map) {
        if(isSwordSharpened(stack) && slot.equals(EntityEquipmentSlot.MAINHAND)) {
            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), MODIFIER_SHARPENED);
        }
    }

    static {
        MODIFIER_SHARPENED = new AttributeModifier(UUID.fromString("85967b31-db1c-43b9-8d0f-09bceb4e484b"), NAME_MODIFIER, Config.swordSharpMultiplier, 2);
    }

}
