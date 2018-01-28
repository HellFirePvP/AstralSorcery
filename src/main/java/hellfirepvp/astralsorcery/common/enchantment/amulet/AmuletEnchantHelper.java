/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletEnchantHelper
 * Created by HellFirePvP
 * Date: 27.01.2018 / 17:39
 */
public class AmuletEnchantHelper {

    private static final Random rand = new Random();

    private static final float chance2nd = 0.9F;
    private static final float chance3rd = 0.3F;

    private static final float chance2Level = 0.08F;

    private static final float chanceToAll = 0.04F;
    private static final float chanceToNonExisting = 0.3F;

    public static void rollAmulet(ItemStack stack, float perfectionDegree) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }

        List<AmuletEnchantment> ench = new ArrayList<>();
        while (mayGetAdditionalRoll(ench, perfectionDegree)) {
            AmuletEnchantment.Type type = getRollType(ench, perfectionDegree);
            if(type != null) {
                int lvl = getRollLevel(perfectionDegree);
                if(type.hasEnchantmentTag()) {
                    Enchantment e = AmuletEnchantmentRegistry.getRandomEnchant();
                    if(e != null) {
                        ench.add(new AmuletEnchantment(type, e, lvl));
                    }
                } else {
                    ench.add(new AmuletEnchantment(type, lvl));
                }
            }
        }
        ItemEnchantmentAmulet.setAmuletEnchantments(stack, collapseEnchantments(ench));
    }

    @Nullable
    private static AmuletEnchantment.Type getRollType(List<AmuletEnchantment> existing, float perfection) {
        int exAll = getAdditionAll(existing);
        switch (existing.size()) {
            case 0:
            case 1:
                if(rand.nextFloat() < chanceToAll) {
                    return AmuletEnchantment.Type.ADD_TO_EXISTING_ALL;
                }
                if(rand.nextFloat() < chanceToNonExisting) {
                    return AmuletEnchantment.Type.ADD_TO_SPECIFIC;
                }
                return AmuletEnchantment.Type.ADD_TO_EXISTING_SPECIFIC;
            case 2:
                if(exAll > 1) {
                    return null;
                } else if(exAll == 1) {
                    if(rand.nextFloat() < chanceToNonExisting) {
                        return AmuletEnchantment.Type.ADD_TO_SPECIFIC;
                    }
                    return AmuletEnchantment.Type.ADD_TO_EXISTING_SPECIFIC;
                } else {
                    if(rand.nextFloat() < chanceToAll) {
                        return AmuletEnchantment.Type.ADD_TO_EXISTING_ALL;
                    }
                    if(rand.nextFloat() < chanceToNonExisting) {
                        return AmuletEnchantment.Type.ADD_TO_SPECIFIC;
                    }
                    return AmuletEnchantment.Type.ADD_TO_EXISTING_SPECIFIC;
                }
        }
        return null;
    }

    private static int getRollLevel(float perfection) {
        if(rand.nextFloat() < chance2Level) {
            return 2;
        }
        return 1;
    }

    private static boolean mayGetAdditionalRoll(List<AmuletEnchantment> existing, float perfection) {
        if(existing.isEmpty()) return true;
        switch (existing.size()) {
            case 1:
                return rand.nextFloat() < chance2nd;
            case 2:
                return getAdditionAll(existing) < 2 && rand.nextFloat() < chance3rd;
        }
        return false;
    }

    private static int getAdditionAll(List<AmuletEnchantment> ench) {
        int i = 0;
        for (AmuletEnchantment e : ench) {
            if(e.getType().equals(AmuletEnchantment.Type.ADD_TO_EXISTING_ALL)) {
                i++;
            }
        }
        return i;
    }

    private static List<AmuletEnchantment> collapseEnchantments(List<AmuletEnchantment> ench) {
        List<AmuletEnchantment> enchantments = new LinkedList<>();
        for (AmuletEnchantment e : ench) {
            boolean found = false;
            for (AmuletEnchantment ex : enchantments) {
                if(ex.canMerge(e)) {
                    ex.merge(e);
                    found = true;
                    break;
                }
            }
            if(!found) {
                enchantments.add(e);
            }
        }
        return enchantments;
    }

}
