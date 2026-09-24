/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.tool;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalPropertyCalculator;
import hellfirepvp.astralsorcery.common.item.base.CreativeTabItem;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalToolItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface CrystalToolItem extends CreativeTabItem {

    default void reApplyModifiers(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);

        float dmg = CrystalPropertyCalculator.getToolDamage(0, stack, 1F / this.getCrystalCount());
        modifiers = modifiers.withModifierAdded(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(AstralSorcery.key("crystal_properties_added_damage"), dmg, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }

    @Override
    default void fillCreativeTab(Consumer<ItemStack> tabItems) {
        ItemStack stack = new ItemStack(this);
        stack.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty()
                .setAttributeTier(CrystalPropertiesAS.SIZE.get(), CrystalPropertiesAS.SIZE.get().getMaxTier() * this.getCrystalCount())
                .setAttributeTier(CrystalPropertiesAS.CUT.get(), CrystalPropertiesAS.CUT.get().getMaxTier() * this.getCrystalCount())
                .setAttributeTier(CrystalPropertiesAS.TOOL_EFFICIENCY.get(), CrystalPropertiesAS.TOOL_EFFICIENCY.get().getMaxTier() * this.getCrystalCount())
                .setAttributeTier(CrystalPropertiesAS.TOOL_DURABILITY.get(), CrystalPropertiesAS.TOOL_DURABILITY.get().getMaxTier() * this.getCrystalCount()));
        this.reApplyModifiers(stack);
        tabItems.accept(stack);
    }

    int getCrystalCount();
}
