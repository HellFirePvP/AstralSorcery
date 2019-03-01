/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.entities.EntityShootingStar;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityShootingStar
 * Created by HellFirePvP
 * Date: 20.10.2018 / 16:12
 */
public class RenderEntityShootingStar extends Render<EntityShootingStar> {

    public RenderEntityShootingStar(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityShootingStar entity, double x, double y, double z, float entityYaw, float partialTicks) {}

    @Override
    protected ResourceLocation getEntityTexture(EntityShootingStar entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityShootingStar> {

        @Override
        public Render<EntityShootingStar> createRenderFor(RenderManager manager) {
            return new RenderEntityShootingStar(manager);
        }

    }

}
