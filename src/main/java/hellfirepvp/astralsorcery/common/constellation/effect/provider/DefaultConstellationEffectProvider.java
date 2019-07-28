/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect.provider;

import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DefaultConstellationEffectProvider
 * Created by HellFirePvP
 * Date: 28.07.2019 / 10:39
 */
public class DefaultConstellationEffectProvider extends ConstellationEffectProvider {

    private final Function<ILocatable, ? extends ConstellationEffect> effectProvider;

    public DefaultConstellationEffectProvider(IWeakConstellation cst, Function<ILocatable, ? extends ConstellationEffect> effectProvider) {
        super(cst);
        this.effectProvider = effectProvider;
    }

    @Override
    public ConstellationEffect createEffect(@Nullable ILocatable origin) {
        return this.effectProvider.apply(origin);
    }
}
