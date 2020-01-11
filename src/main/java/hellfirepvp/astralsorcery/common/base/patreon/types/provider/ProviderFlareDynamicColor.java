/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types.provider;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectProvider;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeFlareColor;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProviderFlareDynamicColor
 * Created by HellFirePvP
 * Date: 31.08.2019 / 11:17
 */
public class ProviderFlareDynamicColor implements PatreonEffectProvider<TypeFlareColor> {
    @Override
    public TypeFlareColor buildEffect(UUID playerUUID, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        return new TypeFlareColor(uniqueId,
                () -> Color.getHSBColor(ClientScheduler.getClientTick() % 360 / 360F, 1F, 1F));
    }
}
