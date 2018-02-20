/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import hellfirepvp.astralsorcery.client.ClientScheduler;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.SpriteLibrary;
import hellfirepvp.astralsorcery.client.util.TextureHelper;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.entities.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderEntityHook
 * Created by HellFirePvP
 * Date: 24.06.2017 / 22:10
 */
public class RenderEntityHook extends Render<EntityGrapplingHook> {

    private static BindableResource texConn;

    public RenderEntityHook(RenderManager renderManager) {
        super(renderManager);
        this.shadowSize = 0F;
    }

    @Override
    public void doRender(EntityGrapplingHook entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if(texConn == null) {
            texConn = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flaresmall");
        }

        float alphaMultiplier = 1;
        if(entity.isDespawning()) {
            alphaMultiplier = Math.max(0, 0.5F - entity.despawnPercentage(partialTicks));
        }
        if(alphaMultiplier <= 1E-4) {
            return;
        }

        Tessellator tes = Tessellator.getInstance();
        BufferBuilder bb = tes.getBuffer();

        GlStateManager.disableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Blending.DEFAULT.applyStateManager();
        GlStateManager.disableCull();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Vector3 iPosE = RenderingUtils.interpolatePosition(entity, partialTicks);


        SpriteSheetResource sprite = SpriteLibrary.spriteHook;
        sprite.getResource().bind();
        Tuple<Double, Double> currentUV = sprite.getUVOffset(ClientScheduler.getClientTick() + entity.ticksExisted);
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        RenderingUtils.renderFacingQuadVB(bb, iPosE.getX(), iPosE.getY(), iPosE.getZ(), partialTicks, 1.3F, 0F,
                currentUV.key, currentUV.value, sprite.getULength(), sprite.getVLength(), 1F, 1F, 1F, alphaMultiplier);
        tes.draw();

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.popMatrix();

        List<Vector3> drawPoints = entity.buildPoints(partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        Blending.ADDITIVE_ALPHA.applyStateManager();

        texConn.bind();

        GlStateManager.disableCull();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        GlStateManager.color(1F, 1F, 1F, 1F);
        for (Vector3 pos : drawPoints) {
            RenderingUtils.renderFacingQuadVB(bb, iPosE.getX() + pos.getX(), iPosE.getY() + pos.getY(), iPosE.getZ() + pos.getZ(),
                    partialTicks, 0.25F, 0, 0, 0, 1, 1,
                    0.2F, 0.15F, 0.7F, 0.8F * alphaMultiplier);
        }
        GlStateManager.enableCull();
        tes.draw();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        Blending.DEFAULT.applyStateManager();

        GlStateManager.enableAlpha();
        TextureHelper.refreshTextureBindState();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGrapplingHook entity) {
        return null;
    }

    public static class Factory implements IRenderFactory<EntityGrapplingHook> {

        @Override
        public Render<? super EntityGrapplingHook> createRenderFor(RenderManager manager) {
            return new RenderEntityHook(manager);
        }

    }
}
