/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectDynColorFlare;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynFlareEffectProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 18:34
 */
public class DynFlareEffectProvider implements EffectProvider<PtEffectDynColorFlare> {

    @Override
    public PtEffectDynColorFlare buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        return new PtEffectDynColorFlare(uniqueId,
                () -> Color.getHSBColor(PtEffectDynColorFlare.getClientTick() % 360 / 360F, 1F, 1F));
    }

}
