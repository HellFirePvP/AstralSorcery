/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.binding.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.client.util.ComponentEffectUtil;
import hellfirepvp.astralsorcery.common.component.StoredLumenComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.types.LumenBindingEffectTypesAS;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBinding;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenBindingDynamicModifierEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenBindingDynamicModifierEffect extends LumenBindingEffect implements EquipmentAttributeModifierProvider {

    public static final MapCodec<LumenBindingDynamicModifierEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            DynamicAttributeModifier.CODEC.listOf().fieldOf("modifiers").forGetter(LumenBindingDynamicModifierEffect::getModifiers)
    ).apply(inst, LumenBindingDynamicModifierEffect::of));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenBindingDynamicModifierEffect> STREAM_CODEC = StreamCodec.composite(
            DynamicAttributeModifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
            LumenBindingDynamicModifierEffect::getModifiers,
            LumenBindingDynamicModifierEffect::new);

    private final List<DynamicAttributeModifier> modifiers = new ArrayList<>();

    private LumenBindingDynamicModifierEffect(List<DynamicAttributeModifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    public static LumenBindingDynamicModifierEffect.Builder builder() {
        return new Builder();
    }

    public static LumenBindingDynamicModifierEffect of(List<DynamicAttributeModifier> modifiers) {
        return new LumenBindingDynamicModifierEffect(modifiers);
    }

    public static LumenBindingDynamicModifierEffect of(DynamicAttributeModifier... modifiers) {
        return new LumenBindingDynamicModifierEffect(List.of(modifiers));
    }

    public static LumenBindingDynamicModifierEffect of(PerkAttributeType attributeType, ModifierType mode, float value) {
        return of(() -> attributeType, mode, value);
    }

    public static LumenBindingDynamicModifierEffect of(Supplier<? extends PerkAttributeType> attributeType, ModifierType mode, float value) {
        return new Builder()
                .addModifier(attributeType, mode, value)
                .build();
    }

    public List<DynamicAttributeModifier> getModifiers() {
        return this.modifiers;
    }

    @Override
    public List<Component> getDisplayText(LogicalSide side, ItemStack stack) {
        return this.getModifiers().stream().map(modifier -> {
            return modifier.getAttributeType().getReader().map(reader -> {
                Component cmp = reader.getDisplay(modifier, null, null);
                ComponentEffectUtil.applyStyle(cmp, Style.EMPTY.withColor(ChatFormatting.WHITE).withItalic(false));
                return cmp;
            }).orElse(Component.empty());
        }).filter(cmp -> !cmp.getString().isBlank()).toList();
    }

    @Override
    public DeferredType<?> getType() {
        return LumenBindingEffectTypesAS.DYNAMIC_MODIFIER;
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(ItemStack stack, Player player, LogicalSide side, boolean ignoreRequirements) {
        StoredLumenComponent storedLumenCmp = stack.getOrDefault(DataComponentsAS.STORED_LUMEN, StoredLumenComponent.EMPTY);
        if (!storedLumenCmp.isEmpty() && storedLumenCmp.hasBinding(LumenAS.PRISMATIC)) {
            float multiplier = this.getEffectMultiplier(stack, side);
            return this.getModifiers().stream()
                    .map(mod -> mod.changeValue(mod.getRawValue() * multiplier))
                    .collect(Collectors.toUnmodifiableList());
        }
        return List.copyOf(this.getModifiers());
    }

    public static class Builder {

        private final List<DynamicAttributeModifier> modifiers = new ArrayList<>();

        private Builder() {}

        public Builder addModifier(DynamicAttributeModifier modifier) {
            this.modifiers.add(modifier);
            return this;
        }

        public Builder addModifier(PerkAttributeType attributeType, ModifierType mode, float value) {
            return this.addModifier(() -> attributeType, mode, value);
        }

        public Builder addModifier(Supplier<? extends PerkAttributeType> attributeType, ModifierType mode, float value) {
            this.addModifier(new DynamicAttributeModifier(UUID.randomUUID().toString(), attributeType, mode, value));
            return this;
        }

        public LumenBindingDynamicModifierEffect build() {
            return new LumenBindingDynamicModifierEffect(this.modifiers);
        }
    }
}
