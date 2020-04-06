/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.types.provider;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.base.patreon.FlareColor;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectProvider;
import hellfirepvp.astralsorcery.common.base.patreon.types.TypeFlareCrystal;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProviderFlareCrystal
 * Created by HellFirePvP
 * Date: 05.04.2020 / 17:16
 */
public class ProviderFlareCrystal implements PatreonEffectProvider<TypeFlareCrystal> {

    @Override
    public TypeFlareCrystal buildEffect(UUID playerUUID, List<String> effectParameters) throws Exception {
        UUID uniqueId = UUID.fromString(effectParameters.get(0));
        FlareColor fc = null;
        if (!"null".equals(effectParameters.get(1))) {
            fc = FlareColor.valueOf(effectParameters.get(1));
        }
        int colorTheme = Integer.parseInt(effectParameters.get(2));
        String modelTexture = effectParameters.get(3);
        if (modelTexture.equalsIgnoreCase("crystal_big_magenta")) {
            modelTexture = "crystal_magenta"; //Remap to 1.14 texture name
        }
        return new TypeFlareCrystal(uniqueId, fc,
                new Color(colorTheme),
                new TextureQuery(AssetLoader.TextureLocation.MODEL, modelTexture));
    }
}
