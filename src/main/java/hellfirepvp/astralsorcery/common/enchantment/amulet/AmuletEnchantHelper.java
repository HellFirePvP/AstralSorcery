/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.enchantment.amulet.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

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

    private static float chance2nd = 0.8F;
    private static float chance3rd = 0.25F;

    private static float chance2Level = 0.15F;

    private static float chanceToAll = 0.02F;
    private static float chanceToNonExisting = 0.35F;

    public static void rollAmulet(ItemStack stack) {
        if(stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }

        List<AmuletEnchantment> ench = new ArrayList<>();
        while (mayGetAdditionalRoll(ench)) {
            AmuletEnchantment.Type type = getRollType(ench);
            if(type != null) {
                int lvl = getRollLevel();
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
    private static AmuletEnchantment.Type getRollType(List<AmuletEnchantment> existing) {
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
            default:
                break;
        }
        return null;
    }

    private static int getRollLevel() {
        if(rand.nextFloat() < chance2Level) {
            return 2;
        }
        return 1;
    }

    private static boolean mayGetAdditionalRoll(List<AmuletEnchantment> existing) {
        if(existing.isEmpty()) return true;
        switch (existing.size()) {
            case 1:
                return rand.nextFloat() < chance2nd;
            case 2:
                return getAdditionAll(existing) < 2 && rand.nextFloat() < chance3rd;
            default:
                break;
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

    public static class CfgEntry extends ConfigEntry {

        public CfgEntry() {
            super(Section.TOOLS, "enchantment_amulet");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            chance2nd = cfg.getFloat("chance2nd", getConfigurationSection(), chance2nd, 0F, 1F, "Defines the chance to roll a 2nd-enchantment-manipulating roll on the amulet. Value defines a percent chance from 0% to 100%. Setting this to 0 also prevents a 3rd roll");
            chance3rd = cfg.getFloat("chance3rd", getConfigurationSection(), chance3rd, 0F, 1F, "Defines the chance to roll a 3rd-enchantment-manipulation roll on the amulet. Value defines a percent chance from 0% to 100%.");

            chance2Level = cfg.getFloat("chanceLevel2", getConfigurationSection(), chance2Level, 0F, 1F, "Defines the chance the roll will be +2 instead of +1 to existing enchantment/to enchantment/to all enchantments");

            chanceToAll = cfg.getFloat("chanceToAll", getConfigurationSection(), chanceToAll, 0F, 1F, "Defines the chance the amulet-roll 'to all existing enchantments' will appear.");
            chanceToNonExisting = cfg.getFloat("chanceToNonExisting", getConfigurationSection(), chanceToNonExisting, 0F, 1F, "Defines the chance the amulet roll 'to <encahntment>' will appear. (Don't mistake this for 'to exsting <enchantment>'!)");
        }
    }

}
