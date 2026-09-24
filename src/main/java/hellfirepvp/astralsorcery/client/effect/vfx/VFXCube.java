/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.function.FXAlphaFunction;
import hellfirepvp.astralsorcery.client.effect.function.FXColorFunction;
import hellfirepvp.astralsorcery.client.lib.ShaderProgramsAS;
import hellfirepvp.astralsorcery.client.shader.DrawChainRenderType;
import hellfirepvp.astralsorcery.client.shader.WrappedBufferSource;
import hellfirepvp.astralsorcery.client.util.RenderUtil;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXCube
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXCube extends EntityVisualFX {

    private BlockState renderState = Blocks.AIR.defaultBlockState();
    private ModelData modelData = ModelData.EMPTY;
    private RenderType stateRenderType = null;

    private Vector3 rotationDegreeAxis = new Vector3();
    private Vector3 prevRotationDegreeAxis = new Vector3();
    private Vector3 rotationChange = new Vector3();
    private float rotationChangeSpeed = 1F;

    public VFXCube(Vector3 pos) {
        super(pos);
    }

    public VFXCube setRenderState(Block block) {
        return this.setRenderState(block.defaultBlockState());
    }

    public VFXCube setRenderState(BlockState renderState) {
        this.renderState = renderState;
        return this;
    }

    public VFXCube setModelData(ModelData modelData) {
        this.modelData = modelData;
        return this;
    }

    public VFXCube setStateRenderType(RenderType stateRenderType) {
        this.stateRenderType = stateRenderType;
        return this;
    }

    public VFXCube tumble() {
        this.rotationDegreeAxis = Vector3.positiveYRandom(this.rand).multiply(360);
        this.prevRotationDegreeAxis = this.rotationDegreeAxis.copy();
        this.rotationChange = Vector3.random(this.rand).multiply(12);
        return this;
    }

    public VFXCube setRotationChangeSpeed(float rotationChangeSpeed) {
        this.rotationChangeSpeed = rotationChangeSpeed;
        return this;
    }

    public Vector3 getInterpolatedRotation(float pTicks) {
        return new Vector3(
                RenderVectorUtil.interpolate(prevRotationDegreeAxis.getX(), rotationDegreeAxis.getX(), pTicks),
                RenderVectorUtil.interpolate(prevRotationDegreeAxis.getY(), rotationDegreeAxis.getY(), pTicks),
                RenderVectorUtil.interpolate(prevRotationDegreeAxis.getZ(), rotationDegreeAxis.getZ(), pTicks));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.rotationChangeSpeed > 0 && this.rotationChange.lengthSquared() > 0) {
            Vector3 degAxis = this.rotationDegreeAxis.copy();
            Vector3 modify = this.rotationChange.copy().multiply(rotationChangeSpeed);
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.copy();
            this.rotationDegreeAxis.add(modify);

            Vector3 newDegAxis = this.rotationDegreeAxis;
            newDegAxis.setX(newDegAxis.getX() % 360D).setY(newDegAxis.getY() % 360D).setZ(newDegAxis.getZ() % 360D);
            if (!degAxis.add(modify).equals(newDegAxis)) {
                this.prevRotationDegreeAxis = this.rotationDegreeAxis.copy().subtract(modify);
            }
        } else {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.copy();
        }
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {
        throw new UnsupportedOperationException("Cannot render cube batched");
    }

    protected void renderCube(Camera renderInfo, MultiBufferSource bufferSource, float pTicks) {
        if (this.renderState.getBlock() == Blocks.AIR) return;
        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();

        Vector3 relativePos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks).subtract(renderInfo.getPosition());
        Vector3 rotation = this.getInterpolatedRotation(pTicks);
        float fScale = this.getScale(pTicks);
        int packedLight = this.getLight(pTicks);

        PoseStack pose = new PoseStack();
        pose.translate(relativePos.getX(), relativePos.getY(), relativePos.getZ());
        pose.mulPose(Axis.XP.rotationDegrees((float) rotation.getX()));
        pose.mulPose(Axis.YP.rotationDegrees((float) rotation.getY()));
        pose.mulPose(Axis.ZP.rotationDegrees((float) rotation.getZ()));
        pose.scale(fScale, fScale, fScale);

        brd.renderSingleBlock(this.renderState, pose, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, this.modelData, this.stateRenderType);
    }

    public static class IndividualTemplate<T extends VFXCube> extends EffectTemplate<T> {

        public IndividualTemplate(RenderType renderType, Function<Vector3, T> particleCreator) {
            super(renderType, true, particleCreator);
        }

        @Override
        public void renderAll(List<T> effects, Camera renderInfo, MultiBufferSource.BufferSource drawBuffer, float pTicks) {
            RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();
            WrappedBufferSource chainBuffers = new WrappedBufferSource(drawBuffer,
                    renderType -> DrawChainRenderType.wrap("fx_cube_immediate_", renderType, transparencyTarget));

            RenderUtil.withTarget(Minecraft.getInstance().levelRenderer.getParticlesTarget(), particleTarget -> {
                effects.forEach(effect -> {
                    transparencyTarget.clear(Minecraft.ON_OSX);
                    ColorWrapper color = effect.getColor(pTicks).copyWithAlpha(effect.getAlphaI(pTicks));
                    ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(color);
                    RenderUtil.safeCopyDepth(transparencyTarget, particleTarget);
                    effect.renderCube(renderInfo, chainBuffers, pTicks);
                    chainBuffers.end();
                    ShaderProgramsAS.TRANSPARENCY_COLOR.redirect(particleTarget, chain -> {
                        chain.process(pTicks);
                    });
                    RenderUtil.safeCopyDepth(particleTarget, transparencyTarget);
                });
            });
        }
    }

    public static class BatchedTemplate<T extends VFXCube> extends EffectTemplate<T> {

        private float alpha = 1F;
        private FXColorFunction colorFn = FXColorFunction.WHITE;
        private FXAlphaFunction alphaFn = FXAlphaFunction.CONSTANT;

        public BatchedTemplate(RenderType renderType, Function<Vector3, T> particleCreator) {
            super(renderType, true, particleCreator);
        }

        public BatchedTemplate<T> setAlpha(float alpha) {
            this.alpha = alpha;
            return this;
        }

        public BatchedTemplate<T> color(FXColorFunction colorFn) {
            this.colorFn = colorFn;
            return this;
        }

        public BatchedTemplate<T> alpha(FXAlphaFunction alphaFn) {
            this.alphaFn = alphaFn;
            return this;
        }

        @Override
        public void renderAll(List<T> effects, Camera renderInfo, MultiBufferSource.BufferSource drawBuffer, float pTicks) {
            RenderTarget transparencyTarget = ShaderProgramsAS.TRANSPARENCY_COLOR.getTransparencyTarget().orElseThrow();
            WrappedBufferSource chainBuffers = new WrappedBufferSource(drawBuffer,
                    renderType -> DrawChainRenderType.wrap("fx_cube_batch_", renderType, transparencyTarget));

            VFXCube first = effects.stream().findFirst().orElseThrow();
            ColorWrapper color = this.colorFn.getColor(first, pTicks)
                    .copyWithAlpha((int) Mth.clamp(this.alphaFn.getAlpha(first, this.alpha, pTicks) * 255, 0, 255));

            ShaderProgramsAS.TRANSPARENCY_COLOR.setColor(color);
            RenderUtil.withTarget(Minecraft.getInstance().levelRenderer.getParticlesTarget(), particleTarget -> {
                transparencyTarget.clear(Minecraft.ON_OSX);
                RenderUtil.safeCopyDepth(transparencyTarget, particleTarget);
                effects.forEach(effect -> {
                    effect.renderCube(renderInfo, chainBuffers, pTicks);
                });
                chainBuffers.end();
                ShaderProgramsAS.TRANSPARENCY_COLOR.redirect(particleTarget, chain -> {
                    chain.process(pTicks);
                });
                RenderUtil.safeCopyDepth(particleTarget, transparencyTarget);
            });
        }
    }
}
