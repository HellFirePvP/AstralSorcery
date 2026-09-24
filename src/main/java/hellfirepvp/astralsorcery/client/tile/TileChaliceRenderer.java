/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderCubeUtil;
import hellfirepvp.astralsorcery.client.util.RenderSpriteUtil;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.tile.TileChalice;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileChaliceRenderer
 * Created by HellFirePvP
 * Date: 11.04.2026
 */
public class TileChaliceRenderer implements BlockEntityRenderer<TileChalice> {

    private static final float MIN_SIZE = 0.125F;
    private static final float SIZE_RANGE = 0.375F;
    private static final float CENTER_Y = 1.4F;

    public TileChaliceRenderer() {}

    @Override
    public AABB getRenderBoundingBox(TileChalice blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(0, 1, 0);
    }

    @Override
    public void render(TileChalice tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        FluidStack contained = tile.getContainedFluid();
        if (contained.isEmpty()) {
            return;
        }
        TextureAtlasSprite sprite = RenderSpriteUtil.getTexture(contained);

        float fillPercent = tile.getTankFillPercentage();
        float cubeScale = MIN_SIZE + fillPercent * SIZE_RANGE;

        Vector3 rotation = RenderVectorUtil.interpolate(tile.getPrevRotation(), tile.getRotation(), partialTick);

        int light = contained.getFluid().getFluidType().getLightLevel(contained);
        int blockLight = packedLight >> 4 & 0xF;
        packedLight = LightmapUtil.getPackedLightCoords(packedLight >> 20 & 0xF, Math.max(blockLight, light));

        int tint = IClientFluidTypeExtensions.of(contained.getFluid()).getTintColor(contained);
        ColorWrapper color = ColorWrapper.opaque(tint);
        VertexConsumer vb = bufferSource.getBuffer(RenderTypesAS.TER_CHALICE_LIQUID);

        float ulen = sprite.getU1() - sprite.getU0();
        float vlen = sprite.getV1() - sprite.getV0();
        float uPart = ulen * cubeScale;
        float vPart = vlen * cubeScale;
        float uOffset = sprite.getU0() + ulen * 0.5F - uPart * 0.5F;
        float vOffset = sprite.getV0() + vlen * 0.5F - vPart * 0.5F;
        UVFrame uv = new UVFrame(uOffset, vOffset, uPart, vPart);

        poseStack.pushPose();
        poseStack.translate(0.5F, CENTER_Y, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotation.getX()));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotation.getY()));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotation.getZ()));
        poseStack.scale(cubeScale, cubeScale, cubeScale);

        RenderCubeUtil.drawCentredCube(vb, poseStack, uv, color, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
