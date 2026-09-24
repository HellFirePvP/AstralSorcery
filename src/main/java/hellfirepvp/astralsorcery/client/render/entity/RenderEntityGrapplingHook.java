/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.entity.EntityGrapplingHook;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import org.joml.Quaternionf;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderEntityGrapplingHook
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderEntityGrapplingHook extends EntityRenderer<EntityGrapplingHook> {

    public RenderEntityGrapplingHook(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityGrapplingHook entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        int alphaMultiplier;
        if (entity.isDespawning()) {
            float alphaPart = 1F - entity.despawnPercentage(partialTick) * 2;
            alphaMultiplier = Mth.clamp((int) (alphaPart * 255F), 0, 255);
        } else {
            alphaMultiplier = 255;
        }
        if (alphaMultiplier <= 1E-4) {
            return;
        }

        List<Vector3> line = entity.buildLine(partialTick);
        //List<Vector3> lineStars = entity.buildLine(partialTick, -0.02F);
        SpriteSheet spr = SpritesAS.SPRITE_GRAPPLING_HOOK;
        Quaternionf facing = new Quaternionf();
        facing.set(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());

        VertexConsumer buf = bufferSource.getBuffer(RenderTypesAS.ENTITY_GRAPPLING_HOOK);
        RenderingDrawUtil.renderFacingQuadPosed(buf, poseStack, facing, new Vector3(),
                ColorWrapper.WHITE.copyWithAlpha(alphaMultiplier), 1F, LightmapUtil.getPackedFullbrightCoords(),
                spr.getUV(ClientProxy.getClientTick() + entity.tickCount));

        VertexConsumer lineBuf = bufferSource.getBuffer(RenderTypesAS.ENTITY_GRAPPLING_HOOK_LINE);
        int lineAlpha = (int) (alphaMultiplier * 0.8F);
        line.forEach(pos -> {
            RenderingDrawUtil.renderFacingQuadPosed(lineBuf, poseStack, facing, pos,
                    ColorsAS.ENTITY_GRAPPLING_HOOK_LINE.copyWithAlpha(lineAlpha), 0.4F, LightmapUtil.getPackedFullbrightCoords(),
                    UVFrame.FULL);
        });

        RenderUtil.finishDrawing(bufferSource);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityGrapplingHook entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
