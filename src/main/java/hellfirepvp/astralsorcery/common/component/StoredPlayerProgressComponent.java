/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.authlib.GameProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StoredPlayerProgressComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record StoredPlayerProgressComponent(Optional<PlayerProgress> progress, Optional<GameProfile> owningPlayer, boolean creative) implements DynamicTooltipComponent {

    public static final StoredPlayerProgressComponent EMPTY = new StoredPlayerProgressComponent(Optional.empty(), Optional.empty(), false);
    public static final StoredPlayerProgressComponent CREATIVE = new StoredPlayerProgressComponent(Optional.empty(), Optional.empty(), true);

    public static final Codec<StoredPlayerProgressComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            PlayerProgress.SHARE_CODEC.optionalFieldOf("progress").forGetter(StoredPlayerProgressComponent::progress),
            CodecUtil.gameProfileCodec().optionalFieldOf("owningPlayer").forGetter(StoredPlayerProgressComponent::owningPlayer),
            Codec.BOOL.fieldOf("creative").forGetter(StoredPlayerProgressComponent::creative)
    ).apply(inst, StoredPlayerProgressComponent::new));

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        if (this.creative()) {
            tooltipAdder.accept(Component.translatable("item.astralsorcery.knowledge_share.creative").withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }

        if (this.owningPlayer().isEmpty()) {
            tooltipAdder.accept(Component.translatable("item.astralsorcery.knowledge_share.empty").withStyle(ChatFormatting.GRAY));
            return;
        }

        this.owningPlayer().ifPresent(profile -> {
            Component playerName = Component.literal(profile.getName()).withStyle(ChatFormatting.GOLD);
            tooltipAdder.accept(Component.translatable("item.astralsorcery.knowledge_share.player", playerName).withStyle(ChatFormatting.BLUE));
        });
    }

    public boolean isEmpty() {
        return !this.creative() && this.progress().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StoredPlayerProgressComponent that = (StoredPlayerProgressComponent) o;
        return creative == that.creative && Objects.equals(progress, that.progress) && Objects.equals(owningPlayer, that.owningPlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(progress, owningPlayer, creative);
    }
}
