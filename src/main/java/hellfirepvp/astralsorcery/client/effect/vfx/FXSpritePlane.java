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
import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.resource.BlockAtlasTexture;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.observerlib.client.util.RenderTypeDecorator;
import net.minecraft.util.Tuple;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXSpritePlane
 * Created by HellFirePvP
 * Date: 18.07.2019 / 00:25
 */
public class FXSpritePlane extends EntityVisualFX implements EntityDynamicFX {

    private float lastRenderDegree = 0F;

    private Vector3 axis = Vector3.RotAxis.Y_AXIS;
    private int ticksPerFullRot = 100;
    private float fixDegree = 0;

    private SpriteSheetResource sprite = null;

    public FXSpritePlane(Vector3 pos) {
        super(pos);
    }

    public FXSpritePlane setSprite(AbstractRenderableTexture tex) {
        return this.setSprite(new SpriteSheetResource(tex));
    }

    public FXSpritePlane setSprite(SpriteSheetResource sprite) {
        this.sprite = sprite;
        return this;
    }

    public FXSpritePlane setAxis(Vector3 axis) {
        this.axis = axis.clone();
        return this;
    }

    public FXSpritePlane setTicksPerFullRotation(int ticksPerFullRot) {
        this.ticksPerFullRot = ticksPerFullRot;
        return this;
    }

    public FXSpritePlane setNoRotation(float fixedDregree) {
        this.ticksPerFullRot = -1;
        this.fixDegree = fixedDregree;
        return this;
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, MatrixStack renderStack, IVertexBuilder vb, float pTicks) {}

    @Override
    public <T extends EntityVisualFX & EntityDynamicFX> void renderNow(BatchRenderContext<T> ctx, MatrixStack renderStack, IDrawRenderTypeBuffer drawBuffer, float pTicks) {
        SpriteSheetResource ssr = this.sprite != null ? this.sprite : ctx.getSprite();
        Tuple<Float, Float> uvOffset = ssr.getUVOffset(this);

        Vector3 vec = this.getRenderPosition(pTicks);
        vec.subtract(RenderingVectorUtils.getStandardTranslationRemovalVector(pTicks));
        float scale = this.getScale(pTicks);

        int alpha = this.getAlpha(pTicks);
        Color color = this.getColor(pTicks);

        Vector3 axis = this.axis.clone();
        float deg;
        if (ticksPerFullRot >= 0) {
            float anglePercent = ((float) (getAge())) / ((float) ticksPerFullRot);
            deg = anglePercent * 360F;
            deg = RenderingVectorUtils.interpolateRotation(lastRenderDegree, deg, pTicks);
            this.lastRenderDegree = deg;
        } else {
            deg = fixDegree;
        }

        RenderTypeDecorator decorated = RenderTypeDecorator.wrapSetup(ctx.getRenderType(), ssr::bindTexture, () -> BlockAtlasTexture.getInstance().bindTexture());
        IVertexBuilder buf = drawBuffer.getBuffer(decorated);
        RenderingDrawUtils.renderAngleRotatedTexturedRectVB(buf, renderStack, vec,
                axis, (float) Math.toRadians(deg), scale,
                uvOffset.getA(), uvOffset.getB(), ssr.getULength(), ssr.getVLength(),
                color.getRed(), color.getGreen(), color.getBlue(), alpha);

        drawBuffer.draw();
    }
}
