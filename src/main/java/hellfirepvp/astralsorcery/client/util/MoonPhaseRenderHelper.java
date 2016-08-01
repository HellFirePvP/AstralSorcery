package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.constellation.CelestialHandler;

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

    private static Map<CelestialHandler.MoonPhase, BindableResource> moonPhaseIcons = new HashMap<>();

    public static BindableResource getMoonPhaseTexture(CelestialHandler.MoonPhase phase) {
        return moonPhaseIcons.get(phase);
    }

    static {
        moonPhaseIcons.put(CelestialHandler.MoonPhase.FULL,      AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_full"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.WANING3_4, AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning1"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.WANING1_2, AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning2"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.WANING1_4, AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waning3"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.NEW,       AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_new"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.WAXING1_4, AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing1"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.WAXING1_2, AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing2"));
        moonPhaseIcons.put(CelestialHandler.MoonPhase.WAXING3_4, AssetLoader.loadTexture(AssetLoader.TextureLocation.MISC, "moon_waxing3"));
    }

}
