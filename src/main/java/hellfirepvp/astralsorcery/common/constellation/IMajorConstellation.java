/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkMap;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IMajorConstellation
 * Created by HellFirePvP
 * Date: 16.11.2016 / 23:08
 */
public interface IMajorConstellation extends IWeakConstellation {

    @Nullable
    public ConstellationPerkMap getPerkMap();

    @Override
    default boolean canDiscover(PlayerProgress progress) {
        return true;
    }

}
