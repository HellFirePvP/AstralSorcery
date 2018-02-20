/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.entities.EntityStarburst;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityStarburst
 * Created by HellFirePvP
 * Date: 12.03.2017 / 10:51
 */
public class RenderEntityStarburst extends Render<EntityStarburst> {

    public RenderEntityStarburst(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityStarburst entity, double x, double y, double z, float entityYaw, float partialTicks) {}

    @Override
    protected ResourceLocation getEntityTexture(EntityStarburst entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityStarburst> {

        @Override
        public Render<EntityStarburst> createRenderFor(RenderManager manager) {
            return new RenderEntityStarburst(manager);
        }

    }

}
