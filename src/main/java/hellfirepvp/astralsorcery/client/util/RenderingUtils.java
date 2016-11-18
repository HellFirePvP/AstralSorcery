package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 29.08.2016 / 16:51
 */
public class RenderingUtils {

    private static final Random rand = new Random();
    private static ParticleDigging.Factory diggingFactory = new ParticleDigging.Factory();

    public static void playBlockBreakParticles(BlockPos pos, IBlockState state) {
        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;

        for (int j = 0; j < 4; j++) {
            for (int k = 0; k < 4; k++) {
                for (int l = 0; l < 4; l++) {
                    double d0 = (double) pos.getX() + ((double) j + 0.5D) / 4D;
                    double d1 = (double) pos.getY() + ((double) k + 0.5D) / 4D;
                    double d2 = (double) pos.getZ() + ((double) l + 0.5D) / 4D;
                    Particle digging = diggingFactory.getEntityFX(0, Minecraft.getMinecraft().theWorld,
                            d0, d1, d2,
                            d0 - (double) pos.getX() - 0.5D,
                            d1 - (double) pos.getY() - 0.5D,
                            d2 - (double) pos.getZ() - 0.5D,
                            Block.getStateId(state));
                    pm.addEffect(digging);
                }
            }
        }
    }

    public static double interpolate(double oldP, double newP, float partialTicks) {
        if(oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
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

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed, int continuousTick, int dstJump, int countFancy, int countNormal) {
        renderLightRayEffects(x, y, z, effectColor, seed, continuousTick, dstJump, 1, countFancy, countNormal);
    }

    public static void renderLightRayEffects(double x, double y, double z, Color effectColor, long seed, int continuousTick, int dstJump, float scale, int countFancy, int countNormal) {
        rand.setSeed(seed);
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);

        int fancy_count = !FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? countNormal : countFancy;

        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();

        RenderHelper.disableStandardItemLighting();
        float f1 = continuousTick / 400.0F;
        float f2 = 0.4F;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < fancy_count; i++) {
            GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(rand.nextFloat() * 360.0F + f1 * 360.0F, 0.0F, 0.0F, 1.0F);
            vb.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
            float fa = rand.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
            float f4 = rand.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
            fa /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            f4 /= 30.0F / (Math.min(dstJump, 10 * scale) / 10.0F);
            vb.pos(0, 0, 0).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), (int) (255.0F * (1.0F - f2))).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos( 0.0D,      fa,    1.0F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            vb.pos(-0.7D * f4, fa,   -0.5F * f4).color(effectColor.getRed(), effectColor.getGreen(), effectColor.getBlue(), 0).endVertex();
            tes.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        RenderHelper.enableStandardItemLighting();

        GL11.glPopMatrix();
    }

    public static void renderBlueTooltip(int x, int y, List<String> tooltipData, FontRenderer fontRenderer) {
        renderTooltip(x, y, tooltipData, new Color(0x000033), new Color(0x000044), fontRenderer);
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, Color color, Color colorFade, FontRenderer fontRenderer) {
        TextureHelper.setActiveTextureToAtlasSprite();
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting)
            RenderHelper.disableStandardItemLighting();

        if (!tooltipData.isEmpty()) {
            int esWidth = 0;
            for (String toolTip : tooltipData) {
                int width = fontRenderer.getStringWidth(toolTip);
                if (width > esWidth)
                    esWidth = width;
            }
            int pX = x + 12;
            int pY = y - 12;
            int sumLineHeight = 8;
            if (tooltipData.size() > 1)
                sumLineHeight += 2 + (tooltipData.size() - 1) * 10;
            float z = 300F;

            drawGradientRect(pX - 3,           pY - 4,                 z, pX + esWidth + 3, pY - 3,                 color, colorFade);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 3, z, pX + esWidth + 3, pY + sumLineHeight + 4, color, colorFade);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX - 4,           pY - 3,                 z, pX - 3,           pY + sumLineHeight + 3, color, colorFade);
            drawGradientRect(pX + esWidth + 3, pY - 3,                 z, pX + esWidth + 4, pY + sumLineHeight + 3, color, colorFade);

            int rgb = color.getRGB();
            int col = (rgb & 0x00FFFFFF) | rgb & 0xFF000000;
            Color colOp = new Color(col);
            drawGradientRect(pX - 3,           pY - 3 + 1,             z, pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX + esWidth + 2, pY - 3 + 1,             z, pX + esWidth + 3, pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY - 3 + 1,                 colOp, colOp);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3,     color, color);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            for (int i = 0; i < tooltipData.size(); ++i) {
                String var14 = tooltipData.get(i);
                fontRenderer.drawString(var14, pX, pY, Color.WHITE.getRGB());
                if (i == 0)
                    pY += 2;
                pY += 10;
            }
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        if (lighting)
            RenderHelper.enableStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void removeStandartTranslationFromTESRMatrix(float partialTicks) {
        Entity rView = Minecraft.getMinecraft().getRenderViewEntity();
        if(rView == null) rView = Minecraft.getMinecraft().thePlayer;
        Entity entity = rView;
        double tx = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
        double ty = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
        double tz = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
        GL11.glTranslated(-tx, -ty, -tz);
    }

    public static void renderAngleRotatedTexturedRect(Vector3 renderOffset, Vector3 axis, double angleRad, double scale, double u, double v, double uLength, double vLength, float partialTicks) {
        GL11.glPushMatrix();
        removeStandartTranslationFromTESRMatrix(partialTicks);

        Vector3 renderStart = axis.clone().perpendicular().rotate(angleRad, axis).normalize();
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer buf = tes.getBuffer();

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        Vector3 vec = renderStart.clone().rotate(Math.toRadians(90), axis).normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,           v + vLength).endVertex();

        vec = renderStart.clone().multiply(-1).normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uLength, v + vLength).endVertex();

        vec = renderStart.clone().rotate(Math.toRadians(270), axis).normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u + uLength, v          ).endVertex();

        vec = renderStart.clone().normalize().multiply(scale).add(renderOffset);
        buf.pos(vec.getX(), vec.getY(), vec.getZ()).tex(u,           v          ).endVertex();

        tes.draw();

        GL11.glPopMatrix();
    }

    public static void drawGradientRect(int x, int y, float z, int toX, int toY, Color color, Color colorFade) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(toX, y,   z).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).endVertex();
        vb.pos(x,   y,   z).color(color.getRed(),     color.getGreen(),     color.getBlue(),     color.getAlpha()).endVertex();
        vb.pos(x,   toY, z).color(colorFade.getRed(), colorFade.getGreen(), colorFade.getBlue(), colorFade.getAlpha()).endVertex();
        vb.pos(toX, toY, z).color(colorFade.getRed(), colorFade.getGreen(), colorFade.getBlue(), colorFade.getAlpha()).endVertex();
        tes.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void renderFacingFullQuadVB(VertexBuffer vb, double px, double py, double pz, float partialTicks, float scale, float angle, float colorRed, float colorGreen, float colorBlue, float alpha) {
        renderFacingQuadVB(vb, px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1, colorRed, colorGreen, colorBlue, alpha);
    }

    public static void renderFacingQuadVB(VertexBuffer vb, double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength, float colorRed, float colorGreen, float colorBlue, float alpha) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().thePlayer;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.aroundAxis(qvec, angle);
            q.rotate(v1);
            q.rotate(v2);
            q.rotate(v3);
            q.rotate(v4);
        }
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u,           v + vLength).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v + vLength).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u + uLength, v          ).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,           v          ).color(colorRed, colorGreen, colorBlue, alpha).endVertex();
    }

    public static void renderFacingFullQuad(double px, double py, double pz, float partialTicks, float scale, float angle) {
        renderFacingQuad(px, py, pz, partialTicks, scale, angle, 0, 0, 1, 1);
    }

    public static void renderFacingQuad(double px, double py, double pz, float partialTicks, float scale, float angle, double u, double v, double uLength, double vLength) {
        float arX =  ActiveRenderInfo.getRotationX();
        float arZ =  ActiveRenderInfo.getRotationZ();
        float arYZ = ActiveRenderInfo.getRotationYZ();
        float arXY = ActiveRenderInfo.getRotationXY();
        float arXZ = ActiveRenderInfo.getRotationXZ();

        Entity e = Minecraft.getMinecraft().getRenderViewEntity();
        if(e == null) {
            e = Minecraft.getMinecraft().thePlayer;
        }
        double iPX = e.prevPosX + (e.posX - e.prevPosX) * partialTicks;
        double iPY = e.prevPosY + (e.posY - e.prevPosY) * partialTicks;
        double iPZ = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTicks;

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.aroundAxis(qvec, angle);
            q.rotate(v1);
            q.rotate(v2);
            q.rotate(v3);
            q.rotate(v4);
        }
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(px + v1.getX() - iPX, py + v1.getY() - iPY, pz + v1.getZ() - iPZ).tex(u,           v + vLength).endVertex();
        vb.pos(px + v2.getX() - iPX, py + v2.getY() - iPY, pz + v2.getZ() - iPZ).tex(u + uLength, v + vLength).endVertex();
        vb.pos(px + v3.getX() - iPX, py + v3.getY() - iPY, pz + v3.getZ() - iPZ).tex(u + uLength, v          ).endVertex();
        vb.pos(px + v4.getX() - iPX, py + v4.getY() - iPY, pz + v4.getZ() - iPZ).tex(u,           v          ).endVertex();
        t.draw();
    }

}
