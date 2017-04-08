/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.common.entities.EntityIlluminationSpark;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityIlluminationSpark
 * Created by HellFirePvP
 * Date: 08.04.2017 / 00:28
 */
public class RenderEntityIlluminationSpark extends Render<EntityIlluminationSpark> {

    public RenderEntityIlluminationSpark(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityIlluminationSpark entity, double x, double y, double z, float entityYaw, float partialTicks) {}

    @Override
    protected ResourceLocation getEntityTexture(EntityIlluminationSpark entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityIlluminationSpark> {

        @Override
        public Render<EntityIlluminationSpark> createRenderFor(RenderManager manager) {
            return new RenderEntityIlluminationSpark(manager);
        }

    }

}
