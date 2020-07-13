/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntitySpectralTool
 * Created by HellFirePvP
 * Date: 22.02.2020 / 14:28
 */
public class RenderEntitySpectralTool extends EntityRenderer<EntitySpectralTool> {

    protected RenderEntitySpectralTool(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(EntitySpectralTool entity, float entityYaw, float partialTicks, MatrixStack renderStack, IRenderTypeBuffer buffer, int packedLight) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty() || !entity.isAlive()) {
            return;
        }

        renderStack.push();
        renderStack.translate(0, entity.getHeight() / 2, 0);
        renderStack.rotate(Vector3f.YP.rotationDegrees(-entityYaw - 90));
        if (stack.getItem() instanceof AxeItem) {
            renderStack.rotate(Vector3f.XP.rotationDegrees(180));
            renderStack.rotate(Vector3f.ZP.rotationDegrees(270));
        }

        RenderingUtils.renderTranslucentItemStackModel(stack, renderStack, ColorsAS.SPECTRAL_TOOL, Blending.CONSTANT_ALPHA, 63);

        renderStack.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(EntitySpectralTool entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntitySpectralTool> {

        @Override
        public EntityRenderer<? super EntitySpectralTool> createRenderFor(EntityRendererManager manager) {
            return new RenderEntitySpectralTool(manager);
        }
    }
}
