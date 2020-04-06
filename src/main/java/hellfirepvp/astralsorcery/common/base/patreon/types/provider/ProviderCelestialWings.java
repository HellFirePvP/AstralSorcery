/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types.provider;

import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectProvider;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeCelestialWings;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProviderCelestialWings
 * Created by HellFirePvP
 * Date: 05.04.2020 / 08:26
 */
public class ProviderCelestialWings implements PatreonEffectProvider<TypeCelestialWings> {
    @Override
    public TypeCelestialWings buildEffect(UUID playerUUID, List<String> effectParameters) throws Exception {
        UUID effectUUID = UUID.fromString(effectParameters.get(0));
        FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = FlareColor.valueOf(effectParameters.get(1));
        }
        return new TypeCelestialWings(effectUUID, fc, playerUUID);
    }
}
