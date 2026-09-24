/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IntegerModeComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record IntegerModeComponent(int mode) {

    public static final IntegerModeComponent ZERO = new IntegerModeComponent(0);

    public static final Codec<IntegerModeComponent> CODEC = Codec.INT.xmap(IntegerModeComponent::new, IntegerModeComponent::mode);

    public static final StreamCodec<RegistryFriendlyByteBuf, IntegerModeComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            IntegerModeComponent::mode,
            IntegerModeComponent::new);

    public IntegerModeComponent withMode(int newMode) {
        return new IntegerModeComponent(newMode);
    }

    public <T> T getMode(Class<T> enumClass) {
        return MiscUtil.getEnumEntry(enumClass, this.mode());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        IntegerModeComponent that = (IntegerModeComponent) o;
        return mode == that.mode;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(mode);
    }
}
