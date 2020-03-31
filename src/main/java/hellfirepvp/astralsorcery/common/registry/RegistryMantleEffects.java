/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.mantle.MantleEffect;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.*;

import static hellfirepvp.astralsorcery.common.lib.MantleEffectsAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryMantleEffects
 * Created by HellFirePvP
 * Date: 17.02.2020 / 20:35
 */
public class RegistryMantleEffects {

    private RegistryMantleEffects() {}

    public static void init() {
        AEVITAS = register(new MantleEffectAevitas());
        ARMARA = register(new MantleEffectArmara());
        BOOTES = register(new MantleEffectBootes());
        DISCIDIA = register(new MantleEffectDiscidia());
        EVORSIO = register(new MantleEffectEvorsio());
        FORNAX = register(new MantleEffectFornax());
        HOROLOGIUM = register(new MantleEffectHorologium());
        LUCERNA = register(new MantleEffectLucerna());
        MINERALIS = register(new MantleEffectMineralis());
        OCTANS = register(new MantleEffectOctans());
        PELOTRIO = register(new MantleEffectPelotrio());
        VICIO = register(new MantleEffectVicio());
    }

    private static <T extends MantleEffect> T register(T effect) {
        AstralSorcery.getProxy().getRegistryPrimer().register(effect);
        return effect;
    }
}
