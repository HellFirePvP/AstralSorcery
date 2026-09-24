/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FlagsComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record FlagsComponent(int flags) {

    public static final FlagsComponent EMPTY = new FlagsComponent(0);

    public static final Codec<FlagsComponent> CODEC = Codec.INT.xmap(FlagsComponent::new, FlagsComponent::flags);

    public static final StreamCodec<RegistryFriendlyByteBuf, FlagsComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            FlagsComponent::flags,
            FlagsComponent::new);

    public boolean isSet(Flag flag) {
        return (this.flags & flag.mask) != 0;
    }

    public FlagsComponent setFlag(Flag flag) {
        return new FlagsComponent(this.flags | flag.mask);
    }

    public FlagsComponent unsetFlag(Flag flag) {
        return new FlagsComponent(this.flags & ~flag.mask);
    }

    public FlagsComponent toggleFlag(Flag flag) {
        return new FlagsComponent(this.flags ^ flag.mask);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FlagsComponent that = (FlagsComponent) o;
        return flags == that.flags;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(flags);
    }

    public enum Flag implements StringRepresentable {

        IS_ARTIFACT_ENHANCED(1),
        HAS_PRISMATIC_LUMEN(1 << 1);

        public static final Codec<Flag> CODEC = StringRepresentable.fromEnum(Flag::values);

        private final int mask;

        Flag(int mask) {
            this.mask = mask;
        }

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
