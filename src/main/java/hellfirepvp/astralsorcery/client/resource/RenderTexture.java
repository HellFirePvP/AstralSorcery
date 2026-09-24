/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderTexture
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderTexture extends AbstractRenderTexture.Full implements ReloadableResource {

    private AbstractTexture resource = null;

    protected RenderTexture(ResourceLocation key) {
        super(key);
    }

    protected AbstractTexture allocateResource() {
        if (AssetLibrary.isReloading()) {
            return null;
        }
        TextureManager mgr = Minecraft.getInstance().getTextureManager();
        AbstractTexture resource = mgr.getTexture(this.getKey(), null);
        if (resource != null) {
            return resource;
        }
        mgr.register(this.getKey(), new SimpleTexture(this.getKey()));
        return mgr.getTexture(this.getKey(), MissingTextureAtlasSprite.getTexture());
    }

    @Override
    public void invalidateAndReload() {
        Minecraft.getInstance().getTextureManager().release(this.getKey());
        this.resource = null;
    }

    @Override
    public void bindTexture() {
        if (AssetLibrary.isReloading()) {
            return; //we do nothing but wait.
        }
        if (this.resource == null) {
            this.resource = this.allocateResource();
        }
        if (this.resource == null) {
            return;
        }
        RenderSystem.setShaderTexture(0, this.resource.getId());
    }

    @Override
    public RenderStateShard.TextureStateShard asState() {
        return new RenderStateShard.TextureStateShard(this.getKey(), false, false) {
            @Override
            public void setupRenderState() {
                RenderTexture.this.bindTexture();
                RenderTexture.this.resource.setBlurMipmap(false, false);
            }
        };
    }
}
