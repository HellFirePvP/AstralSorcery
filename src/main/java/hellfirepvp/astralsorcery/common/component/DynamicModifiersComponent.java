/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DynamicModifiersComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record DynamicModifiersComponent(List<DynamicAttributeModifier> modifiers) implements DynamicTooltipComponent {

    public static final DynamicModifiersComponent EMPTY = new DynamicModifiersComponent(List.of());

    public static final Codec<DynamicModifiersComponent> CODEC = Codec.list(DynamicAttributeModifier.CODEC)
            .xmap(DynamicModifiersComponent::new, DynamicModifiersComponent::modifiers);
    public static final StreamCodec<RegistryFriendlyByteBuf, DynamicModifiersComponent> STREAM_CODEC =
            DynamicAttributeModifier.STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(DynamicModifiersComponent::new, DynamicModifiersComponent::modifiers);

    public DynamicModifiersComponent add(DynamicAttributeModifier... modifiers) {
        return this.add(List.of(modifiers));
    }

    public DynamicModifiersComponent add(List<DynamicAttributeModifier> modifiers) {
        List<DynamicAttributeModifier> newModifiers = new ArrayList<>(this.modifiers());
        for (DynamicAttributeModifier modifier : modifiers) {
            if (newModifiers.contains(modifier)) continue;
            newModifiers.add(modifier);
        }
        return new DynamicModifiersComponent(List.copyOf(newModifiers));
    }

    public DynamicModifiersComponent remove(DynamicAttributeModifier... modifiers) {
        List<DynamicAttributeModifier> newModifiers = new ArrayList<>(this.modifiers());
        for (DynamicAttributeModifier modifier : modifiers) {
            newModifiers.remove(modifier);
        }
        return new DynamicModifiersComponent(List.copyOf(newModifiers));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DynamicModifiersComponent that = (DynamicModifiersComponent) o;
        return Objects.equals(this.modifiers, that.modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.modifiers);
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        Player player = context.player();
        PlayerProgress progress = player != null ? ResearchManager.getProgress(player, SidedHelper.getSide(player)) : null;

        this.modifiers().forEach(modifier -> {
            modifier.getAttributeType().getReader().ifPresent(reader -> {
                tooltipAdder.accept(reader.getDisplay(modifier, player, progress));
            });
        });
    }
}
