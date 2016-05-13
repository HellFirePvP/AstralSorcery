package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.entities.EntityTelescope;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityTelescope
 * Created by HellFirePvP
 * Date: 08.05.2016 / 23:30
 */
public class RenderEntityTelescope<T extends EntityTelescope> extends Render<T> {

    protected RenderEntityTelescope(RenderManager renderManager) {
        super(renderManager);
    }

    //TODO later.

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {

    }

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null; //LUL null.
    }

    public static class Factory implements IRenderFactory<EntityTelescope> {

        @Override
        public Render<? super EntityTelescope> createRenderFor(RenderManager manager) {
            return new RenderEntityTelescope<>(manager);
        }

    }

}
