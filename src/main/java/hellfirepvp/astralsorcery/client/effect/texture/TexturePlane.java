/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.texture;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.util.Blending;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePlane
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:55
 */
public class TexturePlane implements IComplexEffect, IComplexEffect.PreventRemoval {

    protected double u = 0, v = 0, uLength = 1, vLength = 1;

    private float lastRenderDegree = 0F;

    private boolean alphaGradient = false;

    private int counter = 0;
    private boolean remove = false;
    private RefreshFunction refreshFunc = null;

    private Color colorOverlay = Color.WHITE;
    private int ticksPerFullRot = 100;
    private float fixDegree = 0;
    private int maxAge = -1;
    private Vector3 pos = new Vector3(0, 0, 0);
    private float scale = 1F;
    private EntityComplexFX.AlphaFunction alphaFunction = EntityComplexFX.AlphaFunction.CONSTANT;
    private float alphaMultiplier = 1F;
    private ScaleFunction scaleFunc;
    private boolean flagRemoved = true;

    private final BindableResource texture;
    private final Vector3 axis;

    public TexturePlane(BindableResource texture, Vector3 axis) {
        this.texture = texture;
        this.axis = axis;
    }

    public TexturePlane setPosition(Vector3 pos) {
        this.pos = pos;
        return this;
    }

    public TexturePlane setStaticUVOffset(double u, double v) {
        this.u = u;
        this.v = v;
        return this;
    }

    public TexturePlane setUVLength(double uLength, double vLength) {
        this.uLength = uLength;
        this.vLength = vLength;
        return this;
    }

    public TexturePlane setTicksPerFullRotation(int ticksPerFullRot) {
        this.ticksPerFullRot = ticksPerFullRot;
        return this;
    }

    public TexturePlane setNoRotation(float fixedDregree) {
        this.ticksPerFullRot = -1;
        this.fixDegree = fixedDregree;
        return this;
    }

    public TexturePlane setMaxAge(int ticksMaxAge) {
        this.maxAge = ticksMaxAge;
        return this;
    }

    public TexturePlane setColorOverlay(Color colorOverlay) {
        this.colorOverlay = colorOverlay;
        return this;
    }

    public TexturePlane setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public TexturePlane setScaleFunction(ScaleFunction function) {
        this.scaleFunc = function;
        return this;
    }

    public TexturePlane setRefreshFunc(RefreshFunction refreshFunc) {
        this.refreshFunc = refreshFunc;
        return this;
    }

    public TexturePlane setAlphaFunction(EntityComplexFX.AlphaFunction alphaFunction) {
        this.alphaFunction = alphaFunction;
        return this;
    }

    public TexturePlane setAlphaMultiplier(float alphaMultiplier) {
        this.alphaMultiplier = alphaMultiplier;
        return this;
    }

    public TexturePlane setAlphaOverDistance(boolean alphaGradient) {
        this.alphaGradient = alphaGradient;
        return this;
    }

    public float getAlphaDistanceMultiplier(double dstSq) {
        double maxSqDst = Config.maxEffectRenderDistanceSq;
        return 1F - (float) (dstSq / maxSqDst);
    }

    public boolean isRemoved() {
        return flagRemoved;
    }

    public void flagAsRemoved() {
        flagRemoved = true;
    }

    public void clearRemoveFlag() {
        flagRemoved = false;
    }

    public void setDead() {
        remove = true;
    }

    public int getAge() {
        return counter;
    }

    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void tick() {
        counter++;

        if(maxAge >= 0 && counter >= maxAge) {
            if(refreshFunc != null) {
                Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
                if(rView == null) rView = Minecraft.getMinecraft().player;
                if(rView.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) <= Config.maxEffectRenderDistanceSq) {
                    if(refreshFunc.shouldRefresh()) {
                        counter = 0;
                        return;
                    }
                }
            }
            setDead();
        }
    }

    @Override
    public boolean canRemove() {
        return remove;
    }

    @Override
    public void render(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) return;
        double dst = rView.getDistanceSq(pos.getX(), pos.getY(), pos.getZ());
        if(dst > Config.maxEffectRenderDistanceSq) return;

        float alphaMul = alphaFunction.getAlpha(counter, maxAge);
        float alphaGrad = colorOverlay.getAlpha() * alphaMul * this.alphaMultiplier;
        if(alphaGradient) {
            alphaGrad = getAlphaDistanceMultiplier(dst) * alphaMul * this.alphaMultiplier;
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        //removeOldTranslate(rView, partialTicks);
        GL11.glColor4f(colorOverlay.getRed() / 255F, colorOverlay.getGreen() / 255F, colorOverlay.getBlue() / 255F, alphaGrad / 255F);
        GL11.glEnable(GL11.GL_BLEND);
        Blending.DEFAULT.apply();
        Vector3 axis = this.axis.clone();
        float deg;
        if(ticksPerFullRot >= 0) {
            float anglePercent = ((float) (counter)) / ((float) ticksPerFullRot);
            deg = anglePercent * 360F;
            deg = RenderingUtils.interpolateRotation(lastRenderDegree, deg, partialTicks);
            this.lastRenderDegree = deg;
        } else {
            deg = fixDegree;
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);

        currRenderAroundAxis(partialTicks, Math.toRadians(deg), axis);

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void currRenderAroundAxis(float parTicks, double angle, Vector3 axis) {
        float scale = this.scale;
        if(scaleFunc != null) {
            scale = scaleFunc.getScale(scale);
        }
        texture.bind();
        RenderingUtils.renderAngleRotatedTexturedRect(pos, axis, angle, scale, u, v, uLength, vLength, parTicks);
    }

    private void removeOldTranslate(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GL11.glTranslated(-x, -y, -z);
    }

    public static interface ScaleFunction {

        public float getScale(float scaleIn);

    }

    public static interface RefreshFunction {

        public boolean shouldRefresh();

    }

}
