/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAttributeMap
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkAttributeMap {

    private final LogicalSide side;

    private final Map<PerkAttributeType, List<PerkAttributeModifier>> modifiers = new HashMap<>();
    private final List<PerkAttributeConverter> converters = new ArrayList<>();

    PerkAttributeMap(LogicalSide side) {
        this.side = side;
    }

    Collection<PerkAttributeModifier> applyModifier(@Nonnull Player player, @Nonnull PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        PlayerProgress prog = ResearchManager.getProgress(player, this.side);
        List<PerkAttributeModifier> added = new ArrayList<>();

        List<PerkAttributeModifier> modify = Lists.newArrayList();
        modify.add(modifier);
        modify.addAll(this.gainModifiers(player, prog, modifier, owningSource));
        for (PerkAttributeModifier mod : modify) {

            PerkAttributeModifier preMod = mod;

            mod = this.convertModifier(player, prog, mod, owningSource);

            PerkAttributeModifier postMod = mod;
            if (this.addModifierCache(player, mod.getAttributeType(), mod)) {
                added.add(mod);
            }
        }
        return added;
    }

    private boolean addModifierCache(Player player, PerkAttributeType type, PerkAttributeModifier modifier) {
        boolean noModifiers = getModifiersByType(type, modifier.getMode()).isEmpty();
        List<PerkAttributeModifier> modifiers = this.modifiers.computeIfAbsent(type, t -> Lists.newArrayList());
        if (modifiers.contains(modifier)) {
            return false;
        }

        if (noModifiers) {
            type.onModeApply(player, modifier.getMode(), side);
        }
        return modifiers.add(modifier);
    }

    Collection<PerkAttributeModifier> removeModifier(@Nonnull Player player, @Nonnull PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        PlayerProgress prog = ResearchManager.getProgress(player, this.side);
        List<PerkAttributeModifier> removed = new ArrayList<>();

        List<PerkAttributeModifier> modify = Lists.newArrayList();
        modify.add(modifier);
        modify.addAll(this.gainModifiers(player, prog, modifier, owningSource));
        for (PerkAttributeModifier mod : modify) {

            PerkAttributeModifier preMod = mod;

            mod = this.convertModifier(player, prog, mod, owningSource);

            PerkAttributeModifier postMod = mod;
            if (this.removeModifierCache(player, mod.getAttributeType(), mod)) {
                removed.add(mod);
            }
        }
        return removed;
    }

    private boolean removeModifierCache(Player player, PerkAttributeType type, PerkAttributeModifier modifier) {
        if (modifiers.computeIfAbsent(type, t -> Lists.newArrayList()).remove(modifier)) {
            boolean completelyRemoved = modifiers.get(type).isEmpty();
            if (this.getModifiersByType(type, modifier.getMode()).isEmpty()) {
                type.onModeRemove(player, modifier.getMode(), side, completelyRemoved);
            }
            return true;
        }
        return false;
    }

    @Nonnull
    private PerkAttributeModifier convertModifier(@Nonnull Player player, @Nonnull PlayerProgress progress, @Nonnull PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        for (PerkAttributeConverter converter : converters) {
            modifier = converter.convertModifier(player, progress, modifier, owningSource);
        }
        return modifier;
    }

    @Nonnull
    private Collection<PerkAttributeModifier> gainModifiers(@Nonnull Player player, @Nonnull PlayerProgress progress, @Nonnull PerkAttributeModifier modifier, @Nullable ModifierSource owningSource) {
        Collection<PerkAttributeModifier> modifiers = Lists.newArrayList();
        for (PerkAttributeConverter converter : converters) {
            modifiers.addAll(converter.gainExtraModifiers(player, progress, modifier, owningSource));
        }
        return modifiers;
    }

    boolean applyConverter(Player player, PerkAttributeConverter converter) {
        assertConvertersModifiable();

        if (converters.contains(converter)) {
            return false;
        }

        converters.add(converter);
        converter.onApply(player, side);
        return true;
    }

    boolean removeConverter(Player player, PerkAttributeConverter converter) {
        assertConvertersModifiable();

        if (converters.remove(converter)) {
            converter.onRemove(player, side);
            return true;
        }
        return false;
    }

    void assertConvertersModifiable() {
        int appliedModifiers = 0;
        for (List<PerkAttributeModifier> modifiers : this.modifiers.values()) {
            appliedModifiers += modifiers.size();
        }
        if (appliedModifiers > 0) {

            AstralSorcery.LOG.warn("Following modifiers are still applied on {} while trying to modify converters:", this.side.name());
            for (List<PerkAttributeModifier> modifiers : this.modifiers.values()) {
                for (PerkAttributeModifier modifier : modifiers) {
                    AstralSorcery.LOG.warn("Modifier: {}", modifier.getIdentifier());
                }
            }

            throw new IllegalStateException("Trying to modify PerkConverters while modifiers are applied!");
        }
    }

    public boolean hasModifiers(PerkAttributeType type) {
        return !this.modifiers.getOrDefault(type, Collections.emptyList()).isEmpty();
    }

    private List<PerkAttributeModifier> getModifiersByType(PerkAttributeType type, ModifierType mode) {
        return this.modifiers.computeIfAbsent(type, t -> Lists.newArrayList()).stream()
                .filter(mod -> mod.getMode() == mode)
                .collect(Collectors.toList());
    }

    public float getModifier(Player player, PlayerProgress progress, PerkAttributeType type) {
        return this.getModifier(player, progress, type, EnumSet.allOf(ModifierType.class));
    }

    public float getModifier(Player player, PlayerProgress progress, Supplier<? extends PerkAttributeType> type) {
        return this.getModifier(player, progress, type, EnumSet.allOf(ModifierType.class));
    }

    public float getModifier(Player player, PlayerProgress progress, PerkAttributeType type, ModifierType mode) {
        return this.getModifier(player, progress, type, EnumSet.of(mode));
    }

    public float getModifier(Player player, PlayerProgress progress, Supplier<? extends PerkAttributeType> type, ModifierType mode) {
        return this.getModifier(player, progress, type, EnumSet.of(mode));
    }

    public float getModifier(Player player, PlayerProgress progress, Supplier<? extends PerkAttributeType> type, Collection<ModifierType> applicableModes) {
        return this.getModifier(player, progress, type.get(), applicableModes);
    }

    public float getModifier(Player player, PlayerProgress progress, PerkAttributeType type, Collection<ModifierType> applicableModes) {
        float perkEffectModifier;
        if (!type.is(PerksAS.AttributeTypes.PERK_EFFECT)) {
            perkEffectModifier = this.getModifier(player, progress, PerksAS.AttributeTypes.PERK_EFFECT);
        } else {
            perkEffectModifier = 1F;
        }

        float mod = 1F;
        if (applicableModes.contains(ModifierType.ADDITION)) {
            for (PerkAttributeModifier modifier : this.getModifiersByType(type, ModifierType.ADDITION)) {
                mod += (modifier.getValue(player, progress) * perkEffectModifier);
            }
        }
        if (applicableModes.contains(ModifierType.ADDED_MULTIPLY)) {
            float multiply = mod;
            for (PerkAttributeModifier modifier : this.getModifiersByType(type, ModifierType.ADDED_MULTIPLY)) {
                mod += multiply * (modifier.getValue(player, progress) * perkEffectModifier);
            }
        }
        if (applicableModes.contains(ModifierType.STACKING_MULTIPLY)) {
            for (PerkAttributeModifier modifier : this.getModifiersByType(type, ModifierType.STACKING_MULTIPLY)) {
                mod *= ((modifier.getValue(player, progress) - 1F) * perkEffectModifier) + 1;
            }
        }
        return mod;
    }

    public float modifyValue(Player player, PlayerProgress progress, Supplier<? extends PerkAttributeType> type, float value) {
        return this.modifyValue(player, progress, type.get(), value);
    }

    public float modifyValue(Player player, PlayerProgress progress, PerkAttributeType type, float value) {
        float perkEffectModifier;
        if (!type.is(PerksAS.AttributeTypes.PERK_EFFECT)) {
            perkEffectModifier = this.modifyValue(player, progress, PerksAS.AttributeTypes.PERK_EFFECT, 1F);
        } else {
            perkEffectModifier = 1F;
        }

        float val = value;
        for (PerkAttributeModifier mod : this.getModifiersByType(type, ModifierType.ADDITION)) {
            val += (mod.getValue(player, progress) * perkEffectModifier);
        }
        float multiply = val;
        for (PerkAttributeModifier mod : this.getModifiersByType(type, ModifierType.ADDED_MULTIPLY)) {
            val += multiply * (mod.getValue(player, progress) * perkEffectModifier);
        }
        for (PerkAttributeModifier mod : this.getModifiersByType(type, ModifierType.STACKING_MULTIPLY)) {
            val *= ((mod.getValue(player, progress) - 1F) * perkEffectModifier) + 1F;
        }
        return val;
    }
}
