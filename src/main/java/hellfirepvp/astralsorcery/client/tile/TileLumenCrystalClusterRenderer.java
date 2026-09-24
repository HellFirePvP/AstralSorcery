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
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.tile.TileLumenCrystalCluster;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.DisplayRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TileLumenCrystalClusterRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TileLumenCrystalClusterRenderer implements BlockEntityRenderer<TileLumenCrystalCluster> {

    private final BlockRenderDispatcher brd;

    public TileLumenCrystalClusterRenderer(BlockRenderDispatcher brd) {
        this.brd = brd;
    }

    @Override
    public void render(TileLumenCrystalCluster blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        BlockState state = blockEntity.getBlockState();
        BakedModel model = this.brd.getBlockModel(state);
        ModelData modelData = model.getModelData(level, blockEntity.getBlockPos(), state, ModelData.EMPTY);
        int packedColor = Minecraft.getInstance().getBlockColors().getColor(state, level, blockEntity.getBlockPos(), 0);
        ModelBlockRenderer renderer = this.brd.getModelRenderer();

        model.getRenderTypes(state, RandomSource.create(42), modelData).forEach(type -> {
            renderer.renderModel(poseStack.last(), bufferSource.getBuffer(RenderTypeHelper.getEntityRenderType(type, false)), state, model,
                    (packedColor >> 16 & 255) / 255F, (packedColor >> 8 & 255) / 255F, (packedColor & 255) / 255F,
                    packedLight, packedOverlay, modelData, type);
        });
    }
}
