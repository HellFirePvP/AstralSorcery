/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.element;

import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.screen.tome.TomePagesScreen;
import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.sounds.SoundManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeSearchEntryElement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TomeSearchEntryElement extends AbstractWidget {

    private final TomeResearchScreen screen;
    private final ResearchNode node;

    public TomeSearchEntryElement(int x, int y, TomeResearchScreen screen, ResearchNode node, int width) {
        super(x, y, width, 12, node.getName());
        this.screen = screen;
        this.node = node;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.drawString(font, this.node.getName(), this.getX() + 2, this.getY() + 2, 0x00D0D0D0, false);
        if (this.isHoveredOrFocused()) {
            double effectPart = (Math.sin(Math.toRadians(((ClientProxy.getClientTick()) * 5D) % 360D)) + 1D) / 2D;
            int alpha = Math.round((0.45F + 0.1F * ((float) effectPart)) * 255F);
            int grayScale = Math.round((0.7F + 0.2F * ((float) effectPart)) * 255F);
            ColorWrapper boxColor = ColorWrapper.of(grayScale, grayScale, grayScale, alpha);

            guiGraphics.fill(RenderType.gui(),
                    this.getX(),
                    this.getY(),
                    this.getX() + this.getWidth(),
                    this.getY() + this.getHeight(),
                    boxColor.getColor());
        }
    }

    @Override
    public void playDownSound(SoundManager handler) {
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        Minecraft.getInstance().setScreen(TomePagesScreen.fromProgressNode(this.screen, this.node));
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }
}
