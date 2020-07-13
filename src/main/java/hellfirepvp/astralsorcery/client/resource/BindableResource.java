/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

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
public class BindableResource extends AbstractRenderableTexture.Full {

    private Texture resource = null;
    private String path;

    public BindableResource(String path) {
        super(AstralSorcery.key(path.replaceAll("[^a-zA-Z0-9\\.\\-]", "_")));
        this.path = path;
        allocateGlId();
    }

    public String getPath() {
        return path;
    }

    public boolean isInitialized() {
        return this.resource != null;
    }

    public Texture getResource() {
        return this.resource;
    }

    public SpriteSheetResource asSpriteSheet(int rows, int columns) {
        return new SpriteSheetResource(this, rows, columns);
    }

    void invalidateAndReload() {
        Minecraft.getInstance().getTextureManager().deleteTexture(this.getKey());
        this.resource = null;
    }

    private void allocateGlId() {
        if (this.resource != null || AssetLibrary.isReloading()) {
            return;
        }
        TextureManager mgr = Minecraft.getInstance().getTextureManager();
        this.resource = mgr.getTexture(this.getKey());
        if (this.resource == null) {
            mgr.loadTexture(this.getKey(), new SimpleTexture(new ResourceLocation(this.getPath())));
            this.resource = mgr.getTexture(this.getKey());
        }
    }

    @Override
    public void bindTexture() {
        if (AssetLibrary.isReloading()) {
            return; //we do nothing but wait.
        }
        if (this.resource == null) {
            allocateGlId();
        }
        this.resource.bindTexture();
    }

    @Override
    public RenderState.TextureState asState() {
        return new RenderState.TextureState(this.getKey(), false, false);
    }
}
