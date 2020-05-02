/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;

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
    default public ConstellationEffectProvider getConstellationEffect() {
        return RegistriesAS.REGISTRY_CONSTELLATION_EFFECT.getValue(this.getRegistryName());
    }

    @Nullable
    default public MantleEffect getMantleEffect() {
        return RegistriesAS.REGISTRY_MANTLE_EFFECT.getValue(this.getRegistryName());
    }

}
