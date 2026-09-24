/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.star;

import hellfirepvp.astralsorcery.common.util.data.BiDiPair;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarConnection
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarConnection extends BiDiPair<StarLocation, StarLocation> {

    public StarConnection(StarLocation from, StarLocation to) {
        super(from, to);
    }

    @Override
    public String toString() {
        return "StarConnection{" + "from=" + this.getLeft() + ", to=" + this.getRight() + '}';
    }
}
