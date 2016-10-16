package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFacingParticle
 * Created by HellFirePvP
 * Date: 16.10.2016 / 16:10
 */
public class EntityFXFacingParticle extends EntityComplexFX {

    private final BindableResource resource;
    private double x, y, z;
    private double oldX, oldY, oldZ;
    private double yGravity = 0.004;
    private float scale = 1F;

    private boolean alphaFade = false;
    private float alphaMultiplier = 1F;
    private float colorRed = 1F, colorGreen = 1F, colorBlue = 1F;
    private double motionX = 0, motionY = 0, motionZ = 0;

    public EntityFXFacingParticle(BindableResource resource, double x, double y, double z) {
        this.resource = resource;
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldX = x;
        this.oldY = y;
        this.oldZ = z;
    }

    public EntityFXFacingParticle enableAlphaFade() {
        alphaFade = true;
        return this;
    }

    public EntityFXFacingParticle motion(double x, double y, double z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
        return this;
    }

    public EntityFXFacingParticle gravity(double yGrav) {
        this.yGravity -= yGrav;
        return this;
    }

    public EntityFXFacingParticle scale(float scale) {
        this.scale = scale;
        return this;
    }

    public EntityFXFacingParticle setAlphaMultiplier(float alphaMul) {
        alphaMultiplier = alphaMul;
        return this;
    }

    public EntityFXFacingParticle setColor(Color color) {
        colorRed   = ((float) color.getRed())   / 255F;
        colorGreen = ((float) color.getGreen()) / 255F;
        colorBlue  = ((float) color.getBlue())  / 255F;
        return this;
    }

    @Override
    public void tick() {
        super.tick();

        oldX = x;
        oldY = y;
        oldZ = z;
        x += motionX;
        y += motionY - yGravity;
        z += motionZ;
    }

    @Override
    public void render(float pTicks) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        float alpha = 1F;
        if(alphaFade) {
            float halfAge = maxAge / 2F;
            alpha = 1F - (Math.abs(halfAge - age) / halfAge);
        }
        alpha *= alphaMultiplier;
        GL11.glColor4f(colorRed, colorGreen, colorBlue, alpha);
        resource.bind();
        RenderingUtils.renderFacingQuad(interpolate(oldX, x, pTicks), interpolate(oldY, y, pTicks), interpolate(oldZ, z, pTicks), pTicks, scale, 0, 0, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private double interpolate(double oldP, double newP, float partial) {
        return oldP + ((newP - oldP) * partial);
    }

}
