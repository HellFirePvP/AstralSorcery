/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import hellfirepvp.astralsorcery.common.component.EnchantmentModifierComponent;
import hellfirepvp.astralsorcery.common.config.ConfigEntry;
import hellfirepvp.astralsorcery.common.config.json.data.AmuletEnchantmentDataRegistry;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.ModConfigSpec;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentAmuletGenerator
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EnchantmentAmuletGenerator {

    public static final Config CONFIG = new Config();
    private static final RandomSource rand = RandomSource.create();

    private static ModConfigSpec.DoubleValue chance2nd;
    private static ModConfigSpec.DoubleValue chance3rd;
    private static ModConfigSpec.DoubleValue chance2Level;
    private static ModConfigSpec.DoubleValue chanceToAll;
    private static ModConfigSpec.DoubleValue chanceToNonExisting;

    public static EnchantmentModifierComponent generateModifiers() {
        List<EnchantmentModifier> modifiers = new ArrayList<>();

        while (mayGetAdditionalRoll(modifiers)) {
            EnchantmentModifier.Type newType = getRollType(modifiers);
            if (newType == null) break;
            int lvl = getRollLevel();
            if (newType.hasEnchantment()) {
                AmuletEnchantmentDataRegistry.getInstance().getRandomEnchantment().ifPresent(ench -> {
                    modifiers.add(EnchantmentModifier.addEnchantmentLevel(newType, ench, lvl));
                });
            } else {
                modifiers.add(EnchantmentModifier.addToAll(lvl));
            }
        }

        return new EnchantmentModifierComponent(modifiers);
    }

    @Nullable
    public static EnchantmentModifier generateAnyModifier() {
        EnchantmentModifier.Type newType = getAnyRollType();
        if (newType.hasEnchantment()) {
            return AmuletEnchantmentDataRegistry.getInstance().getRandomEnchantment().map(ench -> {
                return EnchantmentModifier.addEnchantmentLevel(newType, ench, getRollLevel());
            }).orElse(null);
        } else {
            return EnchantmentModifier.addToAll(getRollLevel());
        }
    }

    @Nullable
    private static EnchantmentModifier.Type getRollType(List<EnchantmentModifier> existing) {
        int exAll = getAdditionAll(existing);
        switch (existing.size()) {
            case 0:
            case 1:
                return getAnyRollType();
            case 2:
                if (exAll > 1) {
                    return null;
                } else if (exAll == 1) {
                    if (rand.nextFloat() < chanceToNonExisting.get()) {
                        return EnchantmentModifier.Type.ADD_TO_SPECIFIC;
                    }
                    return EnchantmentModifier.Type.ADD_TO_EXISTING_SPECIFIC;
                } else {
                    return getAnyRollType();
                }
            default:
                break;
        }
        return null;
    }

    private static EnchantmentModifier.Type getAnyRollType() {
        if (rand.nextFloat() < chanceToAll.get()) {
            return EnchantmentModifier.Type.ADD_TO_EXISTING_ALL;
        }
        if (rand.nextFloat() < chanceToNonExisting.get()) {
            return EnchantmentModifier.Type.ADD_TO_SPECIFIC;
        }
        return EnchantmentModifier.Type.ADD_TO_EXISTING_SPECIFIC;
    }

    private static int getRollLevel() {
        if (rand.nextFloat() < chance2Level.get()) {
            return 2;
        }
        return 1;
    }

    private static boolean mayGetAdditionalRoll(List<EnchantmentModifier> existing) {
        return switch (existing.size()) {
            case 0 -> true;
            case 1 -> rand.nextFloat() < chance2nd.get();
            case 2 -> getAdditionAll(existing) < 2 && rand.nextFloat() < chance3rd.get();
            default -> false;
        };
    }

    private static int getAdditionAll(List<EnchantmentModifier> ench) {
        int i = 0;
        for (EnchantmentModifier e : ench) {
            if (e.getType().equals(EnchantmentModifier.Type.ADD_TO_EXISTING_ALL)) {
                i++;
            }
        }
        return i;
    }

    public static class Config extends ConfigEntry {

        public Config() {
            super("enchantment_amulet");
        }

        @Override
        public void createEntries(ModConfigSpec.Builder cfgBuilder) {
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
