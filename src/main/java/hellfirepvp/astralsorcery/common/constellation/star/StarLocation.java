/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.star;

import hellfirepvp.astralsorcery.common.util.data.IntPoint;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarLocation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record StarLocation(int x, int y) {

    public int getDistanceToOrigin() {
        return x + y;
    }

    public IntPoint asPoint() {
        return new IntPoint(x, y);
    }

    public Vector3 asLevelVector() {
        return new Vector3(x, 0, y);
    }

    @Override
    public String toString() {
        return "StarLocation{" + "x=" + x + ", y=" + y + '}';
    }
}
