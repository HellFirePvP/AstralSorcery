/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectCrystalFootprint;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CrystalFootprintProvider
 * Created by HellFirePvP
 * Date: 01.03.2019 / 17:37
 */
public class CrystalFootprintProvider implements EffectProvider<PtEffectCrystalFootprint> {

    @Override
    public PtEffectCrystalFootprint buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        PatreonEffectHelper.FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(1));
        }
        Color color = new Color(Integer.parseInt(effectParameters.get(2)));
        return new PtEffectCrystalFootprint(uniqueId, fc, uuid, color);
    }

}
