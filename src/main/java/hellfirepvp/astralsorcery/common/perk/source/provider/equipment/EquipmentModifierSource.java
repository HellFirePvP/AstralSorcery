/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider.equipment;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.ModifierManager;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.util.item.ItemComparator;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EquipmentModifierSource
 * Created by HellFirePvP
 * Date: 02.04.2020 / 19:54
 */
public class EquipmentModifierSource implements ModifierSource, AttributeModifierProvider {

    final EquipmentSlotType slot;
    final ItemStack itemStack;

    EquipmentModifierSource(EquipmentSlotType slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Override
    public boolean canApplySource(PlayerEntity player, LogicalSide dist) {
        return true;
    }

    @Override
    public void onRemove(PlayerEntity player, LogicalSide dist) {}

    @Override
    public void onApply(PlayerEntity player, LogicalSide dist) {}

    @Override
    public Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        if (this.itemStack.isEmpty()) {
            return Collections.emptyList();
        }
        return DynamicModifierHelper.getDynamicModifiers(this.itemStack, player, side, ignoreRequirements);
    }

    @Override
    public boolean isEqual(ModifierSource other) {
        return this.equals(other);
    }

    @Override
    public ResourceLocation getProviderName() {
        return ModifierManager.EQUIPMENT_PROVIDER_KEY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentModifierSource that = (EquipmentModifierSource) o;
        return slot == that.slot && ItemComparator.compare(itemStack, that.itemStack, ItemComparator.Clause.Sets.ITEMSTACK_STRICT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, itemStack.getItem(), itemStack.getCount(), itemStack.getTag());
    }
}
