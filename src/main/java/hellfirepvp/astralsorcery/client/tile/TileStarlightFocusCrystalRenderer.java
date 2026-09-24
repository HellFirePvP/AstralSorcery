/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.helper.TranslucentBlockRenderHelper;
import hellfirepvp.astralsorcery.client.util.RenderLightFanUtil;
import hellfirepvp.astralsorcery.common.tile.TileLightwell;
import hellfirepvp.astralsorcery.common.tile.TileStarlightFocusCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileStarlightFocusCrystalRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileStarlightFocusCrystalRenderer implements BlockEntityRenderer<TileStarlightFocusCrystal> {

    @Override
    public AABB getRenderBoundingBox(TileStarlightFocusCrystal blockEntity) {
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).inflate(1, 5, 1);
    }

    @Override
    public void render(TileStarlightFocusCrystal focusCrystal, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!focusCrystal.doesSeeSky()) return;

        focusCrystal.getTileData().getConstellation().ifPresent(cst -> {
            ColorWrapper color = cst.getConstellationColor();
            long effectSeed = MiscUtil.getBlockPosSeed(focusCrystal.getBlockPos());
            float scale = focusCrystal.getTileData().isOnFocalNode() ? 1F : 0.6F;

            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            RenderLightFanUtil.renderLightFan(poseStack, effectSeed, (int) focusCrystal.getTileData().getTicksExisted(),
                    partialTick, bufferSource, color.getColor(), 32 * scale, 12 * scale, 18);
            poseStack.popPose();
        });
    }
}
