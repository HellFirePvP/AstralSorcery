/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectCorruptedCelestialCrystal;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CorruptedCelestialCrystalProvider
 * Created by HellFirePvP
 * Date: 19.04.2019 / 11:15
 */
public class CorruptedCelestialCrystalProvider implements EffectProvider<PtEffectCorruptedCelestialCrystal> {

    @Override
    public PtEffectCorruptedCelestialCrystal buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        PatreonEffectHelper.FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(1));
        }
        return new PtEffectCorruptedCelestialCrystal(uniqueId, fc);
    }

}
