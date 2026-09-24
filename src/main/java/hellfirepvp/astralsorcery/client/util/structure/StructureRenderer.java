/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.structure;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.api.structure.Structure;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.model.data.ModelData;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructureRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StructureRenderer {

    private final Structure structure;
    private final StructureDisplayLevel level;

    private Vector3 rotation = new Vector3();
    private float scale = 1F;
    private Optional<Integer> ySlice = Optional.empty();

    public StructureRenderer(RegistryAccess registries, StructureBlockArray structure) {
        this.structure = structure;
        BoundingBox structureBox = BoundingBox.fromCorners(structure.getMinimumOffset(), structure.getMaximumOffset());
        this.level = new StructureDisplayLevel(registries, structureBox);
        structure.place(this.level, BlockPos.ZERO);
        int maxLength = Math.max(Math.max(structureBox.getXSpan(), structureBox.getYSpan()), structureBox.getZSpan());
        this.scale = 10F * (10F / maxLength);

        this.resetRotation();
    }

    public final Structure getStructure() {
        return this.structure;
    }

    protected void resetRotation() {
        this.rotation = new Vector3(-30F, 45F, 0F);
    }

    public void rotate(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    public void rotateFromMouseDrag(float mouseDX, float mouseDZ) {
        this.rotate(0.5F * -mouseDZ, 0.5F * mouseDX, 0.0F);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }

    public void zoom(float scrollDelta) {
        this.setScale(Mth.clamp(this.getScale() + (scrollDelta * 0.4F), 1.5F, 20F));
    }

    public int getDefaultSlice() {
        return this.structure.getMinimumOffset().getY();
    }

    public void switchToSliceRender(int slice) {
        this.ySlice = Optional.of(Mth.clamp(slice, this.structure.getMinimumOffset().getY(), this.structure.getMaximumOffset().getY()));
    }

    public void switchToFullRender() {
        this.ySlice = Optional.empty();
    }

    public boolean hasSlice(int y) {
        return y >= this.structure.getMinimumOffset().getY() && y <= this.structure.getMaximumOffset().getY();
    }

    public boolean rendersAsSlice() {
        return this.ySlice.isPresent();
    }

    public int getCurrentSlice() {
        return this.ySlice.orElse(this.getDefaultSlice());
    }

    public void render(PoseStack pose, float x, float y, float pTicks) {
        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        BlockEntityRenderDispatcher tesrMgr = Minecraft.getInstance().getBlockEntityRenderDispatcher();

        pose.pushPose();
        this.ySlice.ifPresent(slice -> this.level.getStorage().pushFilter(pos -> pos.getY() == slice));
        pose.translate(x, y, 256);

        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.XP.rotationDegrees((float) this.rotation.getX()));
        pose.mulPose(Axis.YP.rotationDegrees((float) this.rotation.getY()));
        pose.mulPose(Axis.ZP.rotationDegrees((float) this.rotation.getZ()));
        pose.scale(this.scale, -this.scale, this.scale);
        pose.translate(-0.5, -0.5, -0.5);

        this.getStructure().getContents().keySet().forEach(pos -> {
            BlockState state = this.level.getBlockState(pos);
            if (state.is(Blocks.AIR)) return;

            pose.pushPose();
            pose.translate(pos.getX(), pos.getY(), pos.getZ());
            this.level.getStorage().pushFilter(at -> at.equals(pos));

            brd.renderSingleBlock(state, pose, buffers, LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

            FluidState fluidState = state.getFluidState();
            if (!fluidState.isEmpty()) {
                VertexConsumer buf = buffers.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState));
                brd.renderLiquid(pos, this.level, buf, state, fluidState);
            }

            this.level.getStorage().popFilter();

            pose.popPose();
        });
        RenderUtil.finishDrawing(buffers);

        this.getStructure().getContents().keySet().forEach(pos -> {
            BlockEntity tile = this.level.getBlockEntity(pos);
            if (tile == null) return;

            BlockEntityRenderer tesr = tesrMgr.getRenderer(tile);
            if (tesr == null) return;

            pose.pushPose();
            pose.translate(pos.getX(), pos.getY(), pos.getZ());
            this.level.getStorage().pushFilter(at -> at.equals(pos));
            tesr.render(tile, pTicks, pose, buffers, LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY);
            this.level.getStorage().popFilter();
            pose.popPose();
        });
        RenderUtil.finishDrawing(buffers);

        this.ySlice.ifPresent(slice -> this.level.getStorage().popFilter());
        pose.popPose();
    }
}
