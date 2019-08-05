/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import hellfirepvp.astralsorcery.client.resource.AbstractRenderableTexture;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WidthHeightScreen
 * Created by HellFirePvP
 * Date: 02.08.2019 / 20:33
 */
public class WidthHeightScreen extends Screen {

    protected final int guiHeight;
    protected final int guiWidth;
    protected int guiLeft, guiTop;

    protected boolean closeWithInventoryKey = true;

    protected WidthHeightScreen(ITextComponent titleIn, int guiHeight, int guiWidth) {
        super(titleIn);
        this.passEvents = true;
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

    private void initComponents() {
        guiLeft = width / 2 - guiWidth / 2;
        guiTop = height / 2 - guiHeight / 2;
    }

    protected void drawWHRect(AbstractRenderableTexture resource) {
        resource.bindTexture();
        drawRect(guiLeft, guiTop, guiWidth, guiHeight);
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

        if (button == 1 && !handleRightClickClose(mouseX, mouseY)) {
            this.onClose();

            if (Minecraft.getInstance().currentScreen == null) {
                Minecraft.getInstance().mouseHelper.grabMouse();
            }
            return true;
        }
        return false;
    }

    /**
     * @return false if rightclick is not handled any other way and allow for close. true to deny rightclick close and handle otherwise;
     */
    protected boolean handleRightClickClose(double mouseX, double mouseY) {
        return false;
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, Rectangle.Float uvBounds) {
        drawTexturedRect(offsetX, offsetY, width, height, uvBounds.x, uvBounds.y, uvBounds.width, uvBounds.height);
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, float uFrom, float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, this.blitOffset).tex(uFrom,          vFrom + vWidth).endVertex();
        vb.pos(offsetX + width, offsetY + height, this.blitOffset).tex(uFrom + uWidth, vFrom + vWidth).endVertex();
        vb.pos(offsetX + width, offsetY,          this.blitOffset).tex(uFrom + uWidth, vFrom)         .endVertex();
        vb.pos(offsetX,         offsetY,          this.blitOffset).tex(uFrom,          vFrom)         .endVertex();
        tes.draw();
    }

    protected void drawTexturedRect(double offsetX, double offsetY, double width, double height, AbstractRenderableTexture tex) {
        Point.Double off = tex.getUVOffset();
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, this.blitOffset).tex(off.x,          off.y + tex.getVWidth()).endVertex();
        vb.pos(offsetX + width, offsetY + height, this.blitOffset).tex(off.x + tex.getUWidth(), off.y + tex.getVWidth()).endVertex();
        vb.pos(offsetX + width, offsetY,          this.blitOffset).tex(off.x + tex.getUWidth(), off.y)         .endVertex();
        vb.pos(offsetX,         offsetY,          this.blitOffset).tex(off.x,          off.y)         .endVertex();
        tes.draw();
    }

    protected void drawTexturedRectAtCurrentPos(double width, double height, float uFrom, float vFrom, float uWidth, float vWidth) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,         0 + height, this.blitOffset).tex(uFrom,          vFrom + vWidth).endVertex();
        vb.pos(0 + width, 0 + height, this.blitOffset).tex(uFrom + uWidth, vFrom + vWidth).endVertex();
        vb.pos(0 + width, 0,          this.blitOffset).tex(uFrom + uWidth, vFrom)         .endVertex();
        vb.pos(0,         0,          this.blitOffset).tex(uFrom,          vFrom)         .endVertex();
        tes.draw();
    }

    protected void drawTexturedRectAtCurrentPos(double width, double height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(0,         0 + height, this.blitOffset).tex(0, 1).endVertex();
        vb.pos(0 + width, 0 + height, this.blitOffset).tex(1, 1).endVertex();
        vb.pos(0 + width, 0,          this.blitOffset).tex(1, 0).endVertex();
        vb.pos(0,         0,          this.blitOffset).tex(0, 0).endVertex();
        tes.draw();
    }

    protected void drawRectDetailed(float offsetX, float offsetY, float width, float height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, this.blitOffset).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, this.blitOffset).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          this.blitOffset).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          this.blitOffset).tex(0, 0).endVertex();
        tes.draw();
    }

    protected void drawRect(int offsetX, int offsetY, int width, int height) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        vb.begin(7, DefaultVertexFormats.POSITION_TEX);
        vb.pos(offsetX,         offsetY + height, this.blitOffset).tex(0, 1).endVertex();
        vb.pos(offsetX + width, offsetY + height, this.blitOffset).tex(1, 1).endVertex();
        vb.pos(offsetX + width, offsetY,          this.blitOffset).tex(1, 0).endVertex();
        vb.pos(offsetX,         offsetY,          this.blitOffset).tex(0, 0).endVertex();
        tes.draw();
    }

}
