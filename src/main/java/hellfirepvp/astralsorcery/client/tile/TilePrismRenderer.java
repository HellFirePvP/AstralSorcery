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
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TilePrism;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TilePrismRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TilePrismRenderer implements BlockEntityRenderer<TilePrism> {

    @Override
    public void render(TilePrism prism, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        BlockState render = BlocksAS.STARLIGHT_FOCUS_ROCK_CRYSTAL.get().defaultBlockState();
        float scale = 0.4F;
        long tick = ClientProxy.getClientTick() + MiscUtil.getBlockPosSeed(prism.getBlockPos());

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(scale, scale, scale);
        float spin = (tick % 2000) / 2000F * 2 * Mth.PI;
        poseStack.mulPose(Axis.YP.rotation(spin));
        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.translate(0, Mth.sin((tick % 150) / 150F * 2 * Mth.PI) * 0.03F, 0);

        brd.renderSingleBlock(render, poseStack, bufferSource, packedLight, packedOverlay, ModelData.EMPTY, null);

        poseStack.popPose();
    }
}
