/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BindableResource
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:50
 */
@OnlyIn(Dist.CLIENT)
public class BindableResource extends AbstractRenderableTexture.Full implements ReloadableResource {

    private Texture resource = null;
    private String path = null;

    protected BindableResource(ResourceLocation key) {
        super(key);
    }

    BindableResource(String path) {
        this(AstralSorcery.key(path.replaceAll("[^a-zA-Z0-9\\.\\-]", "_")));
        this.path = path;
        allocateGlId();
    }

    public String getPath() {
        return path;
    }

    public SpriteSheetResource asSpriteSheet(int rows, int columns) {
        return new SpriteSheetResource(this, rows, columns);
    }

    public void invalidateAndReload() {
        Minecraft.getInstance().getTextureManager().deleteTexture(this.getKey());
        this.resource = null;
    }

    protected Texture allocateGlId() {
        if (AssetLibrary.isReloading()) {
            return null;
        }
        TextureManager mgr = Minecraft.getInstance().getTextureManager();
        Texture resource = mgr.getTexture(this.getKey());
        if (resource != null) {
            return resource;
        }
        mgr.loadTexture(this.getKey(), new SimpleTexture(new ResourceLocation(this.getPath())));
        return mgr.getTexture(this.getKey());
    }

    @Override
    public void bindTexture() {
        if (AssetLibrary.isReloading()) {
            return; //we do nothing but wait.
        }
        if (this.resource == null) {
            this.resource = allocateGlId();
        }
        if (this.resource == null) {
            return;
        }
        RenderSystem.bindTexture(this.resource.getGlTextureId());
    }

    @Override
    public RenderState.TextureState asState() {
        return new RenderState.TextureState(this.getKey(), false, false) {
            @Override
            public void setupRenderState() {
                RenderSystem.enableTexture();
                BindableResource.this.bindTexture();
                BindableResource.this.resource.setBlurMipmap(false, false);
            }
        };
    }
}
