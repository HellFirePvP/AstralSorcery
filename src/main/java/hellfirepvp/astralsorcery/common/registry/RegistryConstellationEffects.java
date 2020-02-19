/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffect;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectProvider;
import hellfirepvp.astralsorcery.common.constellation.effect.aoe.*;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;
import hellfirepvp.astralsorcery.common.util.block.ILocatable;

import javax.annotation.Nullable;
import java.util.function.Function;

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
        AEVITAS    = register(makeProvider(ConstellationsAS.aevitas,    CEffectAevitas::new));
        ARMARA     = register(makeProvider(ConstellationsAS.armara,     CEffectArmara::new));
        BOOTES     = register(makeProvider(ConstellationsAS.bootes,     CEffectBootes::new));
        DISCIDIA   = register(makeProvider(ConstellationsAS.discidia,   CEffectDiscidia::new));
        EVORSIO    = register(makeProvider(ConstellationsAS.evorsio,    CEffectEvorsio::new));
        FORNAX     = register(makeProvider(ConstellationsAS.fornax,     CEffectFornax::new));
        HOROLOGIUM = register(makeProvider(ConstellationsAS.horologium, CEffectHorologium::new));
        LUCERNA    = register(makeProvider(ConstellationsAS.lucerna,    CEffectLucerna::new));
        MINERALIS  = register(makeProvider(ConstellationsAS.mineralis,  CEffectMineralis::new));
        OCTANS     = register(makeProvider(ConstellationsAS.octans,     CEffectOctans::new));
        PELOTRIO   = register(makeProvider(ConstellationsAS.pelotrio,   CEffectPelotrio::new));
        VICIO      = register(makeProvider(ConstellationsAS.vicio,      CEffectVicio::new));
    }

    private static ConstellationEffectProvider makeProvider(IWeakConstellation cst, Function<ILocatable, ? extends ConstellationEffect> effectProvider) {
        return new ConstellationEffectProvider(cst) {
            @Override
            public ConstellationEffect createEffect(@Nullable ILocatable origin) {
                return effectProvider.apply(origin);
            }
        };
    }

    private static <T extends ConstellationEffectProvider> T register(T effectProvider) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effectProvider);
        return effectProvider;
    }
}
