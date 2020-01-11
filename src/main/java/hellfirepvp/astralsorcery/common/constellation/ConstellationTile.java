/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationTile
 * Created by HellFirePvP
 * Date: 16.08.2019 / 06:36
 */
public interface ConstellationTile {

    IWeakConstellation getAttunedConstellation();

    boolean setAttunedConstellation(IWeakConstellation cst);

    IMinorConstellation getTraitConstellation();

    boolean setTraitConstellation(IMinorConstellation cst);

}
