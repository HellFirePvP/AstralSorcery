/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data.provider;

import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.TextureQuery;
import hellfirepvp.astralsorcery.common.base.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.base.patreon.base.PtEffectFloatingFlareCrystal;
import hellfirepvp.astralsorcery.common.base.patreon.data.EffectProvider;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FloatingFlareCrystalProvider
 * Created by HellFirePvP
 * Date: 16.02.2019 / 18:38
 */
public class FloatingFlareCrystalProvider implements EffectProvider<PtEffectFloatingFlareCrystal> {

    @Override
    public PtEffectFloatingFlareCrystal buildEffect(UUID uuid, List<String> effectParameters) throws Exception {
        PatreonEffectHelper.FlareColor flareColor = PatreonEffectHelper.FlareColor.valueOf(effectParameters.get(0));
        int colorTheme = Integer.parseInt(effectParameters.get(1));
        String modelTexture = effectParameters.get(2);
        return new PtEffectFloatingFlareCrystal(
                new Color(colorTheme), flareColor,
                new TextureQuery(AssetLoader.TextureLocation.MODELS, modelTexture));
    }

}
