/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IWeakConstellation
 * Created by HellFirePvP
 * Date: 03.01.2017 / 13:28
 */
public interface IWeakConstellation extends IConstellation {

    @Nullable
    public ConstellationEffect getRitualEffect(ILocatable origin);

}
