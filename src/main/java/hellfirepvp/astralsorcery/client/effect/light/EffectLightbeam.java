package hellfirepvp.astralsorcery.client.effect.light;

import hellfirepvp.astralsorcery.client.effect.IComplexEffect;
import hellfirepvp.astralsorcery.client.util.resource.AssetLibrary;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.BindableResource;
import hellfirepvp.astralsorcery.client.util.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectLightbeam
 * Created by HellFirePvP
 * Date: 06.08.2016 / 15:05
 */
public class EffectLightbeam implements IComplexEffect {

    private static final BindableResource beamTex = AssetLibrary.loadTexture(AssetLoader.TextureLocation.MISC, "lightbeam");
    private static final SpriteSheetResource beamSprite = beamTex.asSpriteSheet(16, 4);
    private final Vector3 from, to, aim, aimPerp;
    private final double fromSize, toSize;
    private int maxAge = 64;
    private int age = 0;

    public EffectLightbeam(Vector3 from, Vector3 to, double fromSize, double toSize) {
        this.from = from;
        this.to = to;
        this.aim = to.clone().subtract(from);
        this.aimPerp = aim.clone().perpendicular().normalize();
        this.fromSize = fromSize;
        this.toSize = toSize;
    }

    public EffectLightbeam(Vector3 from, Vector3 to, double size) {
        this(from, to, size, size);
    }

    public void setMaxAge(int newMax) {
        this.maxAge = newMax;
    }

    public void setDead() {
        age = maxAge;
    }

    @Override
    public boolean canRemove() {
        return age >= maxAge;
    }

    @Override
    public RenderTarget getRenderTarget() {
        return RenderTarget.RENDERLOOP;
    }

    @Override
    public void render(float pTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) return;
        if(rView.getDistanceSq(from.getX(), from.getY(), from.getZ()) > Config.maxEffectRenderDistanceSq) return;

        float halfAge = maxAge / 2F;
        float tr = 1F - (Math.abs(halfAge - age) / halfAge);
        tr *= 0.6;

        GL11.glPushMatrix();
        removeOldTranslate(rView, pTicks);
        GL11.glColor4f(tr, tr, tr, tr);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        beamTex.bind();

        renderCurrentTextureAroundAxis(Math.toRadians(0F));
        renderCurrentTextureAroundAxis(Math.toRadians(120F));
        renderCurrentTextureAroundAxis(Math.toRadians(240F));

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }

    private void removeOldTranslate(Entity entity, float partialTicks) {
        double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GL11.glTranslated(-x, -y, -z);
    }

    private void renderCurrentTextureAroundAxis(double angle) {
        Vector3 perp = aimPerp.clone().rotate(angle, aim).normalize();
        Vector3 perpFrom = perp.clone().multiply(fromSize);
        Vector3 perpTo = perp.multiply(toSize);

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer buf = tes.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Tuple<Double, Double> uvOffset = beamSprite.getUVOffset(age);
        double u = uvOffset.key;
        double v = uvOffset.value;
        double uWidth = beamSprite.getULength();
        double vHeight = beamSprite.getVLength();

        Vector3 vec = from.clone().add(perpFrom.clone().multiply(-1));
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,          v + vHeight).endVertex();
        vec = from.clone().add(perpFrom);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uWidth, v + vHeight).endVertex();
        vec = to.clone().add(perpTo);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uWidth, v)          .endVertex();
        vec = to.clone().add(perpTo.clone().multiply(-1));
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,          v)          .endVertex();

        tes.draw();
    }

    @Override
    public void tick() {
        age++;
    }

}
