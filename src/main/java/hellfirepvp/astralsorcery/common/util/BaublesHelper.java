/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.Iterables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BaublesHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 14:14
 */
public class BaublesHelper {

    public static boolean doesPlayerWearBauble(EntityPlayer player, BaubleType inType, Predicate<ItemStack> predicate) {
        for (ItemStack worn : getWornBaublesForType(player, inType)) {
            if (predicate.test(worn)) {
                return true;
            }
        }
        return false;
    }

    public static boolean doesPlayerWearBauble(EntityPlayer player, BaubleType inType, ItemStack expected, boolean strict) {
        for (ItemStack worn : getWornBaublesForType(player, inType)) {
            if (strict ? ItemUtils.matchStacks(worn, expected) : ItemUtils.matchStackLoosely(worn, expected)) {
                return true;
            }
        }
        return false;
    }

    public static Iterable<ItemStack> getWornBaublesForType(EntityPlayer player, BaubleType type) {
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
        List<ItemStack> worn = NonNullList.create();
        for (int slot : type.getValidSlots()) {
            ItemStack stack = handler.getStackInSlot(slot);
            if(!stack.isEmpty()) {
                worn.add(stack);
            }
        }
        return worn;
    }

    public static ItemStack getFirstWornBaublesForType(EntityPlayer player, BaubleType type) {
        return Iterables.getFirst(getWornBaublesForType(player, type), ItemStack.EMPTY);
    }

}
