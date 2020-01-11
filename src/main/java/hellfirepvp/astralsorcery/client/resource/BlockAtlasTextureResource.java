/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import hellfirepvp.astralsorcery.client.util.draw.TextureHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockAtlasTextureResource
 * Created by HellFirePvP
 * Date: 10.11.2019 / 10:21
 */
public class BlockAtlasTextureResource extends AbstractRenderableTexture.Full {

    private static final BlockAtlasTextureResource INSTANCE = new BlockAtlasTextureResource();

    private BlockAtlasTextureResource() {}

    public static BlockAtlasTextureResource getInstance() {
        return INSTANCE;
    }

    @Override
    public void bindTexture() {
        TextureHelper.bindBlockAtlas();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
