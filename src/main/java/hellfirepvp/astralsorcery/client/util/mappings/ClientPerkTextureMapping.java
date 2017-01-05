/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.mappings;

import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;

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

    //TODO wiiv, in case you wanna do the overlay-texture things, those go here. you gotta restart the client when adding ones here.
    public static void init() {
        //mapOverlayTextures.put(Constellations.evorsio, AssetLibrary.loadTexture(AssetLoader.TextureLocation.ENVIRONMENT, "red"));
    }

    @Nullable
    public static BindableResource getOverlayTexture(IMajorConstellation constellation) {
        return mapOverlayTextures.get(constellation);
    }

}
