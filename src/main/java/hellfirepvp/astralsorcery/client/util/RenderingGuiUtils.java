/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingGuiUtils
 * Created by HellFirePvP
 * Date: 26.08.2019 / 19:31
 */
public class RenderingGuiUtils {

    public static void drawTexturedRect(double offsetX, double offsetY, double zLevel, double width, double height, float uFrom, float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(uFrom,          vFrom + vWidth).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(uFrom + uWidth, vFrom + vWidth).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(uFrom + uWidth, vFrom)         .endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(uFrom,          vFrom)         .endVertex();
        tes.draw();
    }
    
    public static void drawTexturedRect(double offsetX, double offsetY, double zLevel, double width, double height, AbstractRenderableTexture tex) {
        Point.Double off = tex.getUVOffset();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(off.x,          off.y + tex.getVWidth()).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(off.x + tex.getUWidth(), off.y + tex.getVWidth()).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(off.x + tex.getUWidth(), off.y)         .endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(off.x,          off.y)         .endVertex();
        tes.draw();
    }
    
    public static void drawTexturedRectAtCurrentPos(double width, double height, double zLevel, float uFrom, float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,         0 + height, zLevel).tex(uFrom,          vFrom + vWidth).endVertex();
        vb.pos(0 + width, 0 + height, zLevel).tex(uFrom + uWidth, vFrom + vWidth).endVertex();
        vb.pos(0 + width, 0,          zLevel).tex(uFrom + uWidth, vFrom)         .endVertex();
        vb.pos(0,         0,          zLevel).tex(uFrom,          vFrom)         .endVertex();
        tes.draw();
    }

    public static void drawTexturedRectAtCurrentPos(double width, double height, double zLevel) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,         0 + height, zLevel).tex(0, 1).endVertex();
        vb.pos(0 + width, 0 + height, zLevel).tex(1, 1).endVertex();
        vb.pos(0 + width, 0,          zLevel).tex(1, 0).endVertex();
        vb.pos(0,         0,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }
    
    public static void drawRectDetailed(float offsetX, float offsetY, double zLevel, float width, float height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }

    public static void drawRect(int offsetX, int offsetY, double zLevel, int width, int height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, zLevel).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, zLevel).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          zLevel).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          zLevel).tex(0, 0).endVertex();
        tes.draw();
    }
}
