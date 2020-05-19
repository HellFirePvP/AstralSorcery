/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.gem;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.GemAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemAttributeHelper
 * Created by HellFirePvP
 * Date: 18.11.2018 / 10:00
 */
public class GemAttributeHelper {

    private static final Random rand = new Random();

    private static Map<String, Integer> weightedModifiers = new HashMap<String, Integer>() {
        {
            put(AttributeTypeRegistry.ATTR_TYPE_HEALTH,                    12);
            put(AttributeTypeRegistry.ATTR_TYPE_MOVESPEED,                 8);
            put(AttributeTypeRegistry.ATTR_TYPE_ARMOR,                     8);
            put(AttributeTypeRegistry.ATTR_TYPE_REACH,                     4);
            put(AttributeTypeRegistry.ATTR_TYPE_ATTACK_SPEED,              2);
            put(AttributeTypeRegistry.ATTR_TYPE_MELEE_DAMAGE,              8);
            put(AttributeTypeRegistry.ATTR_TYPE_PROJ_DAMAGE,               8);
            put(AttributeTypeRegistry.ATTR_TYPE_LIFE_RECOVERY,             2);
            put(AttributeTypeRegistry.ATTR_TYPE_INC_HARVEST_SPEED,         2);
            put(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_CHANCE,           4);
            put(AttributeTypeRegistry.ATTR_TYPE_INC_CRIT_MULTIPLIER,       4);
            put(AttributeTypeRegistry.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST,  2);
            put(AttributeTypeRegistry.ATTR_TYPE_INC_DODGE,                 2);
            put(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EXP,              1);

            //TC runic shielding
            put(AstralSorcery.MODID + ".compat.thaumcraft.runicshield", 2);
        }
    };
    private static Map<String, Integer> configuredModifiers = Maps.newHashMap();

    private static float chance3Modifiers = 0.2F;
    private static float chance4Modifiers = 0.05F;

    private static boolean allowDuplicateTypes = false;
    private static float incModifierLower = 0.05F;
    private static float incModifierHigher = 0.08F;

    private static boolean allowNegativeModifiers = false;
    private static float chanceNegative = 0.25F;
    private static float decModifierLower = -0.05F;
    private static float decModifierHigher = -0.08F;

    private static boolean allowMoreLessModifiers = false;
    private static float chanceMultiplicative = 0.1F;
    private static float moreModifierLower = 0.05F;
    private static float moreModifierHigher = 0.08F;
    private static float lessModifierLower = -0.05F;
    private static float lessModifierHigher = -0.08F;

    public static boolean rollGem(ItemStack gem) {
        return rollGem(gem, rand);
    }

    public static boolean rollGem(ItemStack gem, Random random) {
        if (!ItemPerkGem.getModifiers(gem).isEmpty()) {
            return false;
        }
        ItemPerkGem.GemType gemType = ItemPerkGem.getGemType(gem);
        if (gemType == null) {
            return false;
        }

        int rolls = getPotentialMods(random, gemType.countModifier);
        List<GemAttributeModifier> mods = new ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            String type = null;
            if (allowDuplicateTypes) {
                type = MiscUtils.getWeightedRandomEntry(configuredModifiers.keySet(),
                        random, s -> configuredModifiers.getOrDefault(s, 1));
            } else {
                List<String> keys = new ArrayList<>(configuredModifiers.keySet());
                while (!keys.isEmpty() && type == null) {
                    String item = getWeightedResultAndRemove(keys, random);
                    if (item != null) {
                        boolean foundType = false;
                        for (GemAttributeModifier m : mods) {
                            if (m.getAttributeType().equals(item)) {
                                foundType = true;
                            }
                        }
                        if (foundType) {
                            continue;
                        }
                        type = item;
                    }
                }
            }

            if (type == null) {
                continue;
            }

            boolean isNegative = allowNegativeModifiers && random.nextFloat() < chanceNegative;
            boolean isMultiplicative = allowMoreLessModifiers && random.nextFloat() < chanceMultiplicative;

            float lower = isNegative ? (isMultiplicative ? lessModifierLower : decModifierLower) : (isMultiplicative ? moreModifierLower : incModifierLower);
            float higher = isNegative ? (isMultiplicative ? lessModifierHigher : decModifierHigher) : (isMultiplicative ? moreModifierHigher : incModifierHigher);

            float value;
            if (lower > higher) {
                value = lower;
            } else {
                value = lower + (MathHelper.clamp(random.nextFloat() * gemType.amplifierModifier, 0F, 1F) * (higher - lower));
            }

            PerkAttributeModifier.Mode mode = isMultiplicative ? PerkAttributeModifier.Mode.STACKING_MULTIPLY : PerkAttributeModifier.Mode.ADDED_MULTIPLY;
            float rValue = isMultiplicative ? 1F + value : value;

            if (allowDuplicateTypes) {
                String fType = type;
                GemAttributeModifier existing = MiscUtils.iterativeSearch(mods,
                        mod -> mod.getAttributeType().equals(fType) && mod.getMode().equals(mode));
                if (existing != null) {
                    mods.remove(existing);
                    float combinedValue;
                    if (isMultiplicative) {
                        combinedValue = (existing.getFlatValue() - 1F) + (rValue - 1F);
                    } else {
                        combinedValue = existing.getFlatValue() + rValue;
                    }
                    if (combinedValue != 0F) {
                        mods.add(new GemAttributeModifier(UUID.randomUUID(), type, mode, isMultiplicative ? combinedValue + 1 : combinedValue));
                    } //If == 0 -> don't re-add anything.
                } else {
                    mods.add(new GemAttributeModifier(UUID.randomUUID(), type, mode, rValue));
                }
            } else {
                mods.add(new GemAttributeModifier(UUID.randomUUID(), type, mode, rValue));
            }
        }

        ItemPerkGem.setModifiers(gem, mods);
        return true;
    }

    @Nullable
    private static String getWeightedResultAndRemove(List<String> list, Random random) {
        if (list.isEmpty()) {
            return null;
        }
        String result = MiscUtils.getWeightedRandomEntry(list, random, s -> configuredModifiers.getOrDefault(s, 1));
        if (result != null) {
            list.remove(result);
        }
        return result;
    }

    private static int getPotentialMods(Random random, float countModifier) {
        int mods = 2;
        if (random.nextFloat() < (chance3Modifiers * countModifier)) {
            mods++;
            if (random.nextFloat() < (chance4Modifiers * countModifier)) {
                mods++;
            }
        }
        return mods;
    }

    public static class CfgEntry extends ConfigEntry {

        public CfgEntry() {
            super(Section.PERKS, "gem");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            configuredModifiers.clear();

            List<String> flattened = MiscUtils.flatten(weightedModifiers, (key, weight) -> key + "=" + weight);
            String[] arr = flattened.toArray(new String[flattened.size()]);
            String[] configuredList = cfg.getStringList(getKey() + "WeightedModifiers", getConfigurationSection(),
                    arr, "List of weighted modifiers the gem may roll. Format: 'modifier=weight'");
            fillModifiers(configuredList);

            allowDuplicateTypes = cfg.getBoolean(getKey() + "AllowDuplicateTypes", getConfigurationSection(), allowDuplicateTypes,
                    "If this is set to true, the same type of modifier (e.g. maxhealth) can roll multiple times");
            chance3Modifiers = cfg.getFloat(getKey() + "Chance3Modifiers", getConfigurationSection(), chance3Modifiers, 0F, 1F,
                    "Defines the chance the gem can roll a 3rd modifier. The lower this chance, the rarer.");
            chance4Modifiers = cfg.getFloat(getKey() + "Chance4Modifiers", getConfigurationSection(), chance4Modifiers, 0F, 1F,
                    "Defines the chance the gem can roll a 4th modifier. A 3rd modifier MUST be rolled before and the chances are independent of each other. The lower this chance, the rarer.");

            allowNegativeModifiers = cfg.getBoolean(getKey() + "AllowNegativeModifier", getConfigurationSection(), allowNegativeModifiers,
                    "If this is set to true, a modifier may roll negative instead of positive, depending on the configured chance (see ChanceNegativeModifier)");
            chanceNegative = cfg.getFloat(getKey() + "ChanceNegativeModifier", getConfigurationSection(), chanceNegative, 0F, 1F,
                    "If 'AllowNegativeModifier' is set to true, this defines the chance a given modifier may be negative instead of positive.");

            allowMoreLessModifiers = cfg.getBoolean(getKey() + "AllowMoreLessModifier", getConfigurationSection(), allowMoreLessModifiers,
                    "If this is set to true, a modifier may roll to be 'more'/'less' instead of 'increased'/'decreased', depending on the configured chance (see ChanceMoreLessModifier)");
            chanceMultiplicative = cfg.getFloat(getKey() + "ChanceMoreLessModifier", getConfigurationSection(), chanceMultiplicative, 0F, 1F,
                    "If 'AllowMoreLessModifier' is set to true, this defines the chance a given modifier may be 'more'/'less' instead of 'increased'/'decreased'.");

            String section = getConfigurationSection() + ".ranges";

            incModifierLower = cfg.getFloat("Increased_Lower_Bound", section, incModifierLower, 0F, 1F,
                    "Defines the lower bound an 'increased' modifier can roll. Value is in percent (0.01 means 1%, 0.1 means 10%, ...)");
            incModifierHigher = cfg.getFloat("Increased_Higher_Bound", section, incModifierHigher, 0F, 1F,
                    "Defines the lower bound an 'increased' modifier can roll. Value is in percent (0.01 means 1%, 0.1 means 10%, ...)");

            decModifierLower = cfg.getFloat("Decreased_Lower_Bound", section, decModifierLower, -1F, 0F,
                    "Defines the lower bound an 'decreased' modifier can roll. Value is in percent (-0.01 means 1% decreased, -0.1 means 10% decreased, ...)");
            decModifierHigher = cfg.getFloat("Decreased_Higher_Bound", section, decModifierHigher, -1F, 0F,
                    "Defines the lower bound an 'decreased' modifier can roll. Value is in percent (-0.01 means 1% decreased, -0.1 means 10% decreased, ...)");

            moreModifierLower = cfg.getFloat("More_Lower_Bound", section, moreModifierLower, 0F, 1F,
                    "Defines the lower bound an 'more' modifier can roll. Value is in percent (0.01 means 1%, 0.1 means 10%, ...)");
            moreModifierHigher = cfg.getFloat("More_Higher_Bound", section, moreModifierHigher, 0F, 1F,
                    "Defines the lower bound an 'more' modifier can roll. Value is in percent (0.01 means 1%, 0.1 means 10%, ...)");

            lessModifierLower = cfg.getFloat("Less_Lower_Bound", section, lessModifierLower, -1F, 0F,
                    "Defines the lower bound an 'less' modifier can roll. Value is in percent (-0.01 means 1% less, -0.1 means 10% less, ...)");
            lessModifierHigher = cfg.getFloat("Less_Higher_Bound", section, lessModifierHigher, -1F, 0F,
                    "Defines the lower bound an 'less' modifier can roll. Value is in percent (-0.01 means 1% less, -0.1 means 10% less, ...)");
        }

        private void fillModifiers(String[] configuredList) {
            for (String s : configuredList) {
                String[] spl = s.split("=");
                if (spl.length != 2) {
                    AstralSorcery.log.info("Ignoring wrong format for gem attribute modifier: " + s + " (Too many/not enough '=' found!)");
                    continue;
                }

                String key = spl[0];
                String strWeight = spl[1];
                int weight;
                try {
                    weight = Integer.parseInt(strWeight);
                } catch (NumberFormatException exc) {
                    AstralSorcery.log.info("Ignoring wrong format for gem attribute modifier: " + s + " (The weight '" + strWeight + "' is not an integer number!)");
                    continue;
                }

                configuredModifiers.put(key, weight);
            }
        }

    }

}
