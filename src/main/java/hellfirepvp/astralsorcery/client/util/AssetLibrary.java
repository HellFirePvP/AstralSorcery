package hellfirepvp.astralsorcery.client.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AssetLibrary
 * Created by HellFirePvP
 * Date: 09.08.2016 / 11:00
 */
public class AssetLibrary {

    private static Map<AssetLoader.SubLocation, Map<String, BindableResource>> loadedTextures = new HashMap<>();

    public static BindableResource loadTexture(AssetLoader.TextureLocation location, String name) {
        if(name.endsWith(".png")) {
            throw new IllegalArgumentException("Tried to loadTexture with appended .png from the AssetLibrary!");
        }
        if(!loadedTextures.containsKey(location)) {
            loadedTextures.put(location, new HashMap<>());
        }
        Map<String, BindableResource> resources = loadedTextures.get(location);
        if(resources.containsKey(name)) {
            return resources.get(name);
        }
        BindableResource res = AssetLoader.load(AssetLoader.AssetLocation.TEXTURES, location, name, ".png");
        resources.put(name, res);
        return res;
    }

}
