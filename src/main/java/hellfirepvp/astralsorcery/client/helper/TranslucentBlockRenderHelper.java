/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.helper;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TranslucentBlockRenderHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TranslucentBlockRenderHelper {

    private static final Map<DyeColor, List<BlockRenderable>> colorFrameRenderables = new HashMap<>();

    public static void renderFrame(float pTicks) {
        MultiBufferSource.BufferSource drawBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();

        RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();
        WrappedBufferSource chainBuffers = new WrappedBufferSource(drawBuffer,
                renderType -> DrawChainRenderType.wrap("translucent_block_", renderType, transparencyTarget));

        colorFrameRenderables.forEach((dyeColor, renders) -> {
            if (renders.isEmpty()) return;

            ColorWrapper color = ColorsAS.DYE_COLORS[dyeColor.getId()].copyWithAlpha(0x99);
            ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(color);

            RenderUtil.withTarget(Minecraft.getInstance().levelRenderer.getParticlesTarget(), particleTarget -> {
                transparencyTarget.clear(Minecraft.ON_OSX);
                RenderUtil.safeCopyDepth(transparencyTarget, particleTarget);
                renders.forEach(render -> {
                    brd.renderSingleBlock(render.state(), render.pose(), chainBuffers,
                            LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);
                });
                chainBuffers.end();
                ShaderProgramsAS.TRANSPARENCY_COLOR.redirect(particleTarget, chain -> chain.process(pTicks));
                RenderUtil.safeCopyDepth(particleTarget, transparencyTarget);
            });
        });

        colorFrameRenderables.clear();
    }

    private static final float OVERLAY_SCALE = 1.02F;
    private static final float OVERLAY_OFFSET = -(OVERLAY_SCALE - 1.0F) / 2.0F;

    public static void submitBlockState(DyeColor color, BlockState state, BlockPos pos, Vec3 cameraPos) {
        PoseStack pose = new PoseStack();
        pose.translate(pos.getX() - cameraPos.x, pos.getY() - cameraPos.y, pos.getZ() - cameraPos.z);
        colorFrameRenderables.computeIfAbsent(color, c -> new ArrayList<>()).add(new BlockRenderable(state, pose));
    }

    public static void submitBlockOverlay(DyeColor color, BlockState state, BlockPos pos, Vec3 cameraPos) {
        PoseStack pose = new PoseStack();
        pose.translate(
                pos.getX() - cameraPos.x + OVERLAY_OFFSET,
                pos.getY() - cameraPos.y + OVERLAY_OFFSET,
                pos.getZ() - cameraPos.z + OVERLAY_OFFSET);
        pose.scale(OVERLAY_SCALE, OVERLAY_SCALE, OVERLAY_SCALE);
        colorFrameRenderables.computeIfAbsent(color, c -> new ArrayList<>()).add(new BlockRenderable(state, pose));
    }

    public static void submitBlockState(DyeColor color, BlockState state, PoseStack pose) {
        PoseStack copy = new PoseStack();
        copy.mulPose(pose.last().pose());
        colorFrameRenderables.computeIfAbsent(color, c -> new ArrayList<>()).add(new BlockRenderable(state, copy));
    }

    private record BlockRenderable(BlockState state, PoseStack pose) {}
}
