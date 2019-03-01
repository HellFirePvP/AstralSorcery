/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteQuery;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectFixedSprite;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;
import hellfirepvp.astralsorcery.common.util.data.Vector3;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpecificPatreonHaloEffectProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 18:48
 */
public class SpecificPatreonHaloEffectProvider implements EffectProvider<PtEffectFixedSprite> {

    @Override
    public PtEffectFixedSprite buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        PatreonEffectHelper.FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(1));
        }
        return new PtEffectFixedSprite(
                uniqueId,
                fc,
                new SpriteQuery(AssetLoader.TextureLocation.EFFECT, "halo3", 4, 8)
        ).setPositionFunction(player ->
                Vector3.atEntityCenter(player).setY((player.posY + 48 < 256) ? player.posY + 48 : 256));
    }

}
