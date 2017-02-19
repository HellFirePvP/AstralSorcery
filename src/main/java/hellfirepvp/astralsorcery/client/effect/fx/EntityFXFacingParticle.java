/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.effect.fx;

import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntityFXFacingParticle
 * Created by HellFirePvP
 * Date: 16.10.2016 / 16:10
 */
public final class EntityFXFacingParticle extends EntityComplexFX {

    public static final BindableResource staticFlareTex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.EFFECT, "flareStatic");

    private double x, y, z;
    private double oldX, oldY, oldZ;
    private double yGravity = 0.004;
    private float scale = 1F;

    private boolean alphaFade = false;
    private float alphaMultiplier = 1F;
    private float colorRed = 1F, colorGreen = 1F, colorBlue = 1F;
    private double motionX = 0, motionY = 0, motionZ = 0;

    public EntityFXFacingParticle(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldX = x;
        this.oldY = y;
        this.oldZ = z;
    }

    public EntityFXFacingParticle updatePosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public EntityFXFacingParticle offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
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
        y += (motionY - yGravity);
        z += motionZ;
    }

    public static void renderFast(float parTicks, List<EntityFXFacingParticle> particles) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);

        staticFlareTex.bind();

        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (EntityFXFacingParticle particle : new ArrayList<>(particles)) {
            particle.renderFast(parTicks, vb);
        }

        t.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopAttrib();
    }

    //Vertex format: DefaultVertexFormats.POSITION_TEX_COLOR
    //GL states have to be preinitialized.
    public void renderFast(float pTicks, VertexBuffer vbDrawing) {
        float alpha = 1F;
        if(alphaFade) {
            float halfAge = maxAge / 2F;
            alpha = 1F - (Math.abs(halfAge - age) / halfAge);
        }
        alpha *= alphaMultiplier;
        RenderingUtils.renderFacingFullQuadVB(vbDrawing, interpolate(oldX, x, pTicks), interpolate(oldY, y, pTicks), interpolate(oldZ, z, pTicks), pTicks, scale, 0, colorRed, colorGreen, colorBlue, alpha);
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
        staticFlareTex.bind();
        RenderingUtils.renderFacingQuad(interpolate(oldX, x, pTicks), interpolate(oldY, y, pTicks), interpolate(oldZ, z, pTicks), pTicks, scale, 0, 0, 0, 1, 1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private double interpolate(double oldP, double newP, float partial) {
        return oldP + ((newP - oldP) * partial);
    }

}
