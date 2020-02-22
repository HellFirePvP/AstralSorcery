/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.useables;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemShiftingStarEvorsio
 * Created by HellFirePvP
 * Date: 22.02.2020 / 22:04
 */
public class ItemShiftingStarEvorsio extends ItemShiftingStar {

    @Nullable
    @Override
    public IMajorConstellation getBaseConstellation() {
        return ConstellationsAS.evorsio;
    }
}
