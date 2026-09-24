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
 * Class: FloatRange
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FloatRange {

    public static final Codec<FloatRange> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("minInclusive").forGetter(r -> r.minInclusive),
            Codec.FLOAT.fieldOf("maxInclusive").forGetter(r -> r.maxInclusive)
    ).apply(inst, FloatRange::new));
    public static final StreamCodec<ByteBuf, FloatRange> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            FloatRange::getMinInclusive,
            ByteBufCodecs.FLOAT,
            FloatRange::getMaxInclusive,
            FloatRange::new);

    private final float minInclusive;
    private final float maxInclusive;

    private FloatRange(float minInclusive, float maxInclusive) {
        this.minInclusive = Math.min(minInclusive, maxInclusive);
        this.maxInclusive = Math.max(minInclusive, maxInclusive);
    }

    public static FloatRange of(float value) {
        return new FloatRange(value, value);
    }

    public static FloatRange of(float minInclusive, float maxInclusive) {
        return new FloatRange(minInclusive, maxInclusive);
    }

    private float getMinInclusive() {
        return this.minInclusive;
    }

    private float getMaxInclusive() {
        return this.maxInclusive;
    }

    public float getRandom(RandomSource rand) {
        return this.minInclusive + rand.nextFloat() * (this.maxInclusive - this.minInclusive);
    }
}
