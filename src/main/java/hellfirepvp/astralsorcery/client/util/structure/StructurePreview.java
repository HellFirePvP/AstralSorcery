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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.structure.MatchableStructure;
import hellfirepvp.observerlib.api.tile.MatchableTile;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import hellfirepvp.observerlib.api.util.StructureUtil;
import hellfirepvp.observerlib.client.util.LightmapUtil;
import hellfirepvp.observerlib.client.util.SimpleBossInfo;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;

import java.util.*;
import java.util.function.BiPredicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StructurePreview
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StructurePreview {

    private final Map<BlockPos, BlockState> states = new HashMap<>();
    private final Map<BlockPos, BlockEntity> tiles = new HashMap<>();
    private final BlockPos center;
    private final ResourceKey<Level> levelKey;
    private final MatchableStructure structure;

    private BiPredicate<Level, BlockPos> persistenceTest = (level, pos) -> true;
    private Component structureBarName = Component.empty();
    private SimpleBossInfo structureBar = null;

    private StructurePreview(BlockPos center, Level level, MatchableStructure structure) {
        this.center = center;
        this.levelKey = level.dimension();
        this.structure = structure;
        this.buildContents(level.registryAccess());

        this.persistenceTest = this.persistenceTest.and((lvl, pos) -> lvl.dimension().equals(this.levelKey));
    }

    private void buildContents(RegistryAccess registryAccess) {
        structure.getContents().forEach((relative, matchState) -> {
            BlockState state = matchState.getDescriptiveState(0L);
            this.states.put(relative, state);
            if (state.hasBlockEntity() && state.getBlock() instanceof EntityBlock entityBlock) {
                BlockEntity tile = entityBlock.newBlockEntity(center.offset(relative), state);
                this.tiles.put(relative, tile);

                MatchableTile matchTile = structure.getTileEntityAt(relative);
                if (matchTile == null) return;
                CompoundTag tag = new CompoundTag();
                tile.saveWithoutMetadata(registryAccess);
                matchTile.writeDisplayData(tile, 0L, tag);
                tile.loadWithComponents(tag, registryAccess);
            }
        });
    }

    private boolean isInRenderDistance(BlockPos position) {
        Vec3i size = this.structure.getMaximumOffset().subtract(this.structure.getMinimumOffset());
        int length = Math.max(Math.max(size.getX(), size.getY()), size.getZ());
        double distanceSq = Math.max(100, length * length);
        distanceSq *= 1.5F;
        distanceSq = Math.min(distanceSq, Minecraft.getInstance().gameRenderer.getRenderDistance() * Minecraft.getInstance().gameRenderer.getRenderDistance());
        return this.center.distSqr(position) <= distanceSq;
    }

    public boolean canRender(Level renderLevel, BlockPos playerPos) {
        return renderLevel.dimension().equals(this.levelKey) && this.isInRenderDistance(playerPos);
    }

    public boolean canPersist(Level renderLevel, BlockPos playerPos) {
        return this.persistenceTest.test(renderLevel, playerPos) && this.isInRenderDistance(playerPos);
    }

    public void removed() {
        if (this.structureBar != null) {
            this.structureBar.removeInfo();
            this.structureBar = null;
        }
    }

    public void tick(Level renderLevel, BlockPos playerPos) {
        if (!this.structureBarName.getString().isBlank()) {
            if (this.canPersist(renderLevel, playerPos)) {
                if (this.structureBar == null) {
                    this.structureBar = SimpleBossInfo.create(this.structureBarName, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
                    this.structureBar.displayInfo();
                }

                float progress = StructureUtil.getMismatches(this.structure, renderLevel, this.center).size() / (float) this.structure.getContents().size();
                this.structureBar.setProgress(1F - progress);
            } else {
                if (this.structureBar != null) {
                    this.structureBar.removeInfo();
                    this.structureBar = null;
                }
            }
        }
    }

    public void render(Camera camera, float pTicks) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        Optional<Integer> sliceOpt = StructureUtil.getLowestMismatchingSlice(this.structure, level, this.center);
        if (sliceOpt.isEmpty()) return;
        int slice = sliceOpt.get();

        Quaternionf quaternionf = camera.rotation().conjugate(new Quaternionf());
        Matrix4f frustumMatrix = new Matrix4f().rotation(quaternionf);

        Matrix4fStack viewStack = RenderSystem.getModelViewStack();
        viewStack.pushMatrix();
        viewStack.mul(frustumMatrix);
        RenderSystem.applyModelViewMatrix();

        PoseStack pose = new PoseStack();
        Vec3 cameraPos = camera.getPosition();
        int csX = (int) Math.floor(cameraPos.x());
        int csY = (int) Math.floor(cameraPos.y());
        int csZ = (int) Math.floor(cameraPos.z());
        Vec3i offsetCamera = new Vec3i(csX, csY, csZ);
        Vec3 partialCamera = cameraPos.subtract(csX, csY, csZ);

        Level renderLevel = StructureDisplayLevel.empty(level.registryAccess());
        MultiBufferSource.BufferSource drawBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
        RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();

        WrappedBufferSource depthChainBuffers = new WrappedBufferSource(drawBuffer,
                renderType -> DrawChainRenderType.wrap("structure_", renderType, ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTargetSupplier(),
                        RenderSystem::disableDepthTest, RenderSystem::enableDepthTest));

        renderPreviewMatches(renderLevel, level, pose, slice, offsetCamera, partialCamera, player.blockPosition(),
                transparencyTarget, depthChainBuffers, pTicks, true);
        renderPreviewMatches(renderLevel, level, pose, slice, offsetCamera, partialCamera, player.blockPosition(),
                transparencyTarget, depthChainBuffers, pTicks, false);

        viewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    private void renderPreviewMatches(Level renderLevel,
                                      Level actualLevel,
                                      PoseStack pose,
                                      int renderSlice,
                                      Vec3i offsetCamera,
                                      Vec3 partialCamera,
                                      BlockPos playerPos,
                                      RenderTarget transparencyTarget,
                                      WrappedBufferSource buffers,
                                      float pTicks,
                                      boolean matching) {
        ColorWrapper color = (matching ? ColorWrapper.WHITE : ColorWrapper.opaque(0xFF0000)).copyWithAlpha(0x99);
        ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(color);
        ShaderProgramsAS.TRANSPARENCY_COLOR.setIgnoreDepth(true);

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        BlockEntityRenderDispatcher tesrMgr = Minecraft.getInstance().getBlockEntityRenderDispatcher();

        RenderUtil.withTarget(Minecraft.getInstance().getMainRenderTarget(), particleTarget -> {
            transparencyTarget.clear(Minecraft.ON_OSX);
            RenderUtil.safeCopyDepth(transparencyTarget, particleTarget);

            List<BlockPos> offsets = new ArrayList<>(this.states.keySet());
            offsets.sort(Comparator.comparing(pos -> pos.offset(this.center).distSqr(playerPos)));
            offsets.reversed().forEach(relative -> {
                if (relative.getY() != renderSlice) return;

                BlockState expected = this.states.get(relative);
                if (expected == null || expected.isAir()) return;

                BlockPos absolute = relative.offset(this.center);
                BlockState actual = actualLevel.getBlockState(absolute);
                MatchableState stateMatch = this.structure.getBlockStateAt(relative);
                if (stateMatch.matches(actualLevel, absolute, actual)) {
                    return;
                }
                if (!matching && actualLevel.isEmptyBlock(absolute)) {
                    return;
                }

                Vec3i at = absolute.subtract(offsetCamera);
                float size = 0.8F;
                float offset = (1F - size) / 2F;

                pose.pushPose();
                pose.translate(at.getX(), at.getY(), at.getZ());
                pose.translate(-partialCamera.x(), -partialCamera.y(), -partialCamera.z());
                pose.translate(offset, offset, offset);
                pose.scale(size, size, size);

                brd.renderSingleBlock(expected, pose, buffers,
                        LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null);

                FluidState fluidState = expected.getFluidState();
                if (!fluidState.isEmpty()) {
                    VertexConsumer buf = buffers.getBuffer(ItemBlockRenderTypes.getRenderLayer(fluidState));
                    brd.renderLiquid(BlockPos.ZERO, renderLevel, buf, expected, fluidState);

                }

                BlockEntity tile = this.tiles.get(relative);
                if (tile != null) {
                    BlockEntityRenderer tesr = tesrMgr.getRenderer(tile);
                    if (tesr != null) {
                        tesr.render(tile, pTicks, pose, buffers, LightmapUtil.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY);
                    }
                }

                pose.popPose();
            });

            buffers.end();
            ShaderProgramsAS.TRANSPARENCY_COLOR.redirect(particleTarget, chain -> chain.process(pTicks));
            RenderUtil.safeCopyDepth(particleTarget, transparencyTarget);
        });
    }

    public static class Builder {

        private final StructurePreview preview;

        Builder(Level level, BlockPos center, MatchableStructure structure) {
            this.preview = new StructurePreview(center, level, structure);
        }

        public Builder persistIf(BiPredicate<Level, BlockPos> test) {
            this.preview.persistenceTest = this.preview.persistenceTest.and(test);
            return this;
        }

        public Builder showBar(Component name) {
            this.preview.structureBarName = name;
            return this;
        }

        public void createAndDisplay() {
            StructurePreviewHelper.setCurrentPreview(this.preview);
        }
    }
}
