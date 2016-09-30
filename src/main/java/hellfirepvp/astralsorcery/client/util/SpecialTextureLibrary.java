package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SpecialTextureLibrary
 * Created by HellFirePvP
 * Date: 30.09.2016 / 16:31
 */
public class SpecialTextureLibrary {

    public static ResourceLocation texFontRenderer = new ResourceLocation("textures/font/ascii.png");

    public static ResourceLocation getBlockAtlasTexture() {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static ResourceLocation getMissingTexture() {
        return TextureMap.LOCATION_MISSING_TEXTURE;
    }

}
