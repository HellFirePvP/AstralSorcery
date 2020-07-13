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
import hellfirepvp.astralsorcery.client.effect.EntityDynamicFX;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.render.IDrawRenderTypeBuffer;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import net.minecraft.util.Tuple;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXFacingSprite
 * Created by HellFirePvP
 * Date: 30.08.2019 / 21:24
 */
public class FXFacingSprite extends EntityVisualFX implements EntityDynamicFX {

    private SpriteSheetResource sprite = null;

    public FXFacingSprite(Vector3 pos) {
        super(pos);
    }

    public FXFacingSprite setSprite(SpriteSheetResource sprite) {
        this.sprite = sprite;
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {}

    @Override
    public <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<T> ctx, MatrixStack renderStack, IDrawRenderTypeBuffer drawBuffer, float pTicks) {
        SpriteSheetResource ssr = this.sprite != null ? this.sprite : ctx.getSprite();
        Tuple<Float, Float> uvOffset = ssr.getUVOffset(this);

        int alpha = this.getAlpha(pTicks);
        Color col = this.getColor(pTicks);

        Vector3 vec = this.getRenderPosition(pTicks);
        float scale = this.getScale(pTicks);

        RenderTypeDecorator decorated = RenderTypeDecorator.wrapSetup(ctx.getRenderType(), ssr::bindTexture, BlockAtlasTexture.getInstance()::bindTexture);
        IVertexBuilder buf = drawBuffer.getBuffer(decorated);
        RenderingDrawUtils.renderFacingQuadVB(buf, renderStack,
                vec.getX(), vec.getY(), vec.getZ(),
                scale, 0F,
                uvOffset.getA(), uvOffset.getB(), ssr.getULength(), ssr.getVLength(),
                col.getRed(), col.getGreen(), col.getBlue(), alpha);
        drawBuffer.draw();
    }
}
