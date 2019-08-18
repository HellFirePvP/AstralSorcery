/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityEmpty
 * Created by HellFirePvP
 * Date: 17.08.2019 / 13:08
 */
public class RenderEntityEmpty extends EntityRenderer<Entity> {

    public RenderEntityEmpty(EntityRendererManager mgr) {
        super(mgr);
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float pTicks) { }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<Entity> {

        @Override
        public EntityRenderer<? super Entity> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityEmpty(manager);
        }
    }

}
