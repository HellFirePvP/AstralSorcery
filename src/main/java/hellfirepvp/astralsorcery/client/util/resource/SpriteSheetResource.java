/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.resource;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.common.util.data.Tuple;

import java.awt.*;
import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteSheetResource
 * Created by HellFirePvP
 * Date: 14.09.2016 / 09:15
 */
public class SpriteSheetResource extends AbstractRenderableTexture {

    protected double uPart, vPart;
    protected int frameCount;
    protected int rows, columns;

    private final AbstractRenderableTexture resource;

    public SpriteSheetResource(AbstractRenderableTexture resource, int rows, int columns) {
        if(rows <= 0 || columns <= 0)
            throw new IllegalArgumentException("Can't instantiate a sprite sheet without any rows or columns!");

        frameCount = rows * columns;
        this.rows = rows;
        this.columns = columns;
        this.resource = resource;

        this.uPart = 1D / ((double) columns);
        this.vPart = 1D / ((double) rows);
    }

    @Override
    public void bindTexture() {
        this.resource.bindTexture();
    }

    @Override
    public Point.Double getUVOffset() {
        long timer = ClientScheduler.getClientTick();
        Tuple<Double, Double> offset = getUVOffset(timer);
        return new Point.Double(offset.key, offset.value);
    }

    @Override
    public double getUWidth() {
        return getULength();
    }

    @Override
    public double getVWidth() {
        return getVLength();
    }

    public AbstractRenderableTexture getResource() {
        return resource;
    }

    public double getULength() {
        return uPart;
    }

    public double getVLength() {
        return vPart;
    }

    public Tuple<Double, Double> getUVOffset(long frameTimer) {
        int frame = (int) (frameTimer % frameCount);
        return new Tuple<>((frame % columns) * uPart, (frame / columns) * vPart);
    }

    public int getFrameCount() {
        return frameCount;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpriteSheetResource that = (SpriteSheetResource) o;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return this.resource.hashCode();
    }
}
