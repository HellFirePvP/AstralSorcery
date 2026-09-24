/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.vertex.VertexConsumer;
import hellfirepvp.astralsorcery.client.effect.EffectTemplate;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.ImmediateVFX;
import hellfirepvp.astralsorcery.client.lib.RenderTypesAS;
import hellfirepvp.astralsorcery.client.resource.AtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXImmediateFacingSprite
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXImmediateFacingSprite extends VFXFacingParticle implements ImmediateVFX {

    private RenderType decoratedType = RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE;

    public VFXImmediateFacingSprite(Vector3 pos) {
        super(pos, AtlasTexture.getBlockAtlas());
    }

    @Override
    public <T extends VFXFacingParticle> T setSpriteSheet(@Nonnull SpriteSheet spriteSheet) {
        super.setSpriteSheet(spriteSheet);
        this.decoratedType = this.createDecoratedType();
        this.setMaxAge(spriteSheet.getFrameCount());
        return (T) this;
    }

    protected RenderType getDecoratedType() {
        return this.decoratedType;
    }

    private RenderType createDecoratedType() {
        RenderType r = RenderTypesAS.EFFECT_FX_GENERIC_PARTICLE;
        return new RenderType(r.name, r.format, r.mode, r.bufferSize, r.affectsCrumbling, r.sortOnUpload, r::setupRenderState, r::clearRenderState) {
            @Override
            public void setupRenderState() {
                super.setupRenderState();
                VFXImmediateFacingSprite.this.getSpriteSheet().bindTexture();
            }

            @Override
            public void clearRenderState() {
                super.clearRenderState();
                AtlasTexture.getBlockAtlas().bindTexture();
            }
        };
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

        Quaternionf facing = new Quaternionf();
        facing.set(camera.rotation());
        if (this.getRoll() != 0.0F) {
            facing.rotateZ(this.getRoll());
        }
        RenderingDrawUtil.renderFacingQuad(vb, facing, relativePos, color, fScale, packedLight, uv);

        drawBuffer.endBatch();
    }
}
