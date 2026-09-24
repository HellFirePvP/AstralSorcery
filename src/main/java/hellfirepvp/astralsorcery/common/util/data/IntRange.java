/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: IntRange
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class IntRange {

    public static final Codec<IntRange> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("minInclusive").forGetter(r -> r.minInclusive),
            Codec.INT.fieldOf("maxInclusive").forGetter(r -> r.maxInclusive)
    ).apply(inst, IntRange::new));
    public static final StreamCodec<ByteBuf, IntRange> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            IntRange::getMinInclusive,
            ByteBufCodecs.INT,
            IntRange::getMaxInclusive,
            IntRange::new);

    private final int minInclusive;
    private final int maxInclusive;

    private IntRange(int minInclusive, int maxInclusive) {
        this.minInclusive = Math.min(minInclusive, maxInclusive);
        this.maxInclusive = Math.max(minInclusive, maxInclusive);
    }

    public static IntRange of(int value) {
        return new IntRange(value, value);
    }

    public static IntRange of(int minInclusive, int maxInclusive) {
        return new IntRange(minInclusive, maxInclusive);
    }

    public int getMinInclusive() {
        return this.minInclusive;
    }

    public int getMaxInclusive() {
        return this.maxInclusive;
    }

    public int getRandom(RandomSource rand) {
        return this.minInclusive + rand.nextInt(this.maxInclusive - this.minInclusive + 1);
    }
}
