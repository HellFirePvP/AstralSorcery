/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integration;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IntegrationCurios
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IntegrationCurios {

    public static List<ItemStack> getCurio(LivingEntity le, Predicate<ItemStack> stack) {
        List<ItemStack> stacks = new ArrayList<>();
        Set<String> slotIds = CuriosApi.getSlots(le.level()).keySet();
        CuriosApi.getCuriosInventory(le).ifPresent(inv -> {
            slotIds.forEach(slotId -> {
                ICurioStacksHandler slotHandler = inv.getCurios().get(slotId);
                if (slotHandler != null) {
                    IDynamicStackHandler slotStackHandler = slotHandler.getStacks();
                    for (int slot = 0; slot < slotStackHandler.getSlots(); slot++) {
                        ItemStack curioSlotStack = slotStackHandler.getStackInSlot(slot);
                        if (stack.test(curioSlotStack)) {
                            stacks.add(curioSlotStack);
                        }
                    }
                }
            });
        });
        return stacks;
    }

}
