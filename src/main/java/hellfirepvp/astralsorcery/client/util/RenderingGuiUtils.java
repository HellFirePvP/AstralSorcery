/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.screen.base.WidthHeightScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Tuple;
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
    
    public static void drawTexturedRect(float offsetX, float offsetY, float zLevel, float width, float height, AbstractRenderableTexture tex) {
        Tuple<Float, Float> uv = tex.getUVOffset();
        drawTexturedRect(offsetX, offsetY, zLevel, width, height, uv.getA(), uv.getB(), tex.getUWidth(), tex.getVWidth());
    }
    
    public static void drawTexturedRectAtCurrentPos(float width, float height, float zLevel, float uFrom, float vFrom, float uWidth, float vWidth) {
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR, buf -> {
            rect(buf, 0, 0, zLevel, width, height)
                    .tex(uFrom, vFrom, uWidth, vWidth)
                    .draw();
        });
    }

    public static void drawTexturedRectAtCurrentPos(float width, float height, float zLevel) {
        drawTexturedRectAtCurrentPos(width, height, zLevel, 0, 0, 1, 1);
    }

    public static void drawRect(float offsetX, float offsetY, float zLevel, int width, int height) {
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR, buf -> {
            rect(buf, offsetX, offsetY, zLevel, width, height)
                    .draw();
        });
    }

    public static void drawTexturedRect(float offsetX, float offsetY, float zLevel, float width, float height, float uFrom, float vFrom, float uWidth, float vWidth) {
        RenderingUtils.draw(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR, buf -> {
            rect(buf, offsetX, offsetY, zLevel, width, height)
                    .tex(uFrom, vFrom, uWidth, vWidth)
                    .draw();
        });
    }

    public static DrawBuilder rect(WidthHeightScreen screen) {
        return rect(screen.getGuiLeft(), screen.getGuiTop(), screen.getGuiZLevel(), screen.getGuiWidth(), screen.getGuiHeight());
    }

    public static DrawBuilder rect(BufferBuilder buf, WidthHeightScreen screen) {
        return rect(buf, screen.getGuiLeft(), screen.getGuiTop(), screen.getGuiZLevel(), screen.getGuiWidth(), screen.getGuiHeight());
    }

    public static DrawBuilder rect(float offsetX, float offsetY, float offsetZ, float width, float height) {
        return rect(Tessellator.getInstance().getBuffer(), offsetX, offsetY, offsetZ, width, height);
    }

    public static DrawBuilder rect(BufferBuilder buf, float offsetX, float offsetY, float offsetZ, float width, float height) {
        return new DrawBuilder(buf, offsetX, offsetY, offsetZ, width, height);
    }

    public static class DrawBuilder {

        private final BufferBuilder buf;
        private float offsetX, offsetY, offsetZ;
        private float width, height;
        private float u = 0F, v = 0F, uWidth = 1F, vWidth = 1F;
        private Color color = Color.WHITE;

        private DrawBuilder(BufferBuilder buf, float offsetX, float offsetY, float offsetZ, float width, float height) {
            this.buf = buf;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
            this.width = width;
            this.height = height;
        }

        public DrawBuilder at(float offsetX, float offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        public DrawBuilder zLevel(float offsetZ) {
            this.offsetZ = offsetZ;
            return this;
        }

        public DrawBuilder dim(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
        }

        public DrawBuilder tex(AbstractRenderableTexture texture) {
            Tuple<Float, Float> uv = texture.getUVOffset();
            return this.tex(uv.getA(), uv.getB(), texture.getUWidth(), texture.getVWidth());
        }

        public DrawBuilder tex(float u, float v, float uWidth, float vWidth) {
            this.u = u;
            this.v = v;
            this.uWidth = uWidth;
            this.vWidth = vWidth;
            return this;
        }

        public DrawBuilder color(Color color) {
            this.color = color;
            return this;
        }

        public DrawBuilder color(int color) {
            return this.color(new Color(color, true));
        }

        public DrawBuilder color(int r, int g, int b, int a) {
            return this.color(new Color(r, g, b, a));
        }

        public DrawBuilder color(float r, float g, float b, float a) {
            return this.color(new Color(r, g, b, a));
        }

        public DrawBuilder draw() {
            int r = this.color.getRed();
            int g = this.color.getGreen();
            int b = this.color.getBlue();
            int a = this.color.getAlpha();
            buf.pos(offsetX,         offsetY + height, offsetZ).tex(u, v + vWidth).color(r, g, b, a).endVertex();
            buf.pos(offsetX + width, offsetY + height, offsetZ).tex(u + uWidth, v + vWidth).color(r, g, b, a).endVertex();
            buf.pos(offsetX + width, offsetY,          offsetZ).tex(u + uWidth, v).color(r, g, b, a).endVertex();
            buf.pos(offsetX,         offsetY,          offsetZ).tex(u, v).color(r, g, b, a).endVertex();
            return this;
        }

        public Rectangle.Float getDrawRectangle() {
            return this.getDrawRectangle(1F);
        }

        public Rectangle.Float getDrawRectangle(float scale) {
            return new Rectangle.Float(this.offsetX * scale, this.offsetY * scale, this.width * scale, this.height * scale);
        }
    }
}
