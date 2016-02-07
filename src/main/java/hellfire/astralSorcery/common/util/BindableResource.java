package hellfire.astralSorcery.common.util;

import hellfire.astralSorcery.common.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 07.02.2016 20:07
 */
public class BindableResource {

    private ITextureObject resource = null;
    private String path;

    public BindableResource(String path) {
        this.path = path;
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

    public void bind() {
        if(resource == null) {
            resource = new SimpleTexture(new ResourceLocation(path));
            try {
                resource.loadTexture(Minecraft.getMinecraft().getResourceManager());
            } catch (Exception exc) {
                AstralSorcery.log.warn("Failed to load texture " + path);
                resource = TextureUtil.missingTexture;
            }
        }
        GlStateManager.bindTexture(resource.getGlTextureId());
    }
}
