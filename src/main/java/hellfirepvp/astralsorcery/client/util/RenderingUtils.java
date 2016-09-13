package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingUtils
 * Created by HellFirePvP
 * Date: 29.08.2016 / 16:51
 */
public class RenderingUtils {

    public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int color2) {
        renderTooltip(x, y, tooltipData, color, color2, Minecraft.getMinecraft().fontRendererObj);
    }

    public static void renderTooltip(int x, int y, List<String> tooltipData, int color, int color2, FontRenderer fontRenderer) {
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

            GL11.glDisable(GL11.GL_DEPTH_TEST);
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

            for (int i = 0; i < tooltipData.size(); ++i) {
                String var14 = tooltipData.get(i);
                fontRenderer.drawStringWithShadow(var14, pX, pY, -1);
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
        vb.pos(toX, y, z).color(red1, green1, blue1, alpha1).endVertex();
        vb.pos(x,   y, z).color(red1, green1, blue1, alpha1).endVertex();
        vb.pos(x, toY, z).color(red2, green2, blue2, alpha2).endVertex();
        vb.pos(x, y,   z).color(red2, green2, blue2, alpha2).endVertex(); //TODO Check botania 1.8 src.
        tes.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
