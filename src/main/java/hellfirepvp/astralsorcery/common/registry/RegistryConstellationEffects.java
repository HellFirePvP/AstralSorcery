/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.effect.provider.CEffectProviderVicio;

import static hellfirepvp.astralsorcery.common.lib.ConstellationEffectsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryConstellationEffects
 * Created by HellFirePvP
 * Date: 27.07.2019 / 15:09
 */
public class RegistryConstellationEffects {

    private RegistryConstellationEffects() {}

    public static void init() {
        VICIO = register(new CEffectProviderVicio());
    }

    private static <T extends ConstellationEffectProvider> T register(T effectProvider) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effectProvider);
        return effectProvider;
    }

}
