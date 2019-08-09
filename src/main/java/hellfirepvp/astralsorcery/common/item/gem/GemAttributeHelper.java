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
import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.modifier.GemAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemAttributeHelper
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:41
 */
public class GemAttributeHelper {

    private static final Random rand = new Random();

    //TODO move to data registry
    private static Map<PerkAttributeType, Integer> weightedModifiers = new HashMap<PerkAttributeType, Integer>() {
        {
            put(PerkAttributeTypesAS.ATTR_TYPE_HEALTH,                    12);
            put(PerkAttributeTypesAS.ATTR_TYPE_MOVESPEED,                 8);
            put(PerkAttributeTypesAS.ATTR_TYPE_ARMOR,                     8);
            put(PerkAttributeTypesAS.ATTR_TYPE_REACH,                     4);
            put(PerkAttributeTypesAS.ATTR_TYPE_ATTACK_SPEED,              2);
            put(PerkAttributeTypesAS.ATTR_TYPE_MELEE_DAMAGE,              8);
            put(PerkAttributeTypesAS.ATTR_TYPE_PROJ_DAMAGE,               8);
            put(PerkAttributeTypesAS.ATTR_TYPE_LIFE_RECOVERY,             2);
            put(PerkAttributeTypesAS.ATTR_TYPE_INC_HARVEST_SPEED,         2);
            put(PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_CHANCE,           4);
            put(PerkAttributeTypesAS.ATTR_TYPE_INC_CRIT_MULTIPLIER,       4);
            put(PerkAttributeTypesAS.ATTR_TYPE_INC_ALL_ELEMENTAL_RESIST,  2);
            put(PerkAttributeTypesAS.ATTR_TYPE_INC_DODGE,                 2);
            put(PerkAttributeTypesAS.ATTR_TYPE_INC_PERK_EXP,              1);

            //TC runic shielding
            //put(AstralSorcery.MODID + ".compat.thaumcraft.runicshield", 2);
        }
    };
    private static Map<PerkAttributeType, Integer> configuredModifiers = Maps.newHashMap();

    private static float chance3Modifiers = 0.2F;
    private static float chance4Modifiers = 0.05F;

    private static boolean allowDuplicateTypes = false;
    private static float incModifierLower = 0.05F;
    private static float incModifierHigher = 0.08F;

    private static boolean allowNegativeModifiers = false;
    private static float chanceNegative = 0.25F;
    private static float decModifierLower = 0.05F;
    private static float decModifierHigher = 0.08F;

    private static boolean allowMoreLessModifiers = false;
    private static float chanceMultiplicative = 0.1F;
    private static float moreModifierLower = 0.05F;
    private static float moreModifierHigher = 0.08F;
    private static float lessModifierLower = 0.05F;
    private static float lessModifierHigher = 0.08F;

    public static boolean rollGem(ItemStack gem) {
        return rollGem(gem, rand);
    }

    public static boolean rollGem(ItemStack gem, Random random) {
        if (!ItemPerkGem.getModifiers(gem).isEmpty()) {
            return false;
        }
        GemType gemType = ItemPerkGem.getGemType(gem);
        if (gemType == null) {
            return false;
        }

        int rolls = getPotentialMods(random, gemType.countModifier);
        List<GemAttributeModifier> mods = new ArrayList<>();
        for (int i = 0; i < rolls; i++) {
            PerkAttributeType type = null;
            if (allowDuplicateTypes) {
                type = MiscUtils.getWeightedRandomEntry(configuredModifiers.keySet(),
                        random, s -> configuredModifiers.getOrDefault(s, 1));
            } else {
                List<PerkAttributeType> keys = new ArrayList<>(configuredModifiers.keySet());
                while (!keys.isEmpty() && type == null) {
                    PerkAttributeType item = getWeightedResultAndRemove(keys, random);
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

            ModifierType mode = isMultiplicative ? ModifierType.STACKING_MULTIPLY : ModifierType.ADDED_MULTIPLY;
            float rValue = isMultiplicative ? 1F + value : value;

            if (allowDuplicateTypes) {
                PerkAttributeType fType = type;
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
    private static PerkAttributeType getWeightedResultAndRemove(List<PerkAttributeType> list, Random random) {
        if (list.isEmpty()) {
            return null;
        }
        PerkAttributeType result = MiscUtils.getWeightedRandomEntry(list, random, s -> configuredModifiers.getOrDefault(s, 1));
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

}
