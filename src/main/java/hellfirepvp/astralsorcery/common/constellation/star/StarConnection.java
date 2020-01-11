/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.star;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarConnection
 * Created by HellFirePvP
 * Date: 06.02.2016 01:58
 */
public class StarConnection {

    public final StarLocation from, to;

    public StarConnection(StarLocation from, StarLocation to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StarConnection that = (StarConnection) o;
        return (from.equals(that.from) && to.equals(that.to)) ||
                (from.equals(that.to) && to.equals(that.from));
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }
}
