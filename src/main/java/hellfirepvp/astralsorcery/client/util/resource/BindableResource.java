package hellfirepvp.astralsorcery.client.util.resource;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BindableResource
 * Created by HellFirePvP
 * Date: 07.05.2016 / 00:50
 */
public class BindableResource {

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

    public SpriteSheetResource asSpriteSheet(int columns, int rows) {
        return new SpriteSheetResource(this, rows, columns);
    }

    @Deprecated
    public void invalidateAndReload() {
        if(resource != null)
            GL11.glDeleteTextures(resource.getGlTextureId());
        resource = null;
    }

    public void allocateGlId() {
        if (resource != null) return;
        resource = new SimpleTexture(new ResourceLocation(path));
        try {
            resource.loadTexture(Minecraft.getMinecraft().getResourceManager());
        } catch (Exception exc) {
            AstralSorcery.log.warn("[AstralSorcery] Failed to load texture " + path);
            AstralSorcery.log.warn("[AstralSorcery] Please report this issue; include the message above, the following stacktrace as well as instructions on how to reproduce this!");
            exc.printStackTrace();
            resource = TextureUtil.MISSING_TEXTURE;
            return;
        }
        AstralSorcery.log.info("[AstralSorcery] Allocated " + path + " to " + resource.getGlTextureId());
    }

    public void bind() {
        if (resource == null) {
            allocateGlId();
        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, resource.getGlTextureId());
        //GlStateManager.bindTexture(resource.getGlTextureId());
    }

}
