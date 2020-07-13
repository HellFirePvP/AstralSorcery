/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.gem;

import hellfirepvp.astralsorcery.common.data.config.registry.WeightedPerkAttributeRegistry;
import hellfirepvp.astralsorcery.common.data.config.registry.sets.PerkAttributeEntry;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GemAttributeHelper
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:41
 */
public class GemAttributeHelper {

    private static final Random rand = new Random();

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
        if (!DynamicModifierHelper.getStaticModifiers(gem).isEmpty()) {
            return false;
        }
        GemType gemType = ItemPerkGem.getGemType(gem);
        if (gemType == null) {
            return false;
        }

        int rolls = getPotentialMods(random, gemType.countModifier);
        List<DynamicAttributeModifier> mods = new ArrayList<>();
        List<PerkAttributeEntry> configuredModifiers = WeightedPerkAttributeRegistry.INSTANCE.getConfiguredValues();

        for (int i = 0; i < rolls; i++) {
            PerkAttributeEntry entry = null;
            if (allowDuplicateTypes) {
                entry = MiscUtils.getWeightedRandomEntry(configuredModifiers, random, PerkAttributeEntry::getWeight);
            } else {
                List<PerkAttributeEntry> keys = new ArrayList<>(configuredModifiers);
                while (!keys.isEmpty() && entry == null) {
                    PerkAttributeEntry item = getWeightedResultAndRemove(keys, random);
                    if (item != null) {
                        boolean foundType = false;
                        for (DynamicAttributeModifier m : mods) {
                            if (m.getAttributeType().equals(item.getType())) {
                                foundType = true;
                                break;
                            }
                        }

                        if (foundType) {
                            continue;
                        }
                        entry = item;
                    }
                }
            }

            if (entry == null) {
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

            PerkAttributeType type = entry.getType();
            if (allowDuplicateTypes) {
                DynamicAttributeModifier existing = MiscUtils.iterativeSearch(mods,
                        mod -> mod.getAttributeType().equals(type) && mod.getMode().equals(mode));
                if (existing != null) {
                    mods.remove(existing);
                    float combinedValue;
                    if (isMultiplicative) {
                        combinedValue = (existing.getRawValue() - 1F) + (rValue - 1F);
                    } else {
                        combinedValue = existing.getRawValue() + rValue;
                    }
                    if (combinedValue != 0F) {
                        mods.add(new DynamicAttributeModifier(UUID.randomUUID(), type, mode, isMultiplicative ? combinedValue + 1 : combinedValue));
                    } //If == 0 -> don't re-add anything.
                } else {
                    mods.add(new DynamicAttributeModifier(UUID.randomUUID(), type, mode, rValue));
                }
            } else {
                mods.add(new DynamicAttributeModifier(UUID.randomUUID(), type, mode, rValue));
            }
        }

        DynamicModifierHelper.addModifiers(gem, mods);
        return true;
    }

    @Nullable
    private static PerkAttributeEntry getWeightedResultAndRemove(List<PerkAttributeEntry> list, Random random) {
        if (list.isEmpty()) {
            return null;
        }
        PerkAttributeEntry result = MiscUtils.getWeightedRandomEntry(list, random, PerkAttributeEntry::getWeight);
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
