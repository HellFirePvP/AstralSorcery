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
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.*;
import hellfirepvp.astralsorcery.common.entity.EntityAltarFluidInput;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderEntityAltarFluidInput
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderEntityAltarFluidInput extends EntityRenderer<EntityAltarFluidInput> {

    public RenderEntityAltarFluidInput(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EntityAltarFluidInput entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        float filledPerc = entity.getDrawnFilledPercentage();
        float cubeScale = 0.05F + filledPerc * 0.3F;

        this.drawFluidCube(entity, partialTick, cubeScale, poseStack, bufferSource, packedLight);

        entity.resolveClientColor().ifPresent(color -> {
            poseStack.pushPose();
            poseStack.translate(0, 0.12F, 0);
            RenderLightFanUtil.renderLightFan(poseStack, 160420L + entity.hashCode(), entity.tickCount, partialTick, bufferSource,
                    color.getColor(), 16 * cubeScale * 3, 12, 15);
            poseStack.popPose();
        });
    }

    private void drawFluidCube(EntityAltarFluidInput entity, float partialTick, float scale, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        FluidStack contained = entity.getFluid();
        if (contained.isEmpty()) return;
        TextureAtlasSprite sprite = RenderSpriteUtil.getTexture(contained);
        Vector3 rotation = RenderVectorUtil.interpolate(entity.getPrevRotation(), entity.getRotation(), partialTick);

        int light = contained.getFluid().getFluidType().getLightLevel(contained);
        int blockLight = packedLight >> 4 & 0xF;
        packedLight = LightmapUtil.getPackedLightCoords(packedLight >> 20 & 0xF, Math.max(blockLight, light));

        int tint = IClientFluidTypeExtensions.of(contained.getFluid()).getTintColor(contained);
        ColorWrapper color = ColorWrapper.opaque(tint);
        VertexConsumer vb = bufferSource.getBuffer(RenderTypesAS.TER_CHALICE_LIQUID);

        float ulen = sprite.getU1() - sprite.getU0();
        float vlen = sprite.getV1() - sprite.getV0();
        float uPart = ulen * scale;
        float vPart = vlen * scale;
        float uOffset = sprite.getU0() + ulen * 0.5F - uPart * 0.5F;
        float vOffset = sprite.getV0() + vlen * 0.5F - vPart * 0.5F;
        UVFrame uv = new UVFrame(uOffset, vOffset, uPart, vPart);

        poseStack.pushPose();
        poseStack.translate(0, entity.getDimensions(entity.getPose()).height() / 2, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.getX()));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.getY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.getZ()));
        poseStack.scale(scale, scale, scale);

        RenderCubeUtil.drawCentredCube(vb, poseStack, uv, color, packedLight, OverlayTexture.NO_OVERLAY);
        RenderUtil.finishDrawing(bufferSource);

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(EntityAltarFluidInput entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
