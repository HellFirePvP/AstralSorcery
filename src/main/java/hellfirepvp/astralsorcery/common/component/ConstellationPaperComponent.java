/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationPaperComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ConstellationPaperComponent implements DynamicTooltipComponent {

    public static final ConstellationPaperComponent EMPTY = new ConstellationPaperComponent(null);

    public static final Codec<ConstellationPaperComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistriesAS.REGISTRY_CONSTELLATIONS.byNameCodec().optionalFieldOf("constellation")
                    .forGetter(ConstellationPaperComponent::getConstellation)
    ).apply(inst, cst -> cst.map(ConstellationPaperComponent::new).orElse(EMPTY)));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConstellationPaperComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.registry(RegistriesAS.KEY_CONSTELLATIONS)),
            ConstellationPaperComponent::getConstellation,
            cst -> cst.map(ConstellationPaperComponent::new).orElse(EMPTY)
    );

    @Nullable
    private final BaseConstellation constellation;

    public ConstellationPaperComponent(@Nullable BaseConstellation constellation) {
        this.constellation = constellation;
    }

    public Optional<BaseConstellation> getConstellation() {
        return Optional.ofNullable(this.constellation);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ConstellationPaperComponent that = (ConstellationPaperComponent) o;
        return Objects.equals(constellation, that.constellation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(constellation);
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        this.getConstellation().ifPresent(cst -> {
            Player player = context.player();
            if (player != null) {
                this.addConstellationTooltip(cst, player, stack, tooltipAdder);
            }
        });
    }

    protected void addConstellationTooltip(BaseConstellation cst, Player player, ItemStack stack, Consumer<Component> tooltipAdder) {
        PlayerProgress progress = ResearchManager.getProgress(player, SidedHelper.getSide(player));
        if (!progress.hasSeenConstellation(cst)) {
            NameUtil.resolveLocalizedLines("component.constellation.unknown")
                    .forEach(key -> tooltipAdder.accept(Component.translatable(key).withStyle(ChatFormatting.GRAY)));
        } else {
            tooltipAdder.accept(cst.getColoredName());
        }
    }
}
