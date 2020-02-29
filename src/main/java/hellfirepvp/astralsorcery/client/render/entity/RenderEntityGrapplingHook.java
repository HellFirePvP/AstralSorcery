/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityGrapplingHook
 * Created by HellFirePvP
 * Date: 29.02.2020 / 20:04
 */
public class RenderEntityGrapplingHook extends EntityRenderer<EntityGrapplingHook> {

    protected RenderEntityGrapplingHook(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityGrapplingHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float alphaMultiplier = 1;
        if (entity.isDespawning()) {
            alphaMultiplier = Math.max(0, 0.5F - entity.despawnPercentage(partialTicks));
        }
        if (alphaMultiplier <= 1E-4) {
            return;
        }

        Vector3 entityPos = RenderingVectorUtils.interpolatePosition(entity, partialTicks);
        List<Vector3> line = entity.buildLine(partialTicks);

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder buf = tes.getBuffer();

        GlStateManager.disableAlphaTest();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableCull();

        //Main grappling hook sprite
        SpritesAS.SPR_GRAPPLING_HOOK.bindTexture();

        GlStateManager.pushMatrix();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        RenderingDrawUtils.renderFacingSpriteVB(buf,
                entityPos.getX(), entityPos.getY(), entityPos.getZ(),
                partialTicks, 1.3F, 0F,
                SpritesAS.SPR_GRAPPLING_HOOK, ClientScheduler.getClientTick() + entity.ticksExisted,
                255F, 255F, 255F, alphaMultiplier);
        tes.draw();
        GlStateManager.popMatrix();

        //Small line of particles
        TexturesAS.TEX_PARTICLE_LARGE.bindTexture();

        Blending.ADDITIVE_ALPHA.applyStateManager();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        for (Vector3 pos : line) {
            RenderingDrawUtils.renderFacingFullQuadVB(buf,
                    entityPos.getX() + pos.getX(), entityPos.getY() + pos.getY(), entityPos.getZ() + pos.getZ(),
                    partialTicks, 0.3F, 0F,
                    50F, 40F, 180F, 0.8F * alphaMultiplier);
        }
        tes.draw();

        Blending.DEFAULT.applyStateManager();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableAlphaTest();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGrapplingHook entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityGrapplingHook> {

        @Override
        public EntityRenderer<? super EntityGrapplingHook> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityGrapplingHook(manager);
        }
    }
}
