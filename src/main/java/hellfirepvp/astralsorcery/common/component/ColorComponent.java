/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.util.ColorReference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ColorComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ColorComponent(ColorReference reference) {

    public static final ColorComponent WHITE = new ColorComponent(ColorReference.WHITE);
    public static final ColorComponent BLACK = new ColorComponent(ColorReference.RGB.of(0x0));
    public static final ColorComponent DEFAULT_YELLOW = new ColorComponent(ColorReference.Dye.of(DyeColor.YELLOW));

    public static final Codec<ColorComponent> CODEC = ColorReference.CODEC.xmap(ColorComponent::new, ColorComponent::reference);

    public static final StreamCodec<RegistryFriendlyByteBuf, ColorComponent> STREAM_CODEC = StreamCodec.composite(
            ColorReference.STREAM_CODEC,
            ColorComponent::reference,
            ColorComponent::new);

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ColorComponent that = (ColorComponent) o;
        return Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reference);
    }
}
