/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.mappings;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.lib.Constellations;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientPerkTextureMapping
 * Created by HellFirePvP
 * Date: 23.11.2016 / 15:58
 */
public class ClientPerkTextureMapping {

    private static Map<IMajorConstellation, BindableResource> mapOverlayTextures = new HashMap<>();

    public static void init() {
        mapOverlayTextures.put(Constellations.discidia, AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "overlay_discidia"));
        mapOverlayTextures.put(Constellations.aevitas,  AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "overlay_aevitas"));
        mapOverlayTextures.put(Constellations.vicio,    AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "overlay_vicio"));
        mapOverlayTextures.put(Constellations.armara,   AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "overlay_armara"));
        mapOverlayTextures.put(Constellations.evorsio,  AssetLibrary.loadTexture(AssetLoader.TextureLocation.GUI, "overlay_evorsio"));
    }

    @Nullable
    public static BindableResource getOverlayTexture(IMajorConstellation constellation) {
        return mapOverlayTextures.get(constellation);
    }

}
