/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.lib.TexturesAS;
import hellfirepvp.astralsorcery.client.util.RenderingDrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TextInputElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SearchInputElement extends AbstractWidget {

    private String text = "";
    private final Runnable changeCallback;
    private final TextFieldHelper inputUtil;

    public SearchInputElement(int x, int y, Runnable changeCallback) {
        super(x, y, 88, 15, Component.translatable("screen.astralsorcery.element.input.search"));
        this.changeCallback = changeCallback;
        this.inputUtil = new TextFieldHelper(
                this::getText,
                this::setText,
                TextFieldHelper.createClipboardGetter(Minecraft.getInstance()),
                TextFieldHelper.createClipboardSetter(Minecraft.getInstance()),
                (text) -> text.length() < 256);
    }

    public void setText(@Nullable String newText) {
        if (newText == null) newText = "";
        String prevText = this.text;
        this.text = newText;
        if (!newText.equals(prevText) && this.changeCallback != null) {
            this.changeCallback.run();
        }
    }

    @Nonnull
    public String getText() {
        return text;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        RenderingDrawUtil.drawTexturedRect(guiGraphics.pose(), TexturesAS.SCREEN_TOME_SEARCH_TEXT_INPUT, this.getRectangle());
        RenderSystem.disableBlend();

        Font font = Minecraft.getInstance().font;
        String drawnText = this.getText();

        int length = font.width(drawnText);
        boolean addDots = length > 75;
        while (length > 75) {
            drawnText = drawnText.substring(1);
            length = font.width("..." + drawnText);
        }
        if (addDots) {
            drawnText = "..." + drawnText;
        }
        if ((ClientProxy.getClientTick() % 20) > 10) {
            drawnText += "_";
        }
        guiGraphics.drawString(font, drawnText, this.getX() + 4, this.getY() + 4, 0xCCCCCC);
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (key == GLFW.GLFW_KEY_ESCAPE ||
                key == GLFW.GLFW_KEY_ENTER ||
                key == GLFW.GLFW_KEY_KP_ENTER ||
                key == GLFW.GLFW_KEY_HOME ||
                key == GLFW.GLFW_KEY_END ||
                key == GLFW.GLFW_KEY_INSERT ||
                key == GLFW.GLFW_KEY_DELETE) {
            return false;
        }
        //Arrow keys
        if (key >= GLFW.GLFW_KEY_RIGHT && key <= GLFW.GLFW_KEY_UP) {
            return false;
        }
        return this.inputUtil.keyPressed(key);
    }

    @Override
    public boolean charTyped(char charCode, int keyModifiers) {
        return this.inputUtil.charTyped(charCode);
    }
}
