/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source.provider.lumen;

import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.PerksAS;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.CombinedLumenBindingEffect;
import hellfirepvp.astralsorcery.common.lumen.binding.effect.LumenBindingEffect;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSourceProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingSource
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingSource implements ModifierSource, AttributeModifierProvider {

    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingSource> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(EquipmentSlot.class),
            src -> src.slot,
            ItemStack.STREAM_CODEC,
            src -> src.itemStack,
            LumenBindingSource::new
    );

    final EquipmentSlot slot;
    final ItemStack itemStack;
    final List<DynamicAttributeModifier> modifierSnapshot = new ArrayList<>();

    LumenBindingSource(EquipmentSlot slot, ItemStack itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    void snapshotModifiers(Player player, LogicalSide side, boolean ignoreRequirements) {
        this.modifierSnapshot.clear();

        if (this.itemStack.isEmpty()) return;
        StoredLumenComponent storedLumenCmp = this.itemStack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        if (storedLumenCmp.isEmpty()) return;

        storedLumenCmp.getActiveBindings().stream()
                .map(lumen -> storedLumenCmp.getBinding(side, lumen))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(bindingType -> bindingType.getActiveBinding(this.itemStack, player))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(binding -> {
                    List<LumenBindingEffect> effects = new ArrayList<>();
                    effects.add(binding.getEffect());
                    if (binding.getEffect() instanceof CombinedLumenBindingEffect combined) {
                        effects.addAll(combined.getEffects());
                    }

                    effects.forEach(effect -> {
                        if (effect instanceof EquipmentAttributeModifierProvider provider) {
                            this.modifierSnapshot.addAll(provider.getModifiers(this.itemStack, player, side, ignoreRequirements).stream()
                                    .filter(modifier -> modifier instanceof DynamicAttributeModifier)
                                    .map(modifier -> (DynamicAttributeModifier) modifier)
                                    .toList());
                        }
                    });
                });
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(Player player, LogicalSide side, boolean ignoreRequirements) {
        if (this.itemStack.isEmpty()) return Collections.emptyList();
        return Collections.unmodifiableList(this.modifierSnapshot);
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
    public ModifierSourceProvider<?> getSourceProvider() {
        return PerksAS.Sources.LUMEN_BINDING.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LumenBindingSource that = (LumenBindingSource) o;
        return slot == that.slot &&
                Objects.equals(IdentifierComponent.getIdentifier(this.itemStack), IdentifierComponent.getIdentifier(that.itemStack)) &&
                Objects.equals(this.modifierSnapshot, that.modifierSnapshot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slot, IdentifierComponent.getIdentifier(this.itemStack), this.modifierSnapshot);
    }
}
