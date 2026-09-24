/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttunedConstellationComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttunedConstellationComponent extends ConstellationPaperComponent {

    public static final AttunedConstellationComponent EMPTY = AttunedConstellationComponent.wrap(ConstellationPaperComponent.EMPTY);

    public static final Codec<AttunedConstellationComponent> CODEC = ConstellationPaperComponent.CODEC
            .xmap(AttunedConstellationComponent::wrap, Function.identity());

    public static final StreamCodec<RegistryFriendlyByteBuf, AttunedConstellationComponent> STREAM_CODEC = ConstellationPaperComponent.STREAM_CODEC
            .map(AttunedConstellationComponent::wrap, Function.identity());

    public AttunedConstellationComponent(@Nullable BaseConstellation constellation) {
        super(constellation);
    }

    private static AttunedConstellationComponent wrap(ConstellationPaperComponent parent) {
        return new AttunedConstellationComponent(parent.getConstellation().orElse(null));
    }

    @Override
    protected void addConstellationTooltip(BaseConstellation cst, Player player, ItemStack stack, Consumer<Component> tooltipAdder) {
        PlayerProgress progress = ResearchManager.getProgress(player, SidedHelper.getSide(player));
        if (!progress.hasDiscoveredConstellation(cst)) {
            NameUtil.resolveLocalizedLines("component.constellation.crystal.unknown")
                    .forEach(key -> tooltipAdder.accept(Component.translatable(key).withStyle(ChatFormatting.GRAY)));
        } else {
            Component cstComponent = cst.getColoredName();
            tooltipAdder.accept(Component.translatable("component.constellation.crystal.attuned", cstComponent).withStyle(ChatFormatting.GRAY));
        }
    }
}
