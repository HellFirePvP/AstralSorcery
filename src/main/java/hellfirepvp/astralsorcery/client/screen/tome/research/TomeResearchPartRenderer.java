/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.tome.research;

import hellfirepvp.astralsorcery.client.screen.tome.TomeResearchScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TomeResearchPartRenderer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class TomeResearchPartRenderer {

    private final TomeResearchScreen screen;
    private ScreenRectangle renderBoundingBox = ScreenRectangle.empty();

    public TomeResearchPartRenderer(TomeResearchScreen screen) {
        this.screen = screen;
    }

    public TomeResearchScreen getParentScreen() {
        return screen;
    }

    public void setViewBox(ScreenRectangle rect) {
        this.renderBoundingBox = rect;
    }

    protected ScreenRectangle getRenderBoundingBox() {
        return this.renderBoundingBox;
    }

    public abstract void refreshView();

    public abstract void draw(GuiGraphics guiGraphics, Runnable renderWidgets, float mouseX, float mouseY, float pTicks);

    public abstract boolean mouseClick(double mouseX, double mouseY);

    public abstract boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta);
}
