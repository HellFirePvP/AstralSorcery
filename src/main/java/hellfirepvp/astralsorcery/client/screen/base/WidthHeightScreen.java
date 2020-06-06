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
import net.minecraft.util.text.ITextComponent;

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

    public int getGuiLeft() {
        return this.guiLeft;
    }

    public int getGuiTop() {
        return this.guiTop;
    }

    public int getGuiZLevel() {
        return this.getBlitOffset();
    }

    public void changeZLevel(int change) {
        this.setBlitOffset(this.getBlitOffset() + change);
    }

    public int getGuiWidth() {
        return this.guiWidth;
    }

    public int getGuiHeight() {
        return this.guiHeight;
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
        RenderingGuiUtils.drawRect(guiLeft, guiTop, this.getBlitOffset(), guiWidth, guiHeight);
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

    protected RenderingGuiUtils.DrawBuilder drawRect(BufferBuilder buf) {
        return RenderingGuiUtils.rect(buf, this);
    }
}
