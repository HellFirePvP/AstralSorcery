package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 29.08.2016 / 16:51
 */
public class RenderingUtils {

    private static ParticleDigging.Factory diggingFactory = new ParticleDigging.Factory();

    public static void playBlockBreakParticles(BlockPos pos, IBlockState state) {
        ParticleManager pm = Minecraft.getMinecraft().effectRenderer;

        for (int j = 0; j < 4; ++j) {
            for (int k = 0; k < 4; ++k) {
                for (int l = 0; l < 4; ++l) {
                    double d0 = (double) pos.getX() + ((double)j + 0.5D) / 4.0D;
                    double d1 = (double) pos.getY() + ((double)k + 0.5D) / 4.0D;
                    double d2 = (double) pos.getZ() + ((double)l + 0.5D) / 4.0D;
                    Particle digging = diggingFactory.getEntityFX(0, Minecraft.getMinecraft().theWorld,
                            d0, d1, d2,
                            d0 - (double)pos.getX() - 0.5D,
                            d1 - (double)pos.getY() - 0.5D,
                            d2 - (double)pos.getZ() - 0.5D,
                            BlockMarble.getStateId(state));
                    pm.addEffect(digging);
                }
            }
        }
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int color2) {
        renderTooltip(x, y, tooltipData, color, color2, Minecraft.getMinecraft().fontRendererObj);
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int color2, FontRenderer fontRenderer) {
        Minecraft.getMinecraft().renderEngine.bindTexture(SpecialTextureLibrary.getBlockAtlasTexture());
        boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
        if (lighting)
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();

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

            drawGradientRect(pX - 3,           pY - 4,                 z, pX + esWidth + 3, pY - 3,                 color2, color2);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 3, z, pX + esWidth + 3, pY + sumLineHeight + 4, color2, color2);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY + sumLineHeight + 3, color2, color2);
            drawGradientRect(pX - 4,           pY - 3,                 z, pX - 3,           pY + sumLineHeight + 3, color2, color2);
            drawGradientRect(pX + esWidth + 3, pY - 3,                 z, pX + esWidth + 4, pY + sumLineHeight + 3, color2, color2);

            int colOp = (color & 0xFFFFFF) >> 1 | color & -16777216;
            drawGradientRect(pX - 3,           pY - 3 + 1,             z, pX - 3 + 1,       pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX + esWidth + 2, pY - 3 + 1,             z, pX + esWidth + 3, pY + sumLineHeight + 3 - 1, color, colOp);
            drawGradientRect(pX - 3,           pY - 3,                 z, pX + esWidth + 3, pY - 3 + 1,                 color, color);
            drawGradientRect(pX - 3,           pY + sumLineHeight + 2, z, pX + esWidth + 3, pY + sumLineHeight + 3,     colOp, colOp);

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
            net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        GL11.glColor4f(1F, 1F, 1F, 1F);
    }

    public static void drawGradientRect(int x, int y, float z, int toX, int toY, int color1, int color2) {
        int alpha1 = color1 >> 24 & 255;
        int red1   = color1 >> 16 & 255;
        int green1 = color1 >> 8  & 255;
        int blue1  = color1       & 255;

        int alpha2 = color2 >> 24 & 255;
        int red2   = color2 >> 16 & 255;
        int green2 = color2 >> 8  & 255;
        int blue2  = color2       & 255;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(toX, y,   z).color(red1, green1, blue1, alpha1).endVertex();
        vb.pos(x,   y,   z).color(red1, green1, blue1, alpha1).endVertex();
        vb.pos(x,   toY, z).color(red2, green2, blue2, alpha2).endVertex();
        vb.pos(toX, toY, z).color(red2, green2, blue2, alpha2).endVertex();
        tes.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void renderFacingFullQuad(double px, double py, double pz, float partialTicks, float scale/*, float angle*/) {
        renderFacingQuad(px, py, pz, partialTicks, scale, 0, 0, 1, 1);
    }

    public static void renderFacingQuad(double px, double py, double pz, float partialTicks, float scale/*, float angle*/, double u, double v, double uLength, double vLength) {
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

        GL11.glTranslated(-iPX, -iPY, -iPZ);

        Vector3 v1 = new Vector3(-arX * scale - arYZ * scale, -arXZ * scale, -arZ * scale - arXY * scale);
        Vector3 v2 = new Vector3(-arX * scale + arYZ * scale,  arXZ * scale, -arZ * scale + arXY * scale);
        Vector3 v3 = new Vector3( arX * scale + arYZ * scale,  arXZ * scale,  arZ * scale + arXY * scale);
        Vector3 v4 = new Vector3( arX * scale - arYZ * scale, -arXZ * scale,  arZ * scale - arXY * scale);
        /*if (angle != 0.0F) {
            Vector3 pvec = new Vector3(iPX, iPY, iPZ);
            Vector3 tvec = new Vector3(px, py, pz);
            Vector3 qvec = pvec.subtract(tvec).normalize();
            Vector3.Quat q = Vector3.Quat.aroundAxis(qvec, angle);
            q.rotate(v1);
            q.rotate(v2);
            q.rotate(v3);
            q.rotate(v4);
        }*/
        Tessellator t = Tessellator.getInstance();
        VertexBuffer vb = t.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(px + v1.getX(), py + v1.getY(), pz + v1.getZ()).tex(u,           v + vLength).endVertex();
        vb.pos(px + v2.getX(), py + v2.getY(), pz + v2.getZ()).tex(u + uLength, v + vLength).endVertex();
        vb.pos(px + v3.getX(), py + v3.getY(), pz + v3.getZ()).tex(u + uLength, v          ).endVertex();
        vb.pos(px + v4.getX(), py + v4.getY(), pz + v4.getZ()).tex(u,           v          ).endVertex();
        t.draw();
    }

}
