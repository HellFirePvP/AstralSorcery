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
import hellfirepvp.astralsorcery.client.model.builtin.ModelLens;
import hellfirepvp.astralsorcery.common.tile.TileLens;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLensRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLensRenderer implements BlockEntityRenderer<TileLens> {

    private final ModelLens model;

    public TileLensRenderer(EntityModelSet modelSet) {
        this.model = ModelLens.bake(modelSet);
    }

    @Override
    public void render(TileLens tile, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockPos linkedPos = tile.getTileData().getLinkedPos().orElse(null);
        BlockPos from = tile.getBlockPos();

        float degYaw = 0;
        float degPitch = 0;
        poseStack.pushPose();

        if (linkedPos != null) {
            Vector3 dir = new Vector3(linkedPos).subtract(from);

            switch (tile.getPlacedAgainst()) {
                case DOWN, UP -> {
                    degPitch = (float) Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ()));
                    degYaw = (float) Math.atan2(dir.getX(), dir.getZ());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
                case NORTH, SOUTH -> {
                    degPitch = (float) Math.atan2(dir.getZ(), Math.sqrt(dir.getX() * dir.getX() + dir.getY() * dir.getY()));
                    degYaw = (float) Math.atan2(dir.getX(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
                case WEST, EAST -> {
                    degPitch = (float) Math.atan2(dir.getX(), Math.sqrt(dir.getZ() * dir.getZ() + dir.getY() * dir.getY()));
                    degYaw = (float) Math.atan2(dir.getZ(), dir.getY());

                    degYaw = 180F + (float) Math.toDegrees(-degYaw);
                    degPitch = (float) Math.toDegrees(degPitch);
                }
            }
        }

        switch (tile.getPlacedAgainst()) {
            case DOWN -> {
                poseStack.translate(0.5F, 1.5F, 0.5F);

                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(degYaw % 360));
                this.renderLens(poseStack, bufferSource, packedLight, packedOverlay, degPitch);
            }
            case UP -> {
                poseStack.translate(0.5F, -0.5F, 0.5F);

                poseStack.mulPose(Axis.YP.rotationDegrees((-degYaw + 180) % 360));
                this.renderLens(poseStack, bufferSource, packedLight, packedOverlay, -degPitch);
            }
            case NORTH -> {
                poseStack.translate(0.5F, 0.5F, 1.5F);

                poseStack.mulPose(Axis.XP.rotationDegrees(270));
                poseStack.mulPose(Axis.YP.rotationDegrees((-degYaw + 180) % 360));
                this.renderLens(poseStack, bufferSource, packedLight, packedOverlay, degPitch);
            }
            case SOUTH -> {
                poseStack.translate(0.5F, 0.5F, -0.5F);

                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                poseStack.mulPose(Axis.YP.rotationDegrees(degYaw % 360));
                this.renderLens(poseStack, bufferSource, packedLight, packedOverlay, -degPitch);
            }
            case WEST -> {
                poseStack.translate(1.5F, 0.5F, 0.5F);

                poseStack.mulPose(Axis.ZP.rotationDegrees(90));
                poseStack.mulPose(Axis.YP.rotationDegrees((degYaw + 270 % 360)));
                this.renderLens(poseStack, bufferSource, packedLight, packedOverlay, degPitch);
            }
            case EAST -> {
                poseStack.translate(-0.5F, 0.5F, 0.5F);

                poseStack.mulPose(Axis.ZP.rotationDegrees(270));
                poseStack.mulPose(Axis.YP.rotationDegrees((-degYaw + 90 % 360)));
                this.renderLens(poseStack, bufferSource, packedLight, packedOverlay, -degPitch);
            }
        }

        poseStack.popPose();
    }

    private void renderLens(PoseStack renderStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, float pitchDeg) {
        this.model.rotateLens(pitchDeg);
        this.model.renderModel(renderStack, buffer, combinedLight, combinedOverlay);
    }
}
