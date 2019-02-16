/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectTreeBeacon;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeBeaconEffectProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 18:42
 */
public class TreeBeaconEffectProvider implements EffectProvider<PtEffectTreeBeacon> {

    @Override
    public PtEffectTreeBeacon buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        PatreonEffectHelper.FlareColor flareColor = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(0));
        int overlay = Integer.parseInt(effectParameters.get(1));
        int drain = Integer.parseInt(effectParameters.get(2));
        int tree = Integer.parseInt(effectParameters.get(3));
        return new PtEffectTreeBeacon(flareColor)
                        .setOverlayColor(overlay)
                        .setDrainColor(drain)
                        .setTreeColor(tree);
    }

}
