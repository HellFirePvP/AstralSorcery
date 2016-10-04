package hellfirepvp.astralsorcery.client.effect.texture;

import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.util.BlendingHelper;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePlane
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:55
 */
public class TexturePlane implements IComplexEffect {

    protected double u, v, uLength, vLength;

    private float lastRenderDegree = 0F;

    private boolean alphaGradient = false;

    private int counter = 0;
    private boolean remove = false;
    private RefreshFunction refreshFunc = null;

    private BlendingHelper blendMode = BlendingHelper.DEFAULT;

    private Color colorOverlay = Color.WHITE;
    private int ticksPerFullRot = 100;
    private float fixDegree = 0;
    private int maxAge = -1;
    private Vector3 pos = new Vector3(0, 0, 0);
    private float scale = 1F;
    private float alphaMul = 1F;
    private ScaleFunction scaleFunc;

    private final BindableResource texture;
    private final Axis axis;

    public TexturePlane(BindableResource texture, Axis axis) {
        this.texture = texture;
        this.axis = axis;
    }

    public TexturePlane setPosition(Vector3 pos) {
        this.pos = pos;
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

    public TexturePlane setBlendMode(@Nonnull BlendingHelper blendMode) {
        this.blendMode = blendMode;
        return this;
    }

    public TexturePlane setAlphaMultiplier(float alphaMul) {
        this.alphaMul = alphaMul;
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
                if(rView == null) rView = Minecraft.getMinecraft().thePlayer;
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

        float alphaGrad = colorOverlay.getAlpha() * alphaMul;
        if(alphaGradient) {
            alphaGrad = getAlphaDistanceMultiplier(dst) * alphaMul;
        }

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        removeOldTranslate(rView, partialTicks);
        GL11.glColor4f(colorOverlay.getRed(), colorOverlay.getGreen(), colorOverlay.getBlue(), alphaGrad);
        GL11.glEnable(GL11.GL_BLEND);
        blendMode.apply();
        Vector3 axis = this.axis.getAxis();
        float deg;
        if(ticksPerFullRot >= 0) {
            float anglePercent = ((float) (counter)) / ((float) ticksPerFullRot);
            deg = anglePercent * 360F;
            deg = interpolateRotation(lastRenderDegree, deg, partialTicks);
            this.lastRenderDegree = deg;
        } else {
            deg = fixDegree;
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);

        currRenderAroundAxis(Math.toRadians(deg), axis);
        currRenderAroundAxis(Math.toRadians(360F - deg), axis.clone().multiply(-1)); //From the other side.

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private void currRenderAroundAxis(double angle, Vector3 axis) {
        Vector3 renderStart = axis.clone().perpendicular().rotate(angle, axis).normalize();

        float scale = this.scale;
        if(scaleFunc != null) {
            scale = scaleFunc.getScale(this);
        }

        texture.bind();
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer buf = tes.getBuffer();

        //GL11.glEnable(GL11.GL_BLEND);
        //GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,           v + vLength).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uLength, v + vLength).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uLength, v          ).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,           v          ).endVertex();
        tes.draw();

        //GL11.glDisable(GL11.GL_BLEND);
    }

    private void removeOldTranslate(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GL11.glTranslated(-x, -y, -z);
    }

    public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick) {
        float rot = nextRotation - prevRotation;
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        return prevRotation + partialTick * rot;
    }

    public static interface ScaleFunction {

        public float getScale(TexturePlane plane);

    }

    public static interface RefreshFunction {

        public boolean shouldRefresh();

    }

}
