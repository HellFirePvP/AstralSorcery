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
 * Class: IntPoint
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record IntPoint(int x, int y) {

    public static final Codec<IntPoint> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("x").forGetter(IntPoint::x),
            Codec.INT.fieldOf("y").forGetter(IntPoint::y)
    ).apply(inst, IntPoint::new));

    public IntPoint add(IntPoint other) {
        return this.add(other.x, other.y);
    }

    public IntPoint add(int x, int y) {
        return new IntPoint(this.x + x, this.y + y);
    }

    public IntPoint subtract(IntPoint other) {
        return this.subtract(other.x, other.y);
    }

    public IntPoint subtract(int x, int y) {
        return new IntPoint(this.x - x, this.y - y);
    }

    public FloatPoint toFloat() {
        return new FloatPoint(this.x, this.y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntPoint point = (IntPoint) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public double distance(IntPoint other) {
        double px = other.x() - this.x();
        double py = other.y() - this.y();
        return Math.sqrt(px * px + py * py);
    }
}
