/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.ImmediateVFX;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderVectorUtil;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXImmediateTextureSprite
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXImmediateTextureSprite extends EntityVisualFX implements ImmediateVFX {

    private SpriteSheet spriteSheet;
    private RenderType decoratedType = RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE;

    private Vector3 rotationAxis = Vector3.RotAxis.Y_AXIS.getVector();
    private int ticksPerFullRotation = 100;
    private float staticRotationDegree = 0F;

    private float lastRotationDegree = 0F;

    public VFXImmediateTextureSprite(Vector3 pos) {
        super(pos);
    }

    public <T extends VFXImmediateTextureSprite> T setSpriteSheet(SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
        this.decoratedType = this.createDecoratedType();
        this.setMaxAge(spriteSheet.getFrameCount());
        return MiscUtil.cast(this);
    }

    protected SpriteSheet getSpriteSheet() {
        return this.spriteSheet;
    }

    private RenderType createDecoratedType() {
        RenderType r = RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE;
        return new RenderType(r.name, r.format, r.mode, r.bufferSize, r.affectsCrumbling, r.sortOnUpload, r::setupRenderState, r::clearRenderState) {
            @Override
            public void setupRenderState() {
                super.setupRenderState();
                VFXImmediateTextureSprite.this.getSpriteSheet().bindTexture();
            }

            @Override
            public void clearRenderState() {
                super.clearRenderState();
                AtlasTexture.getBlockAtlas().bindTexture();
            }
        };
    }

    protected RenderType getDecoratedType() {
        return this.decoratedType;
    }

    public <T extends VFXImmediateTextureSprite> T setRotationAxis(Vector3 axis) {
        this.rotationAxis = axis.copy();
        return MiscUtil.cast(this);
    }

    public <T extends VFXImmediateTextureSprite> T setTicksPerFullRotation(int ticksPerFullRot) {
        this.ticksPerFullRotation = ticksPerFullRot;
        return MiscUtil.cast(this);
    }

    public <T extends VFXImmediateTextureSprite> T setNoRotation(float fixedDegree) {
        this.ticksPerFullRotation = -1;
        this.staticRotationDegree = fixedDegree;
        return MiscUtil.cast(this);
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {}

    @Override
    public <T extends EntityVisualFX & ImmediateVFX> void renderImmediate(EffectTemplate<T> template, Camera camera, MultiBufferSource.BufferSource drawBuffer, float pTicks) {
        VertexConsumer vb = drawBuffer.getBuffer(this.getDecoratedType());

        Vector3 relativePos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks).subtract(camera.getPosition());
        float fScale = this.getScale(pTicks);
        ColorWrapper color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks));
        UVFrame uv = this.getSpriteSheet().getUV(this.getAge());
        int packedLight = this.getLight(pTicks);

        float degree;
        if (this.ticksPerFullRotation >= 0) {
            float anglePercent = (float) this.getAge() / this.ticksPerFullRotation;
            degree = anglePercent * 360F;
            degree = RenderVectorUtil.interpolateRotation(this.lastRotationDegree, degree, pTicks);
            this.lastRotationDegree = degree;
        } else {
            degree = this.staticRotationDegree;
        }

        RenderingDrawUtil.renderAngledTexturedQuad(vb, new PoseStack(), relativePos,
                this.rotationAxis, (float) Math.toRadians(degree), fScale, uv, color, packedLight);
        drawBuffer.endBatch();
    }
}
