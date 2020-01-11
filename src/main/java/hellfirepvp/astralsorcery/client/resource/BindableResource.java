/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BindableResource
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:50
 */
@OnlyIn(Dist.CLIENT)
public class BindableResource extends AbstractRenderableTexture.Full {

    private ITextureObject resource = null;
    private String path;

    public BindableResource(String path) {
        this.path = path;
        allocateGlId();
    }

    public String getPath() {
        return path;
    }

    public boolean isInitialized() {
        return resource != null;
    }

    public ITextureObject getResource() {
        return resource;
    }

    public SpriteSheetResource asSpriteSheet(int rows, int columns) {
        return new SpriteSheetResource(this, rows, columns);
    }

    void invalidateAndReload() {
        if(resource != null) {
            GlStateManager.deleteTexture(resource.getGlTextureId());
        }
        resource = null;
    }

    private void allocateGlId() {
        if (resource != null || AssetLibrary.isReloading()) return;
        resource = new SimpleTexture(new ResourceLocation(path));
        try {
            resource.loadTexture(Minecraft.getInstance().getResourceManager());
        } catch (Exception exc) {
            AstralSorcery.log.warn("[AssetLibrary] Failed to load texture " + path);
            AstralSorcery.log.warn("[AssetLibrary] Please report this issue; include the message above, the following stacktrace as well as instructions on how to reproduce this!");
            exc.printStackTrace();
            resource = MissingTextureSprite.getDynamicTexture();
        }
    }

    @Override
    public void bindTexture() {
        if (AssetLibrary.isReloading()) {
            return; //we do nothing but wait.
        }
        if (resource == null) {
            allocateGlId();
        }
        GlStateManager.bindTexture(resource.getGlTextureId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BindableResource that = (BindableResource) obj;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
