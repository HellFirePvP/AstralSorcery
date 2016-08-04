package hellfirepvp.astralsorcery.client.effect;

import hellfirepvp.astralsorcery.client.util.BindableResource;
import hellfirepvp.astralsorcery.common.util.Axis;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TexturePlane
 * Created by HellFirePvP
 * Date: 02.08.2016 / 12:55
 */
public class TexturePlane implements IComplexEffect {

    private float lastRenderDegree = 0F;

    private int counter = 0;
    private boolean remove = false;

    private Color colorOverlay = Color.WHITE;
    private int ticksPerFullRot = 100;
    private float fixDegree = 0;
    private int maxAge = -1;
    private Vector3 pos = new Vector3(0, 0, 0);
    private float scale = 1F;
    private ScaleFunction scaleFunc;

    private final BindableResource texture;
    private final Axis axis;

    protected TexturePlane(BindableResource texture, Axis axis) {
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
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void tick() {
        counter++;

        if(maxAge >= 0 && counter >= maxAge) {
            setDead();
        }
    }

    @Override
    public boolean canRemove() {
        return remove;
    }

    @Override
    public void render(float partialTicks) {
        if(Minecraft.getMinecraft().getRenderViewEntity() == null) return;

        GL11.glPushMatrix();
        removeOldTranslate(Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);
        GL11.glColor4f(colorOverlay.getRed(), colorOverlay.getGreen(), colorOverlay.getBlue(), colorOverlay.getAlpha());
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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

        currRenderAroundAxis(Math.toRadians(deg), axis);
        currRenderAroundAxis(Math.toRadians(360F - deg), axis.clone().multiply(-1));

        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
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
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(0, 1).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(1, 1).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(1, 0).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(pos);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(0, 0).endVertex();
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

}
