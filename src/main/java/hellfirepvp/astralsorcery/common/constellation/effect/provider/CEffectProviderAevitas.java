/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.provider;

import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.CEffectAevitas;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CEffectProviderAevitas
 * Created by HellFirePvP
 * Date: 27.07.2019 / 22:42
 */
public class CEffectProviderAevitas extends ConstellationEffectProvider {

    public CEffectProviderAevitas() {
        super(ConstellationsAS.aevitas);
    }

    @Override
    public ConstellationEffect createEffect(@Nullable ILocatable origin) {
        return new CEffectAevitas(origin);
    }
}
