/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TextureHelper
 * Created by HellFirePvP
 * Date: 30.09.2016 / 16:31
 */
public class TextureHelper {

    private static final ResourceLocation blackSpaceholder = new ResourceLocation(AstralSorcery.MODID, "textures/misc/black.png");
    public static ResourceLocation texFontRenderer = new ResourceLocation("textures/font/ascii.png");

    public static ResourceLocation getBlockAtlasTexture() {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }

    public static void refreshTextureBindState() {
        Minecraft.getMinecraft().renderEngine.bindTexture(blackSpaceholder);
    }

    public static void setActiveTextureToAtlasSprite() {
        Minecraft.getMinecraft().renderEngine.bindTexture(getBlockAtlasTexture());
    }

}
