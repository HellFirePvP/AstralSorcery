package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.client.models.tcn.TCNModelTelescope;
import hellfirepvp.astralsorcery.client.render.RenderEntityModel;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityTelescope
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:30
 */
public class RenderEntityTelescope<T extends EntityTelescope> extends RenderEntityModel<T> {

    private static final TCNModelTelescope telescopeModel = new TCNModelTelescope();
    private static final BindableResource texTelescope = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MODELS, "telescope");

    protected RenderEntityTelescope(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y + 2.38, z);
        GL11.glScaled(0.1, 0.1, 0.1);
        GL11.glRotated(180, 1, 0, 0);
        doModelRender(entity);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public void doModelRender(T entity) {
        texTelescope.bind();
        telescopeModel.render(entity, 0, 0, 0, 0, 0, 1);
    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

    public static class Factory extends RenderEntityModelFactory<EntityTelescope> {

        @Override
        public RenderEntityTelescope<? super EntityTelescope> createRenderFor(RenderManager manager) {
            return new RenderEntityTelescope<>(manager);
        }

    }

}
