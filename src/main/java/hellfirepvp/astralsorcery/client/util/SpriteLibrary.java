package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpriteLibrary
 * Created by HellFirePvP
 * Date: 26.09.2016 / 00:03
 */
public class SpriteLibrary {

    public static SpriteSheetResource spriteLightbeam = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "lightbeam").asSpriteSheet(16, 4);
    public static SpriteSheetResource spriteHalo = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "halo1").asSpriteSheet(8, 6);

    public static void init() {}

}
