/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon.type.provider;

import hellfirepvp.astralsorcery.common.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffectProvider;
import hellfirepvp.astralsorcery.common.patreon.type.TypeFlare;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TypeFlareProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TypeFlareProvider implements PatreonEffectProvider<TypeFlare> {

    @Override
    public TypeFlare buildEffect(UUID playerUUID, List<String> parameters) throws Exception {
        UUID effectUUID = this.getEffectUUID(parameters);
        FlareColor flareColor = FlareColor.valueOf(parameters.get(1));
        return new TypeFlare(effectUUID, flareColor);
    }
}
