/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment.amulet;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.data.config.registry.AmuletEnchantmentRegistry;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentType;
import hellfirepvp.astralsorcery.common.item.ItemEnchantmentAmulet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AmuletRandomizeHelper
 * Created by HellFirePvP
 * Date: 11.08.2019 / 21:09
 */
public class AmuletRandomizeHelper {

    public static final Config CONFIG = new Config();
    private static final Random rand = new Random();

    private static ForgeConfigSpec.DoubleValue chance2nd;
    private static ForgeConfigSpec.DoubleValue chance3rd;
    private static ForgeConfigSpec.DoubleValue chance2Level;
    private static ForgeConfigSpec.DoubleValue chanceToAll;
    private static ForgeConfigSpec.DoubleValue chanceToNonExisting;

    public static void rollAmulet(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemEnchantmentAmulet)) {
            return;
        }

        List<AmuletEnchantment> ench = new ArrayList<>();
        while (mayGetAdditionalRoll(ench)) {
            DynamicEnchantmentType type = getRollType(ench);
            if (type != null) {
                int lvl = getRollLevel();
                if (type.isEnchantmentSpecific()) {
                    Enchantment e = AmuletEnchantmentRegistry.getRandomEnchant();
                    if (e != null) {
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
    private static DynamicEnchantmentType getRollType(List<AmuletEnchantment> existing) {
        int exAll = getAdditionAll(existing);
        switch (existing.size()) {
            case 0:
            case 1:
                if (rand.nextFloat() < chanceToAll.get()) {
                    return DynamicEnchantmentType.ADD_TO_EXISTING_ALL;
                }
                if (rand.nextFloat() < chanceToNonExisting.get()) {
                    return DynamicEnchantmentType.ADD_TO_SPECIFIC;
                }
                return DynamicEnchantmentType.ADD_TO_EXISTING_SPECIFIC;
            case 2:
                if (exAll > 1) {
                    return null;
                } else if (exAll == 1) {
                    if (rand.nextFloat() < chanceToNonExisting.get()) {
                        return DynamicEnchantmentType.ADD_TO_SPECIFIC;
                    }
                    return DynamicEnchantmentType.ADD_TO_EXISTING_SPECIFIC;
                } else {
                    if (rand.nextFloat() < chanceToAll.get()) {
                        return DynamicEnchantmentType.ADD_TO_EXISTING_ALL;
                    }
                    if (rand.nextFloat() < chanceToNonExisting.get()) {
                        return DynamicEnchantmentType.ADD_TO_SPECIFIC;
                    }
                    return DynamicEnchantmentType.ADD_TO_EXISTING_SPECIFIC;
                }
            default:
                break;
        }
        return null;
    }

    private static int getRollLevel() {
        if (rand.nextFloat() < chance2Level.get()) {
            return 2;
        }
        return 1;
    }

    private static boolean mayGetAdditionalRoll(List<AmuletEnchantment> existing) {
        if (existing.isEmpty()) return true;
        switch (existing.size()) {
            case 1:
                return rand.nextFloat() < chance2nd.get();
            case 2:
                return getAdditionAll(existing) < 2 && rand.nextFloat() < chance3rd.get();
            default:
                break;
        }
        return false;
    }

    private static int getAdditionAll(List<AmuletEnchantment> ench) {
        int i = 0;
        for (AmuletEnchantment e : ench) {
            if (e.getType().equals(DynamicEnchantmentType.ADD_TO_EXISTING_ALL)) {
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
                if (ex.canMerge(e)) {
                    ex.merge(e);
                    found = true;
                    break;
                }
            }
            if (!found) {
                enchantments.add(e);
            }
        }
        return enchantments;
    }

    public static class Config extends ConfigEntry {

        public Config() {
            super("enchantment_amulet");
        }

        @Override
        public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
            chance2nd = cfgBuilder
                    .comment("Defines the chance to roll a 2nd-enchantment-manipulating roll on the amulet. Value defines a percent chance from 0% to 100%. Setting this to 0 also prevents a 3rd roll")
                    .translation(translationKey("chance2nd"))
                    .defineInRange("chance2nd", 0.8, 0, 1.0);
            chance3rd = cfgBuilder
                    .comment("Defines the chance to roll a 3rd-enchantment-manipulation roll on the amulet. Value defines a percent chance from 0% to 100%.")
                    .translation(translationKey("chance3rd"))
                    .defineInRange("chance3rd", 0.25, 0, 1.0);

            chance2Level = cfgBuilder
                    .comment("Defines the chance the roll will be +2 instead of +1 to existing enchantment/to enchantment/to all enchantments.")
                    .translation(translationKey("chance2Level"))
                    .defineInRange("chance2Level", 0.15, 0, 1.0);

            chanceToAll = cfgBuilder
                    .comment("Defines the chance the amulet-roll 'to all existing enchantments' will appear.")
                    .translation(translationKey("chanceToAll"))
                    .defineInRange("chanceToAll", 0.02, 0, 1.0);
            chanceToNonExisting = cfgBuilder
                    .comment("Defines the chance the amulet roll 'to <encahntment>' will appear. (Don't mistake this for 'to exsting <enchantment>'!)")
                    .translation(translationKey("chanceToNonExisting"))
                    .defineInRange("chanceToNonExisting", 0.35, 0, 1.0);
        }
    }

}
