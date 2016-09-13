package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.entities.EntityGrindstone;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityGrindstone
 * Created by HellFirePvP
 * Date: 13.09.2016 / 13:15
 */
public class RenderEntityGrindstone<T extends EntityGrindstone> extends Render<T> {

    protected RenderEntityGrindstone(RenderManager renderManager) {
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

    public static class Factory implements IRenderFactory<EntityGrindstone> {

        @Override
        public Render<? super EntityGrindstone> createRenderFor(RenderManager manager) {
            return new RenderEntityGrindstone<>(manager);
        }

    }
}
