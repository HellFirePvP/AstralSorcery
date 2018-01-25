/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMinorConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:09
 */
public interface IMinorConstellation extends IConstellation {

    public List<MoonPhase> getShowupMoonPhases();

    @Override
    default boolean canDiscover(PlayerProgress progress) {
        return false;
        //return progress.getTierReached().isThisLaterOrEqual(ProgressionTier.TRAIT_CRAFT) && progress.getAttunedConstellation() != null;
    }

}
