/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAttributeMap
 * Created by HellFirePvP
 * Date: 08.07.2018 / 11:00
 */
public class PlayerAttributeMap {

    private Side side;
    private List<AbstractPerk> cacheAppliedPerks = new LinkedList<>();
    private Map<PerkAttributeType, List<PerkAttributeModifier>> attributes = new HashMap<>();
    private List<PerkConverter> converters = new ArrayList<>();

    PlayerAttributeMap(Side side) {
        this.side = side;
    }

    public boolean applyModifier(EntityPlayer player, String type, PerkAttributeModifier modifier) {
        PerkAttributeType attributeType = AttributeTypeRegistry.getType(type);
        if (attributeType == null) return false;

        boolean noModifiers = getModifiersByType(attributeType, modifier.getMode()).isEmpty();
        List<PerkAttributeModifier> modifiers = attributes.computeIfAbsent(attributeType, t -> Lists.newArrayList());
        if (modifiers.contains(modifier)) {
            return false;
        }

        attributeType.onApply(player, side);
        if (noModifiers) {
            attributeType.onModeApply(player, modifier.getMode(), side);
        }
        return modifiers.add(modifier);
    }

    public boolean removeModifier(EntityPlayer player, String type, PerkAttributeModifier modifier) {
        PerkAttributeType attributeType = AttributeTypeRegistry.getType(type);
        if (attributeType == null) return false;

        if (attributes.computeIfAbsent(attributeType, t -> Lists.newArrayList()).remove(modifier)) {
            boolean completelyRemoved = attributes.get(attributeType).isEmpty();
            attributeType.onRemove(player, side, completelyRemoved);
            if (getModifiersByType(attributeType, modifier.getMode()).isEmpty()) {
                attributeType.onModeRemove(player, modifier.getMode(), side, completelyRemoved);
            }
            return true;
        }
        return false;
    }

    @Nonnull
    public PerkAttributeModifier convertModifier(@Nonnull EntityPlayer player, @Nonnull PlayerProgress progress, @Nonnull PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
        for (PerkConverter converter : converters) {
            modifier = converter.convertModifier(player, progress, modifier, owningPerk);
        }
        return modifier;
    }

    @Nonnull
    public Collection<PerkAttributeModifier> gainModifiers(@Nonnull EntityPlayer player, @Nonnull PlayerProgress progress, @Nonnull PerkAttributeModifier modifier, @Nullable AbstractPerk owningPerk) {
        Collection<PerkAttributeModifier> modifiers = Lists.newArrayList();
        for (PerkConverter converter : converters) {
            modifiers.addAll(converter.gainExtraModifiers(player, progress, modifier, owningPerk));
        }
        return modifiers;
    }

    boolean markPerkApplied(AbstractPerk perk) {
        return !cacheAppliedPerks.contains(perk) && cacheAppliedPerks.add(perk);
    }

    boolean markPerkRemoved(AbstractPerk perk) {
        return cacheAppliedPerks.remove(perk);
    }

    boolean isPerkApplied(AbstractPerk perk) {
        return cacheAppliedPerks.contains(perk);
    }

    List<AbstractPerk> getCacheAppliedPerks() {
        return cacheAppliedPerks;
    }

    boolean applyConverter(EntityPlayer player, PerkConverter converter) {
        assertConvertersModifiable();

        if (converters.contains(converter)) {
            return false;
        }
        if (converters.add(converter)) {
            converter.onApply(player, side);
            return true;
        }
        return false;
    }

    boolean removeConverter(EntityPlayer player, PerkConverter converter) {
        assertConvertersModifiable();

        if (converters.remove(converter)) {
            converter.onRemove(player, side);
            return true;
        }
        return false;
    }

    void assertConvertersModifiable() {
        int appliedModifiers = 0;
        for (List<PerkAttributeModifier> modifiers : this.attributes.values()) {
            appliedModifiers += modifiers.size();
        }
        if (appliedModifiers > 0) {
            throw new IllegalStateException("Trying to modify PerkConverters while modifiers are applied!");
        }
    }

    private List<PerkAttributeModifier> getModifiersByType(PerkAttributeType type, PerkAttributeModifier.Mode mode) {
        return attributes.computeIfAbsent(type, t -> Lists.newArrayList()).stream()
                .filter(mod -> mod.getMode() == mode)
                .collect(Collectors.toList());
    }

    public float getModifier(EntityPlayer player, PlayerProgress progress, String type) {
        return getModifier(player, progress, type, Arrays.asList(PerkAttributeModifier.Mode.values()));
    }

    public float getModifier(EntityPlayer player, PlayerProgress progress, String type, PerkAttributeModifier.Mode mode) {
        return getModifier(player, progress, type, Lists.newArrayList(mode));
    }

    public float getModifier(EntityPlayer player, PlayerProgress progress, String type, Collection<PerkAttributeModifier.Mode> applicableModes) {
        PerkAttributeType attributeType = AttributeTypeRegistry.getType(type);
        if (attributeType == null) return 1F;

        float mod = 1F;

        float perkEffectModifier = 1F;
        if (!type.equals(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT)) {
            perkEffectModifier = modifyValue(player, progress, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, 1F);
        }

        if (applicableModes.contains(PerkAttributeModifier.Mode.ADDITION)) {
            for (PerkAttributeModifier modifier : getModifiersByType(attributeType, PerkAttributeModifier.Mode.ADDITION)) {
                mod += (modifier.getValue(player, progress) * perkEffectModifier);
            }
        }
        if (applicableModes.contains(PerkAttributeModifier.Mode.ADDED_MULTIPLY)) {
            float multiply = mod;
            for (PerkAttributeModifier modifier : getModifiersByType(attributeType, PerkAttributeModifier.Mode.ADDED_MULTIPLY)) {
                mod += multiply * (modifier.getValue(player, progress) * perkEffectModifier);
            }
        }
        if (applicableModes.contains(PerkAttributeModifier.Mode.STACKING_MULTIPLY)) {
            for (PerkAttributeModifier modifier : getModifiersByType(attributeType, PerkAttributeModifier.Mode.STACKING_MULTIPLY)) {
                mod *= ((modifier.getValue(player, progress) - 1F) * perkEffectModifier) + 1;
            }
        }
        return mod;
    }

    public float modifyValue(EntityPlayer player, PlayerProgress progress, String type, float value) {
        PerkAttributeType attributeType = AttributeTypeRegistry.getType(type);
        if (attributeType == null) return value;

        float perkEffectModifier = 1F;
        if (!type.equals(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT)) {
            perkEffectModifier = modifyValue(player, progress, AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, 1F);
        }

        for (PerkAttributeModifier mod : getModifiersByType(attributeType, PerkAttributeModifier.Mode.ADDITION)) {
            value += (mod.getValue(player, progress) * perkEffectModifier);
        }
        float multiply = value;
        for (PerkAttributeModifier mod : getModifiersByType(attributeType, PerkAttributeModifier.Mode.ADDED_MULTIPLY)) {
            value += multiply * (mod.getValue(player, progress) * perkEffectModifier);
        }
        for (PerkAttributeModifier mod : getModifiersByType(attributeType, PerkAttributeModifier.Mode.STACKING_MULTIPLY)) {
            value *= ((mod.getValue(player, progress) - 1F) * perkEffectModifier) + 1F;
        }
        return value;
    }

}
