/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider.equipment;

import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EquipmentModifierSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class EquipmentModifierSource implements ModifierSource, AttributeModifierProvider {

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentModifierSource> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(EquipmentSlot.class),
            src -> src.slot,
            ItemStack.STREAM_CODEC,
            src -> src.itemStack,
            EquipmentModifierSource::new
    );

    final EquipmentSlot slot;
    final ItemStack itemStack;

    EquipmentModifierSource(EquipmentSlot slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    @Override
    public boolean canApplySource(Player player, LogicalSide dist) {
        return true;
    }

    @Override
    public void onRemove(Player player, LogicalSide dist) {
    }

    @Override
    public void onApply(Player player, LogicalSide dist) {
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(Player player, LogicalSide side, boolean ignoreRequirements) {
        if (this.itemStack.isEmpty()) {
            return Collections.emptyList();
        }
        return DynamicModifierHelper.getModifiers(this.itemStack, player, side, ignoreRequirements);
    }

    @Override
    public ModifierSourceProvider<?> getSourceProvider() {
        return PerksAS.Sources.EQUIPMENT.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentModifierSource that = (EquipmentModifierSource) o;
        return slot == that.slot && Objects.equals(IdentifierComponent.getIdentifier(this.itemStack), IdentifierComponent.getIdentifier(that.itemStack));
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, IdentifierComponent.getIdentifier(this.itemStack));
    }
}
