/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.star;

import hellfirepvp.astralsorcery.common.util.data.BiDiPair;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarConnection
 * Created by HellFirePvP
 * Date: 06.02.2016 01:58
 */
public class StarConnection extends BiDiPair<StarLocation, StarLocation> {

    public final StarLocation from, to;

    public StarConnection(StarLocation from, StarLocation to) {
        super(from, to);
        this.from = from;
        this.to = to;
    }
}
