/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.enchantment.CombinedEnchantmentModifiers;
import hellfirepvp.astralsorcery.common.enchantment.EnchantmentModifier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentModifierComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record EnchantmentModifierComponent(List<EnchantmentModifier> modifiers) implements DynamicTooltipComponent {

    public static final EnchantmentModifierComponent EMPTY = new EnchantmentModifierComponent(List.of());

    public static final Codec<EnchantmentModifierComponent> CODEC = EnchantmentModifier.CODEC.listOf()
            .xmap(EnchantmentModifierComponent::new, EnchantmentModifierComponent::modifiers);

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentModifierComponent> STREAM_CODEC = StreamCodec.composite(
            EnchantmentModifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
            EnchantmentModifierComponent::modifiers,
            EnchantmentModifierComponent::new);

    public CombinedEnchantmentModifiers combined() {
        return CombinedEnchantmentModifiers.of(this.modifiers());
    }

    public boolean isEmpty() {
        return this.modifiers().isEmpty() || this.modifiers().stream().allMatch(EnchantmentModifier::isEmpty);
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        this.modifiers().forEach(modifier -> tooltipAdder.accept(modifier.getDisplay()));
    }
}
