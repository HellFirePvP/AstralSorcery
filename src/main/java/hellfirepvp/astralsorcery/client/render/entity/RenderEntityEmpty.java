/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

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
    public void render(Entity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {}

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<Entity> {

        @Override
        public EntityRenderer<? super Entity> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityEmpty(manager);
        }
    }

}
