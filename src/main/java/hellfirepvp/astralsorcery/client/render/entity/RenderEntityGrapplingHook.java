/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.entity.technical.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

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
    public void render(EntityGrapplingHook entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        int alphaMultiplier;
        if (entity.isDespawning()) {
            alphaMultiplier = MathHelper.clamp(127 - ((int) (entity.despawnPercentage(partialTicks) * 255F)), 0, 255);
        } else {
            alphaMultiplier = 255;
        }
        if (alphaMultiplier <= 1E-4) {
            return;
        }

        Vector3 entityPos = RenderingVectorUtils.interpolatePosition(entity, partialTicks);
        List<Vector3> line = entity.buildLine(partialTicks);

        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        Blending.DEFAULT.apply();
        RenderSystem.disableCull();

        //Main grappling hook sprite
        SpritesAS.SPR_GRAPPLING_HOOK.bindTexture();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            RenderingDrawUtils.renderFacingSpriteVB(buf, matrixStack,
                    entityPos.getX(), entityPos.getY(), entityPos.getZ(),
                    1.3F, 0F,
                    SpritesAS.SPR_GRAPPLING_HOOK, ClientScheduler.getClientTick() + entity.ticksExisted,
                    255, 255, 255, alphaMultiplier);
        });

        //Small line of particles
        TexturesAS.TEX_PARTICLE_LARGE.bindTexture();
        Blending.ADDITIVE_ALPHA.apply();

        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX, buf -> {
            for (Vector3 pos : line) {
                RenderingDrawUtils.renderFacingFullQuadVB(buf, matrixStack,
                        entityPos.getX() + pos.getX(), entityPos.getY() + pos.getY(), entityPos.getZ() + pos.getZ(),
                        0.3F, 0F,
                        50, 40, 180, (int) (alphaMultiplier * 0.8F));
            }
        });

        RenderSystem.enableCull();
        Blending.DEFAULT.apply();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    @Override
    public ResourceLocation getEntityTexture(EntityGrapplingHook entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }

    public static class Factory implements IRenderFactory<EntityGrapplingHook> {

        @Override
        public EntityRenderer<? super EntityGrapplingHook> createRenderFor(EntityRendererManager manager) {
            return new RenderEntityGrapplingHook(manager);
        }
    }
}
