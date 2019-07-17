/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.registry;

import hellfirepvp.astralsorcery.client.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;

import static hellfirepvp.astralsorcery.client.lib.TexturesAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryTextures
 * Created by HellFirePvP
 * Date: 07.07.2019 / 11:03
 */
public class RegistryTextures {

    private RegistryTextures() {}

    public static void loadTextures() {
        TEX_BLACK = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "black");

        TEX_STATIC_FLARE = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flarestatic");
        TEX_CRYSTAL_EFFECT_1 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect1");
        TEX_CRYSTAL_EFFECT_2 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect2");
        TEX_CRYSTAL_EFFECT_3 = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "ceffect3");
        TEX_COLLECTOR_EFFECT = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "burst2");

        TEX_LIGHTNING_PART = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightningpart");
        TEX_LIGHTBEAM = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam");
    }

}
