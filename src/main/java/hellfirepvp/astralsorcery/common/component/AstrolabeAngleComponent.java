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
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstrolabeAngleComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record AstrolabeAngleComponent(float angle, boolean matchesAll) implements DynamicTooltipComponent {

    public static final AstrolabeAngleComponent DEFAULT = new AstrolabeAngleComponent(45F, false);

    public static final Codec<AstrolabeAngleComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("angle").forGetter(AstrolabeAngleComponent::angle),
            Codec.BOOL.fieldOf("matches_all").forGetter(AstrolabeAngleComponent::matchesAll)
    ).apply(inst, AstrolabeAngleComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AstrolabeAngleComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            AstrolabeAngleComponent::angle,
            ByteBufCodecs.BOOL,
            AstrolabeAngleComponent::matchesAll,
            AstrolabeAngleComponent::new);

    public AstrolabeAngleComponent(float angle, boolean matchesAll) {
        this.angle = Math.clamp(angle, 0, 90);
        this.matchesAll = matchesAll;
    }

    public float getAngleVisibility(BaseConstellation cst, float cstAngle) {
        if (this.matchesAll()) {
            return 1F;
        }
        return Mth.clamp(1F - Math.abs(this.angle - cstAngle) / 8F, 0F, 1F);
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        if (this.matchesAll()) return;
        int angle = Math.round(this.angle());
        tooltipAdder.accept(
                Component.translatable("item.astralsorcery.astrolabe.angle",
                                Component.literal(String.valueOf(angle)).withStyle(ChatFormatting.GOLD))
                        .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AstrolabeAngleComponent that = (AstrolabeAngleComponent) o;
        return Float.compare(angle, that.angle) == 0 && matchesAll == that.matchesAll;
    }

    @Override
    public int hashCode() {
        return Objects.hash(angle, matchesAll);
    }
}
