/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.MoonPhase;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MoonPhaseRenderHelper
 * Created by HellFirePvP
 * Date: 01.08.2016 / 00:53
 */
public class MoonPhaseRenderHelper {

    private static Map<MoonPhase, BindableResource> moonPhaseIcons = new HashMap<>();

    public static BindableResource getMoonPhaseTexture(MoonPhase phase) {
        return moonPhaseIcons.get(phase);
    }

    static {
        moonPhaseIcons.put(MoonPhase.FULL,      AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_full"));
        moonPhaseIcons.put(MoonPhase.WANING3_4, AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning1"));
        moonPhaseIcons.put(MoonPhase.WANING1_2, AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning2"));
        moonPhaseIcons.put(MoonPhase.WANING1_4, AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning3"));
        moonPhaseIcons.put(MoonPhase.NEW,       AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_new"));
        moonPhaseIcons.put(MoonPhase.WAXING1_4, AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing1"));
        moonPhaseIcons.put(MoonPhase.WAXING1_2, AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing2"));
        moonPhaseIcons.put(MoonPhase.WAXING3_4, AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing3"));
    }

}
