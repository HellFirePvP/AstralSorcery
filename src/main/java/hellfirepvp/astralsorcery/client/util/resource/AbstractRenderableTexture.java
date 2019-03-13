/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.resource;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AbstractRenderableTexture
 * Created by HellFirePvP
 * Date: 25.02.2018 / 23:24
 */
public abstract class AbstractRenderableTexture {

    public abstract void bindTexture();

    public abstract Point.Double getUVOffset();

    public abstract double getUWidth();

    public abstract double getVWidth();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public static abstract class Full extends AbstractRenderableTexture {

        @Override
        public Point.Double getUVOffset() {
            return new Point2D.Double(0, 0);
        }

        @Override
        public double getUWidth() {
            return 1.0;
        }

        @Override
        public double getVWidth() {
            return 1.0;
        }
    }

}
