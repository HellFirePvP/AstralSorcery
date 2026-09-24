/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeConverterProvider;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkApplicationManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkApplicationManager {

    public static void updateSource(Player player, LogicalSide side, ModifierSource oldSource, ModifierSource newSource) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attributeMap = PerkManager.getOrCreateAttributes(player);
        if (ModifierManager.isModifierApplied(player, side, oldSource)) {
            removeSource(attributeMap, player, side, oldSource);
        }
        if (!ModifierManager.isModifierApplied(player, side, newSource) && newSource.canApplySource(player, side)) {
            applySource(attributeMap, player, side, newSource);
        }
    }

    public static <T extends ModifierSource> void modifySources(Player player, LogicalSide side, Collection<T> sources, PerkManager.Action action) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attributeMap = PerkManager.getOrCreateAttributes(player);
        for (T src : sources) {
            if (action.isRemove()) {
                if (ModifierManager.isModifierApplied(player, side, src)) {
                    removeSource(attributeMap, player, side, src);
                }
            } else {
                if (!ModifierManager.isModifierApplied(player, side, src) && src.canApplySource(player, side)) {
                    applySource(attributeMap, player, side, src);
                }
            }
        }
    }

    public static void modifySource(Player player, LogicalSide side, ModifierSource source, PerkManager.Action action) {
        PlayerProgress progress = ResearchManager.getProgress(player, side);
        if (!progress.isValid()) {
            return;
        }

        PerkAttributeMap attributeMap = PerkManager.getOrCreateAttributes(player);
        if (action.isRemove()) {
            if (ModifierManager.isModifierApplied(player, side, source)) {
                removeSource(attributeMap, player, side, source);
            }
        } else {
            if (!ModifierManager.isModifierApplied(player, side, source) && source.canApplySource(player, side)) {
                applySource(attributeMap, player, side, source);
            }
        }
    }

    //***************************************** RAW APPLICATION *****************************************//

    static void applySource(PerkAttributeMap attrMap, Player player, LogicalSide side, ModifierSource add) {
        //The onlyAdd perk is already on the playerprogress (potentially with other, not-yet-added perks); filter it away.
        Collection<ModifierSource> sources = ModifierManager.getAppliedModifiers(player, side);

        sources.forEach(source -> {
            removeModifiers(source, attrMap, player, side);
            ModifierManager.removeModifier(player, side, source);
        });

        if (add instanceof AttributeConverterProvider addedConverter) {
            addedConverter.getConverters(player, side, false)
                    .forEach(c -> attrMap.applyConverter(player, c));
        }
        Collection<PerkAttributeModifier> newModifiers = applyModifiers(add, attrMap, player, side);

        sources.forEach(source -> {
            applyModifiers(source, attrMap, player, side);
            ModifierManager.addModifier(player, side, source);
        });
        //Add new source.
        ModifierManager.addModifier(player, side, add);
        newModifiers.forEach(mod -> mod.getAttributeType().onApply(player, side, add));
    }

    static Collection<PerkAttributeModifier> applyModifiers(ModifierSource source, PerkAttributeMap attrMap, Player player, LogicalSide side) {
        Collection<PerkAttributeModifier> addedModifiers = new ArrayList<>();
        if (source instanceof AttributeModifierProvider modifierSource) {
            for (PerkAttributeModifier modifier : modifierSource.getModifiers(player, side, false)) {
                addedModifiers.addAll(attrMap.applyModifier(player, modifier, source));
            }
        }
        return addedModifiers;
    }

    static void removeSource(PerkAttributeMap attrMap, Player player, LogicalSide side, ModifierSource remove) {
        //Drop the old source
        ModifierManager.removeModifier(player, side, remove);

        Collection<ModifierSource> sources = ModifierManager.getAppliedModifiers(player, side);
        sources.forEach(source -> {
            removeModifiers(source, attrMap, player, side);
            ModifierManager.removeModifier(player, side, source);
        });

        Collection<PerkAttributeModifier> removedModifiers = removeModifiers(remove, attrMap, player, side);
        if (remove instanceof AttributeConverterProvider removedConverter) {
            removedConverter.getConverters(player, side, false)
                    .forEach(c -> attrMap.removeConverter(player, c));
        }

        sources.forEach(source -> {
            applyModifiers(source, attrMap, player, side);
            ModifierManager.addModifier(player, side, source);
        });

        //PerkAttributeMap map = getOrCreateAttributes(player);
        removedModifiers.forEach(mod -> {
            mod.getAttributeType().onRemove(player, side, !attrMap.hasModifiers(mod.getAttributeType()), remove);
        });
    }

    static Collection<PerkAttributeModifier> removeModifiers(ModifierSource source, PerkAttributeMap attrMap, Player player, LogicalSide side) {
        Collection<PerkAttributeModifier> removedModifiers = new ArrayList<>();
        if (source instanceof AttributeModifierProvider modifierSource) {
            for (PerkAttributeModifier modifier : modifierSource.getModifiers(player, side, false)) {
                removedModifiers.addAll(attrMap.removeModifier(player, modifier, source));
            }
        }
        return removedModifiers;
    }
}
