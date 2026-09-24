/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AtlasSpriteTexture
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AtlasSpriteTexture extends AbstractRenderTexture {

    private final TextureAtlasSprite sprite;

    protected AtlasSpriteTexture(TextureAtlasSprite sprite) {
        super(NameUtil.suffixPath(sprite.atlasLocation(), String.format("/%s_%s", sprite.getU0(), sprite.getV0()).replace(".", "_")));
        this.sprite = sprite;
    }

    public static AtlasSpriteTexture fromAtlasSprite(TextureAtlasSprite sprite) {
        return new AtlasSpriteTexture(sprite);
    }

    @Override
    public void bindTexture() {
        RenderSystem.setShaderTexture(0, this.sprite.atlasLocation());
    }

    @Override
    public RenderStateShard.TextureStateShard asState() {
        return new RenderStateShard.TextureStateShard(this.getKey(), false, false) {
            @Override
            public void setupRenderState() {
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                texturemanager.getTexture(AtlasSpriteTexture.this.sprite.atlasLocation()).setFilter(this.blur, this.mipmap);
                AtlasSpriteTexture.this.bindTexture();
            }
        };
    }

    @Override
    public UVFrame getUV() {
        return UVFrame.fromAtlasSprite(this.sprite);
    }

    @Override
    public void invalidateAndReload() {
        // No-op, minecraft reloads atlases
    }
}
