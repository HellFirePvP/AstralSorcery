/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.entities.EntitySpectralTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderSpectralTool
 * Created by HellFirePvP
 * Date: 11.10.2017 / 21:14
 */
public class RenderSpectralTool extends Render<EntitySpectralTool> {

    protected RenderSpectralTool(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntitySpectralTool entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemStack is = entity.getItem();
        if(is.isEmpty()) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.2, z);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.rotate(-entityYaw - 90, 0, 1, 0);
        if(is.getItem() instanceof ItemAxe) {
            GlStateManager.rotate(180, 1, 0, 0);
            GlStateManager.rotate(270, 0, 0, 1);
        }

        IBakedModel bakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(is, null, null);
        bakedModel = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(bakedModel, ItemCameraTransforms.TransformType.GROUND, false);

        TextureManager textureManager = Minecraft.getMinecraft().renderEngine;
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.001F);
        GlStateManager.enableBlend();
        Blending.CONSTANT_ALPHA.applyStateManager();
        GlStateManager.disableCull();

        RenderingUtils.tryRenderItemWithColor(is, bakedModel, new Color(0x4A27B0), 0.7F);

        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Blending.DEFAULT.applyStateManager();
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntitySpectralTool entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntitySpectralTool> {

        @Override
        public Render<? super EntitySpectralTool> createRenderFor(RenderManager manager) {
            return new RenderSpectralTool(manager);
        }

    }
}
