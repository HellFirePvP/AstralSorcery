/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.entity.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;
import java.awt.*;

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
    public void doRender(EntitySpectralTool entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty() || !entity.isAlive()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y + entity.getHeight() / 2, z);
        GlStateManager.rotatef(-entityYaw - 90, 0, 1, 0);
        if (stack.getItem() instanceof AxeItem) {
            GlStateManager.rotatef(180, 1, 0, 0);
            GlStateManager.rotatef(270, 0, 0, 1);
        }
        GlStateManager.disableCull();

        RenderingUtils.renderTranslucentItemStackModel(stack, ColorsAS.SPECTRAL_TOOL, Blending.CONSTANT_ALPHA, 0.25F);

        GlStateManager.enableCull();

        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySpectralTool entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntitySpectralTool> {

        @Override
        public EntityRenderer<? super EntitySpectralTool> createRenderFor(EntityRendererManager manager) {
            return new RenderEntitySpectralTool(manager);
        }
    }
}
