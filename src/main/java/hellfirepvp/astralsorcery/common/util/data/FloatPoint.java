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

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FloatPoint
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record FloatPoint(float x, float y) {

    public static final Codec<FloatPoint> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("x").forGetter(FloatPoint::x),
            Codec.FLOAT.fieldOf("y").forGetter(FloatPoint::y)
    ).apply(inst, FloatPoint::new));

    public FloatPoint add(FloatPoint other) {
        return this.add(other.x, other.y);
    }

    public FloatPoint add(float x, float y) {
        return new FloatPoint(this.x + x, this.y + y);
    }

    public FloatPoint subtract(FloatPoint other) {
        return this.subtract(other.x, other.y);
    }

    public FloatPoint subtract(float x, float y) {
        return new FloatPoint(this.x - x, this.y - y);
    }

    public Vector3 toVector() {
        return new Vector3(this.x, this.y, 0);
    }

    public FloatPoint multiply(float factor) {
        return new FloatPoint(this.x * factor, this.y * factor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatPoint that = (FloatPoint) o;
        return Float.compare(that.x, x) == 0 && Float.compare(that.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double distance(FloatPoint other) {
        return this.distance(other.x, other.y);
    }

    public double distance(float otherX, float otherY) {
        double px = otherX - this.x();
        double py = otherY - this.y();
        return Math.sqrt(px * px + py * py);
    }
}
