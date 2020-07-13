/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXFacingAtlasSprite
 * Created by HellFirePvP
 * Date: 10.11.2019 / 15:17
 */
public class FXFacingAtlasParticle extends EntityVisualFX {

    private TextureAtlasSprite sprite;
    private float minU = 0, minV = 0;
    private float uLength = 1, vLength = 1;

    public FXFacingAtlasParticle(Vector3 pos) {
        super(pos);
    }

    public <T extends FXFacingAtlasParticle> T setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
        this.minU = this.sprite.getMinU();
        this.minV = this.sprite.getMinV();
        this.uLength = this.sprite.getMaxU() - this.minU;
        this.vLength = this.sprite.getMaxV() - this.minV;
        return (T) this;
    }

    public <T extends FXFacingAtlasParticle> T selectFraction(float percentage) {
        percentage = MathHelper.clamp(percentage, 0F, 1F);
        this.minU *= (1F - percentage) * rand.nextFloat();
        this.minV *= (1F - percentage) * rand.nextFloat();
        this.uLength *= percentage;
        this.vLength *= percentage;
        return (T) this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {
        if (this.sprite == null) {
            return;
        }

        Vector3 vec = this.getRenderPosition(pTicks);
        int alpha = this.getAlpha(pTicks);
        float fScale = this.getScale(pTicks);
        Color col = this.getColor(pTicks);

        RenderingDrawUtils.renderFacingQuadVB(vb, renderStack,
                vec.getX(), vec.getY(), vec.getZ(),
                fScale, 0F,
                this.minU, this.minV, this.uLength, this.vLength,
                col.getRed(), col.getGreen(), col.getBlue(), alpha);
    }
}
