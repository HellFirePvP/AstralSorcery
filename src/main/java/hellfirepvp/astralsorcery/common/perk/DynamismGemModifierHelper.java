/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.config.json.data.PerkGemModifierRegistry;
import hellfirepvp.astralsorcery.common.item.DynamismGemItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DynamismGemModifierHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DynamismGemModifierHelper {

    private static final RandomSource rand = RandomSource.create();

    //TODO this. at some point.
    //TODO please rework this :(
    //TODO i can't believe this is still here
    private static final float chance3Modifiers = 0.4F;
    private static final float chance4Modifiers = 0.15F;

    private static final boolean allowDuplicateTypes = false;
    private static final float incModifierLower = 0.04F;
    private static final float incModifierHigher = 0.08F;

    private static final boolean allowNegativeModifiers = false;
    private static final float chanceNegative = 0.25F;
    private static final float decModifierLower = -0.06F;
    private static final float decModifierHigher = -0.08F;

    private static final boolean allowMoreLessModifiers = false;
    private static final float chanceMultiplicative = 0.1F;
    private static final float moreModifierLower = 0.05F;
    private static final float moreModifierHigher = 0.08F;
    private static final float lessModifierLower = -0.05F;
    private static final float lessModifierHigher = -0.08F;

    public static boolean rollGem(ItemStack gem) {
        return rollGem(gem, rand);
    }

    public static boolean rollGem(ItemStack gemStack, RandomSource random) {
        if (!(gemStack.getItem() instanceof DynamismGemItem gemItem)) return false;

        DynamismGemItem.GemType gemType = gemItem.getGemType();
        if (gemType == null) return false;

        int rolls = getPotentialMods(random, gemType.getCountModifier());
        List<DynamicAttributeModifier> mods = new ArrayList<>();

        for (int i = 0; i < rolls; i++) {
            DynamicAttributeModifier modifier = generateModifier(mods, random);
            if (modifier != null) {
                mods.add(modifier);
            }
        }

        gemStack.set(DataComponentsAS.DYNAMIC_MODIFIERS, new DynamicModifiersComponent(mods));
        return true;
    }

    @Nullable
    public static DynamicAttributeModifier generateModifier(List<DynamicAttributeModifier> existingModifiers, RandomSource random) {
        return generateModifier(existingModifiers, random, 1F);
    }

    @Nullable
    public static DynamicAttributeModifier generateModifier(List<DynamicAttributeModifier> existingModifiers, RandomSource random, float amplifierMultiplier) {
        PerkGemModifierRegistry.Entry entry = null;
        if (allowDuplicateTypes) {
            entry = PerkGemModifierRegistry.getInstance().getRandomEntry().orElse(null);
        } else {
            List<PerkGemModifierRegistry.Entry> keys = new ArrayList<>(PerkGemModifierRegistry.getInstance().getLoadedValues());
            while (!keys.isEmpty() && entry == null) {
                PerkGemModifierRegistry.Entry item = getWeightedResultAndRemove(keys, random);
                if (item != null) {
                    boolean foundType = false;
                    for (DynamicAttributeModifier m : existingModifiers) {
                        if (m.getAttributeType().equals(item.type().value())) {
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

        if (entry == null) return null;

        boolean isNegative = allowNegativeModifiers && random.nextFloat() < chanceNegative;
        boolean isMultiplicative = allowMoreLessModifiers && random.nextFloat() < chanceMultiplicative;

        float lower = isNegative ? (isMultiplicative ? lessModifierLower : decModifierLower) : (isMultiplicative ? moreModifierLower : incModifierLower);
        float higher = isNegative ? (isMultiplicative ? lessModifierHigher : decModifierHigher) : (isMultiplicative ? moreModifierHigher : incModifierHigher);

        float value;
        if (lower > higher) {
            value = lower;
        } else {
            float exp = 1F / amplifierMultiplier;
            float multiplierScale = (float) Math.pow(random.nextFloat(), exp);
            value = lower + (Mth.clamp(multiplierScale, 0F, 1F) * (higher - lower));
        }

        ModifierType mode = isMultiplicative ? ModifierType.STACKING_MULTIPLY : ModifierType.ADDED_MULTIPLY;
        float rValue = isMultiplicative ? 1F + value : value;

        PerkAttributeType type = entry.type().value();
        if (allowDuplicateTypes) {
            DynamicAttributeModifier existing = existingModifiers.stream()
                    .filter(mod -> mod.getAttributeType().equals(type) && mod.getMode().equals(mode))
                    .findFirst()
                    .orElse(null);
            if (existing != null) {
                existingModifiers.remove(existing);
                float combinedValue;
                if (isMultiplicative) {
                    combinedValue = (existing.getRawValue() - 1F) + (rValue - 1F);
                } else {
                    combinedValue = existing.getRawValue() + rValue;
                }
                if (combinedValue != 0F) {
                    return new DynamicAttributeModifier(UUID.randomUUID().toString(), type, mode, isMultiplicative ? combinedValue + 1 : combinedValue);
                } //If == 0 -> don't re-add anything.
            } else {
                return new DynamicAttributeModifier(UUID.randomUUID().toString(), type, mode, rValue);
            }
        } else {
            return new DynamicAttributeModifier(UUID.randomUUID().toString(), type, mode, rValue);
        }
        return null;
    }

    @Nullable
    private static PerkGemModifierRegistry.Entry getWeightedResultAndRemove(List<PerkGemModifierRegistry.Entry> list, RandomSource random) {
        if (list.isEmpty()) return null;

        PerkGemModifierRegistry.Entry result = MiscUtil.getWeightedRandomEntry(list, random, PerkGemModifierRegistry.Entry::weight).orElse(null);
        if (result != null) {
            list.remove(result);
        }
        return result;
    }

    private static int getPotentialMods(RandomSource random, float countModifier) {
        int mods = 2;
        if (random.nextFloat() < (chance3Modifiers + countModifier)) {
            mods++;
            if (random.nextFloat() < (chance4Modifiers + countModifier)) {
                mods++;
            }
        }
        return mods;
    }


}
