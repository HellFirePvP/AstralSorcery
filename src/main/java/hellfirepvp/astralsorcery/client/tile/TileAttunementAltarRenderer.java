/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.model.builtin.ModelAttunementAltar;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileAttunementAltarRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileAttunementAltarRenderer implements BlockEntityRenderer<TileAttunementAltar> {

    private final ModelAttunementAltar model;

    public TileAttunementAltarRenderer(EntityModelSet modelSet) {
        this.model = ModelAttunementAltar.bake(modelSet);
    }

    @Override
    public AABB getRenderBoundingBox(TileAttunementAltar blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(1, 2, 1);
    }

    @Override
    public void render(TileAttunementAltar tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        this.model.renderBase(poseStack, bufferSource.getBuffer(RenderTypesAS.MODEL_ATTUNEMENT_ALTAR), packedLight, packedOverlay);
        poseStack.popPose();

        float spinDur = TileAttunementAltar.MAX_START_ANIMATION_SPIN;
        float spinStart = TileAttunementAltar.MAX_START_ANIMATION_TICK;

        float startY = -1.2F;
        float endY   = -0.5F;
        float tickPartY = (endY - startY) / spinStart;
        float prevPosY = endY + (tile.prevActivationTick * tickPartY);
        float posY     = endY + (tile.activationTick     * tickPartY);
        float framePosY = RenderVectorUtil.interpolate(prevPosY, posY, partialTick);

        double generalAnimationTick = (ClientProxy.getClientTick() + partialTick) / 4D;
        if (tile.animate) {
            if (tile.tesrLocked) {
                tile.tesrLocked = false;
            }
        } else {
            if (tile.tesrLocked) {
                generalAnimationTick = 7.25D;
            } else {
                if (Math.abs((generalAnimationTick % spinDur) - 7.25D) <= 0.3125) {
                    generalAnimationTick = 7.25D;
                    tile.tesrLocked = true;
                }
            }
        }

        for (int i = 1; i < 9; i++) {
            float incrementer = (spinDur / 8F) * i;

            double aFrame =     generalAnimationTick + incrementer;
            double prevAFrame = generalAnimationTick + incrementer - 1;
            double renderFrame = RenderVectorUtil.interpolate(prevAFrame, aFrame, 0);

            double partRenderFrame = (renderFrame % spinDur) / spinDur;
            float normalized = (float) (partRenderFrame * 2F * Math.PI);

            float xOffset = Mth.cos(normalized);
            float zOffset = Mth.sin(normalized);
            float rotation = RenderVectorUtil.interpolate(tile.prevActivationTick / spinStart, tile.activationTick / spinStart, partialTick);

            poseStack.pushPose();
            poseStack.translate(0.5, framePosY, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            this.model.renderHoveringToBuffer(poseStack, bufferSource.getBuffer(RenderTypesAS.MODEL_ATTUNEMENT_ALTAR), packedLight, packedOverlay, xOffset, zOffset, rotation);
            poseStack.popPose();
        }
    }
}
