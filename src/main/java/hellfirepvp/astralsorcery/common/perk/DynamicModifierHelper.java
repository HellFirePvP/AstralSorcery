/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DynamicModifierHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DynamicModifierHelper {

    public static void addModifier(ItemStack stack, String identifier, PerkAttributeType type, ModifierType mode, float value) {
        addModifier(stack, new DynamicAttributeModifier(identifier, type, mode, value));
    }

    public static void addModifier(ItemStack stack, DynamicAttributeModifier... modifiers) {
        addModifier(stack, List.of(modifiers));
    }

    public static void addModifier(ItemStack stack, List<DynamicAttributeModifier> modifiers) {
        DynamicModifiersComponent component = stack.getOrDefault(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY);
        component = component.add(modifiers);
        stack.set(DataComponentsAS.DYNAMIC_MODIFIERS, component);
        IdentifierComponent.createIdentifierIfNotExists(stack);
    }

    public static List<PerkAttributeModifier> getModifiers(ItemStack stack, Player player, LogicalSide side, boolean ignoreRequirements) {
        List<PerkAttributeModifier> modifiers = Lists.newArrayList();

        if (stack.getItem() instanceof AttributeModifierProvider modifierProvider) {
            modifiers.addAll(modifierProvider.getModifiers(player, side, ignoreRequirements));
        }
        if (stack.getItem() instanceof EquipmentAttributeModifierProvider modifierProvider) {
            modifiers.addAll(modifierProvider.getModifiers(stack, player, side, ignoreRequirements));
        }
        DynamicModifiersComponent component = stack.getOrDefault(DataComponentsAS.DYNAMIC_MODIFIERS, DynamicModifiersComponent.EMPTY);
        modifiers.addAll(component.modifiers());

        return modifiers;
    }
}
