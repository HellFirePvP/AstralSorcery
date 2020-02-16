/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import hellfirepvp.astralsorcery.client.util.RenderingGuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WidthHeightScreen
 * Created by HellFirePvP
 * Date: 02.08.2019 / 20:33
 */
public class WidthHeightScreen extends InputScreen {

    protected final int guiHeight;
    protected final int guiWidth;
    protected int guiLeft, guiTop;

    protected boolean closeWithInventoryKey = true;

    protected WidthHeightScreen(ITextComponent titleIn, int guiHeight, int guiWidth) {
        super(titleIn);
        this.guiHeight = guiHeight;
        this.guiWidth = guiWidth;
    }

    @Override
    protected void init() {
        super.init();

        this.initComponents();
    }

    public int getGuiHeight() {
        return guiHeight;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    public int getGuiTop() {
        return guiTop;
    }

    public int getGuiWidth() {
        return guiWidth;
    }

    public Rectangle getGuiBox() {
        return new Rectangle(this.getGuiLeft(), this.getGuiTop(), this.getGuiWidth(), this.getGuiHeight());
    }

    private void initComponents() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
    }

    protected void drawWHRect(AbstractRenderableTexture resource) {
        resource.bindTexture();
        RenderingGuiUtils.drawRect(guiLeft, guiTop, this.blitOffset, guiWidth, guiHeight);
    }

    @Override
    public boolean charTyped(char charCode, int keyModifiers) {
        if (super.charTyped(charCode, keyModifiers)) {
            return true;
        }

        if (closeWithInventoryKey && Minecraft.getInstance().gameSettings.keyBindInventory.isKeyDown()) {
            this.onClose();

            if (Minecraft.getInstance().currentScreen == null) {
                Minecraft.getInstance().mouseHelper.grabMouse();
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (button == 1 && shouldRightClickCloseScreen(mouseX, mouseY)) {
            this.onClose();

            if (Minecraft.getInstance().currentScreen == null) {
                Minecraft.getInstance().mouseHelper.grabMouse();
            }
            return true;
        }
        return false;
    }

    /**
     * @return false if rightclick shouldn't close the current screen, true if it should close the current screen
     */
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return false;
    }

    protected DrawBuilder drawRect() {
        return this.drawRect(Tessellator.getInstance().getBuffer());
    }

    protected DrawBuilder drawRect(BufferBuilder buf) {
        return new DrawBuilder(buf, this);
    }

    protected static class DrawBuilder {

        private final BufferBuilder buf;
        private float offsetX, offsetY, offsetZ;
        private float width, height;
        private float u = 0F, v = 0F, uWidth = 1F, vWidth = 1F;
        private Color color = Color.WHITE;

        private DrawBuilder(BufferBuilder buf, WidthHeightScreen screen) {
            this.buf = buf;
            this.offsetX = screen.getGuiLeft();
            this.offsetY = screen.getGuiTop();
            this.width = screen.getGuiWidth();
            this.height = screen.getGuiHeight();
            this.offsetZ = screen.blitOffset;
        }

        public DrawBuilder at(float offsetX, float offsetY) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            return this;
        }

        public DrawBuilder dim(float width, float height) {
            this.width = width;
            this.height = height;
            return this;
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

        public Rectangle getDrawRectangle() {
            return new Rectangle((int) this.offsetX, (int) this.offsetY, (int) this.width, (int) this.height);
        }
    }
}
