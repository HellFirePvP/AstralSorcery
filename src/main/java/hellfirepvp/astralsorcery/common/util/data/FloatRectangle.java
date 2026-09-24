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
 * Class: FloatRectangle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record FloatRectangle(float x, float y, float width, float height) {

    public static final Codec<FloatRectangle> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.FLOAT.fieldOf("x").forGetter(FloatRectangle::x),
            Codec.FLOAT.fieldOf("y").forGetter(FloatRectangle::y),
            Codec.FLOAT.fieldOf("width").forGetter(FloatRectangle::width),
            Codec.FLOAT.fieldOf("height").forGetter(FloatRectangle::height)
    ).apply(inst, FloatRectangle::new));

    public FloatRectangle(FloatPoint offset, float width, float height) {
        this(offset.x(), offset.y(), width, height);
    }

    public float maxX() {
        return this.x() + this.width();
    }

    public float maxY() {
        return this.y() + this.height();
    }

    public static FloatRectangle zero() {
        return new FloatRectangle(0, 0, 0, 0);
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

    public boolean intersects(FloatRectangle other) {
        return x < other.x + other.width && x + width > other.x && y < other.y + other.height && y + height > other.y;
    }

    public FloatRectangle copyInflate(float inflate) {
        return new FloatRectangle(x - inflate, y - inflate, width + 2 * inflate, height + 2 * inflate);
    }

    public FloatRectangle copyShrink(float shrink) {
        return copyInflate(-shrink);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FloatRectangle that = (FloatRectangle) o;
        return Float.compare(that.x, x) == 0 && y == that.y && width == that.width && height == that.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }
}
