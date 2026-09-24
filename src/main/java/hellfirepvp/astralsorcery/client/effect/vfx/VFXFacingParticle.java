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
import hellfirepvp.astralsorcery.client.resource.AbstractRenderTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheet;
import hellfirepvp.astralsorcery.client.resource.UVFrame;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Camera;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VFXFacingParticle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VFXFacingParticle extends EntityVisualFX {

    private SpriteSheet spriteSheet;
    private float roll;

    public VFXFacingParticle(Vector3 pos, AbstractRenderTexture texture) {
        this(pos, texture.asSpriteSheet());
    }

    public VFXFacingParticle(Vector3 pos, SpriteSheet spriteSheet) {
        super(pos);
        this.spriteSheet = spriteSheet;
    }

    public VFXFacingParticle setRoll(float roll) {
        this.roll = roll;
        return this;
    }

    protected float getRoll() {
        return this.roll;
    }

    protected <T extends VFXFacingParticle> T setSpriteSheet(@Nonnull SpriteSheet spriteSheet) {
        this.spriteSheet = spriteSheet;
        return (T) this;
    }

    @Nonnull
    protected SpriteSheet getSpriteSheet() {
        return this.spriteSheet;
    }

    @Override
    public void render(EffectTemplate<?> ctx, Camera renderInfo, VertexConsumer vb, float pTicks) {
        Vector3 relativePos = this.getRenderOffset(this.getInterpolatedPos(pTicks), pTicks).subtract(renderInfo.getPosition());
        float fScale = this.getScale(pTicks);
        ColorWrapper color = this.getColor(pTicks).copyWithAlpha(this.getAlphaI(pTicks));
        UVFrame uv = this.getSpriteSheet().getUV(this.getAge());
        int packedLight = this.getLight(pTicks);

        Quaternionf facing = new Quaternionf();
        facing.set(renderInfo.rotation());
        if (this.roll != 0.0F) {
            facing.rotateZ(this.roll);
        }
        RenderingDrawUtil.renderFacingQuad(vb, facing, relativePos, color, fScale, packedLight, uv);
    }
}
