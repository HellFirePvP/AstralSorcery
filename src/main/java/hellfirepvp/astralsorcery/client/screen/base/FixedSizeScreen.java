/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FixedSizeScreen
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FixedSizeScreen extends Screen {

    protected final int screenHeight;
    protected final int screenWidth;
    protected int screenLeft, screenTop;

    protected double draggedMouseOffsetX, draggedMouseOffsetY;

    protected FixedSizeScreen(Component title, int screenHeight, int screenWidth) {
        super(title);
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.updateScreenPosition();
    }

    @Override
    protected void init() {
        super.init();
        this.updateScreenPosition();
    }

    protected void updateScreenPosition() {
        this.screenLeft = (this.width - this.screenWidth) / 2;
        this.screenTop = (this.height - this.screenHeight) / 2;
    }

    protected void stopDragging(double mouseX, double mouseY) {
        if (this.isDragging()) {
            this.setDragging(false);
            this.mouseDragStop(mouseX, mouseY, this.draggedMouseOffsetX, this.draggedMouseOffsetY);
        }
    }

    protected void startDragging(double mouseX, double mouseY) {
        if (!this.isDragging()) {
            this.setDragging(true);
            this.draggedMouseOffsetX = mouseX;
            this.draggedMouseOffsetY = mouseY;
            this.mouseDragStart(mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (this.shouldInventoryKeyCloseScreen() && Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1 && this.shouldRightClickCloseScreen(mouseX, mouseY)) {
            this.onClose();
            return true;
        }
        boolean usedClick = super.mouseClicked(mouseX, mouseY, button);
        if (!usedClick && button == 0) {
            this.startDragging(mouseX, mouseY);
        } else {
            this.stopDragging(mouseX, mouseY);
        }
        return usedClick;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && this.isDragging()) {
            double diffX = this.draggedMouseOffsetX - mouseX;
            double diffY = this.draggedMouseOffsetY - mouseY;
            this.mouseDragTick(mouseX, mouseY, diffX, diffY, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.stopDragging(mouseX, mouseY);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /**
     * @return false if rightclick shouldn't close the current screen, true if it should close the current screen
     */
    protected boolean shouldRightClickCloseScreen(double mouseX, double mouseY) {
        return false;
    }

    protected boolean shouldInventoryKeyCloseScreen() {
        return false;
    }

    protected void mouseDragStart(double mouseX, double mouseY) {}

    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {}

    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {}

    public int getScreenLeft() {
        return this.screenLeft;
    }

    public int getScreenTop() {
        return this.screenTop;
    }

    public int getScreenWidth() {
        return this.screenWidth;
    }

    public int getScreenHeight() {
        return this.screenHeight;
    }

    public ScreenRectangle getScreenRectangle() {
        return new ScreenRectangle(this.screenLeft, this.screenTop, this.screenWidth, this.screenHeight);
    }
}
