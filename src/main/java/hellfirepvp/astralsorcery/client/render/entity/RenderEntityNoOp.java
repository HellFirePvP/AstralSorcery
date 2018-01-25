/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityNoOp
 * Created by HellFirePvP
 * Date: 03.07.2017 / 13:36
 */
public class RenderEntityNoOp<T extends Entity> extends Render<T> {

    public RenderEntityNoOp(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {}

    @Override
    protected ResourceLocation getEntityTexture(T entity) {
        return null;
    }

    public static class Factory<T extends Entity> implements IRenderFactory<T> {

        @Override
        public Render<T> createRenderFor(RenderManager manager) {
            return new RenderEntityNoOp<>(manager);
        }

    }

}
