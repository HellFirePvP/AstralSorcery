/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.base;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: InputScreen
 * Created by HellFirePvP
 * Date: 10.08.2019 / 17:18
 */
public class InputScreen extends Screen {

    private Set<Integer> heldKeys = new HashSet<>();

    private double oMouseX, oMouseY;
    private boolean dragging = false;

    protected InputScreen(ITextComponent name) {
        super(name);
    }

    @Override
    public void tick() {
        heldKeys.forEach(this::keyPressedTick);

        super.tick();
    }

    protected void keyPressedTick(int key) {}

    protected void mouseDragStart(double mouseX, double mouseY) {}

    protected void mouseDragStop(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY) {}

    protected void mouseDragTick(double mouseX, double mouseY, double mouseDiffX, double mouseDiffY, double mouseOffsetX, double mouseOffsetY) {}

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        heldKeys.add(key);
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int key, int scanCode, int modifiers) {
        heldKeys.remove(key);
        return super.keyReleased(key, scanCode, modifiers);
    }

    public boolean isCurrentlyDragging() {
        return this.dragging;
    }

    protected void stopDragging(double mouseX, double mouseY) {
        if (this.dragging) {
            this.dragging = false;
            this.mouseDragStop(mouseX, mouseY, oMouseX, oMouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int click) {
        if (click == 0) {
            this.dragging = true;
            this.oMouseX = mouseX;
            this.oMouseY = mouseY;
            this.mouseDragStart(mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int click) {
        if (click == 0) {
            this.stopDragging(mouseX, mouseY);
        }
        return super.mouseReleased(mouseX, mouseY, click);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int clickType, double offsetX, double offsetY) {
        if (clickType == 0 && this.dragging) {
            double diffX = this.oMouseX - mouseX;
            double diffY = this.oMouseY - mouseY;
            this.mouseDragTick(mouseX, mouseY, diffX, diffY, offsetX, offsetY);
        }
        return super.mouseDragged(mouseX, mouseY, clickType, offsetX, offsetY);
    }
}
