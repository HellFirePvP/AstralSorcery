/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.vfx;

import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import hellfirepvp.astralsorcery.client.effect.EntityVisualFX;
import hellfirepvp.astralsorcery.client.effect.context.base.BatchRenderContext;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtils;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.RenderingVectorUtils;
import hellfirepvp.astralsorcery.client.util.draw.BufferContext;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tuple;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FXSpritePlane
 * Created by HellFirePvP
 * Date: 18.07.2019 / 00:25
 */
public class FXSpritePlane extends EntityVisualFX {

    private float lastRenderDegree = 0F;
    private boolean alphaGradient = false;

    private Vector3 axis = Vector3.RotAxis.Y_AXIS;
    private int ticksPerFullRot = 100;
    private float fixDegree = 0;

    private SpriteSheetResource sprite = null;

    public FXSpritePlane(Vector3 pos) {
        super(pos);
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

    public FXSpritePlane setAlphaOverDistance(boolean alphaGradient) {
        this.alphaGradient = alphaGradient;
        return this;
    }

    public float getAlphaDistanceMultiplier(double dstSq) {
        double maxSqDst = RenderingConfig.CONFIG.getMaxEffectRenderDistanceSq();
        return 1F - (float) (dstSq / maxSqDst);
    }

    @Override
    public <T extends EntityVisualFX> void render(BatchRenderContext<T> ctx, BufferContext buf, float pTicks) {
        if (!RenderingUtils.canEffectExist(this)) {
            return;
        }
        Entity rView = Minecraft.getInstance().getRenderViewEntity();
        if (rView == null) {
            rView = Minecraft.getInstance().player;
        }

        Vector3 pos = this.getRenderPosition(pTicks);
        double dst = rView.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());

        float scale = this.getScale(pTicks);
        float alpha = this.getAlpha(pTicks);
        if (this.alphaGradient) {
            alpha *= this.getAlphaDistanceMultiplier(dst);
        }

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

        Color c = this.getColor(pTicks);
        float r = c.getRed() / 255F;
        float g = c.getGreen() / 255F;
        float b = c.getBlue() / 255F;

        SpriteSheetResource ssr = this.sprite != null ? this.sprite : ctx.getSprite();
        Tuple<Double, Double> uvOffset = ssr.getUVOffset(age);
        double u = uvOffset.getA();
        double v = uvOffset.getB();
        double uLength = ssr.getULength();
        double vLength = ssr.getVLength();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        RenderingDrawUtils.renderAngleRotatedTexturedRectVB(buf, pos, axis, Math.toRadians(deg), scale, u, v, uLength, vLength, r, g, b, alpha);
        buf.draw();
    }

}
