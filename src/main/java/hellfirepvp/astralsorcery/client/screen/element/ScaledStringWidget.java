/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScaledStringWidget
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScaledStringWidget extends StringWidget {

    private final float scale;
    private float alignX = 0.5F;

    public ScaledStringWidget(float scale, Component message, Font font) {
        this(0, 0, font.width(message.getVisualOrderText()), 9, scale, message, font);
    }

    public ScaledStringWidget(int width, int height, float scale, Component message, Font font) {
        this(0, 0, width, height, scale, message, font);
    }

    public ScaledStringWidget(int x, int y, int width, int height, float scale, Component message, Font font) {
        super(x, y, Mth.ceil(width * scale), Mth.ceil(height * scale), message, font);
        this.scale = scale;
    }

    @Override
    public StringWidget alignLeft() {
        this.alignX = 0F;
        return super.alignLeft();
    }

    @Override
    public StringWidget alignCenter() {
        this.alignX = 0.5F;
        return super.alignCenter();
    }

    @Override
    public StringWidget alignRight() {
        this.alignX = 1F;
        return super.alignRight();
    }

    // Copied from StringWidget mostly
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Component component = this.getMessage();
        Font font = this.getFont();
        int width = this.getWidth();
        int strWidth = font.width(component);
        int offsetX = this.getX() + Math.round(this.alignX * (float)(width - strWidth));
        int offsetY = this.getY() + (this.getHeight() - font.lineHeight) / 2;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(offsetX, offsetY, 0);
        guiGraphics.pose().scale(this.scale, this.scale, 1);
        FormattedCharSequence formattedcharsequence = strWidth > width ? this.clipText(component, width) : component.getVisualOrderText();
        guiGraphics.drawString(font, formattedcharsequence, 0, 0, this.getColor());
        guiGraphics.pose().popPose();
    }

    private FormattedCharSequence clipText(Component message, int width) {
        Font font = this.getFont();
        FormattedText formattedtext = font.substrByWidth(message, width - font.width(CommonComponents.ELLIPSIS));
        return Language.getInstance().getVisualOrder(FormattedText.composite(formattedtext, CommonComponents.ELLIPSIS));
    }
}
