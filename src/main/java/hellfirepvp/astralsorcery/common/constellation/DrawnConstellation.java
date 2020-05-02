/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DrawnConstellation
 * Created by HellFirePvP
 * Date: 02.06.2019 / 13:49
 */
public class DrawnConstellation {

    public static final int CONSTELLATION_DRAW_SIZE = 30;
    public static final float CONSTELLATION_SIZE_PART = (CONSTELLATION_DRAW_SIZE * 2F) / IConstellation.STAR_GRID_WIDTH_HEIGHT;
    public static final float CONSTELLATION_STAR_SIZE = CONSTELLATION_SIZE_PART * 2.5F;

    private final Point point;
    private final IConstellation constellation;

    public DrawnConstellation(Point point, IConstellation constellation) {
        this.point = point;
        this.constellation = constellation;
    }

    public IConstellation getConstellation() {
        return constellation;
    }

    public Point getPoint() {
        return point;
    }
}

