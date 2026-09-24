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
 * Class: IntRectangle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record IntRectangle(int x, int y, int width, int height) {

    public static final Codec<IntRectangle> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("x").forGetter(IntRectangle::x),
            Codec.INT.fieldOf("y").forGetter(IntRectangle::y),
            Codec.INT.fieldOf("width").forGetter(IntRectangle::width),
            Codec.INT.fieldOf("height").forGetter(IntRectangle::height)
    ).apply(inst, IntRectangle::new));

    public int maxX() {
        return this.x() + this.width();
    }

    public int maxY() {
        return this.y() + this.height();
    }

    public IntPoint center() {
        return new IntPoint(this.x + this.width / 2, this.y + this.height / 2);
    }

    public IntPoint offset() {
        return new IntPoint(this.x, this.y);
    }

    public static IntRectangle zero() {
        return new IntRectangle(0, 0, 0, 0);
    }

    public boolean contains(FloatPoint point) {
        return contains(point.x(), point.y());
    }

    public boolean contains(IntPoint point) {
        return contains(point.x(), point.y());
    }

    public boolean contains(double x, double y) {
        return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
    }

    public boolean intersects(IntRectangle other) {
        return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y;
    }

    public IntRectangle translate(int x, int y) {
        return new IntRectangle(this.x + x, this.y + y, this.width, this.height);
    }

    public IntRectangle grow(int horizontal, int vertical) {
        return new IntRectangle(this.x - horizontal, this.y - vertical, this.width + horizontal * 2, this.height + vertical * 2);
    }

    public FloatRectangle toFloat() {
        return new FloatRectangle(this.x, this.y, this.width, this.height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IntRectangle that = (IntRectangle) o;
        return x == that.x && y == that.y && width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
