/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal;

import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.gui.base.GuiWHScreen;
import net.minecraft.client.Minecraft;

import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiScreenJournalOverlay
 * Created by HellFirePvP
 * Date: 25.09.2018 / 12:45
 */
public class GuiScreenJournalOverlay extends GuiWHScreen {

    private final GuiScreenJournal origin;

    protected GuiScreenJournalOverlay(GuiScreenJournal origin) {
        super(origin.getGuiHeight(), origin.getGuiWidth());
        this.origin = origin;
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return origin.doesGuiPauseGame();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);

        origin.setWorldAndResolution(mc, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        origin.drawScreen(0, 0, partialTicks);
    }

    @Override
    protected boolean handleRightClickClose(int mouseX, int mouseY) {
        Minecraft.getMinecraft().displayGuiScreen(origin);
        return true;
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        if (origin instanceof GuiJournalProgression) {
            ((GuiJournalProgression) origin).expectReinit = true;
        }
        if (origin instanceof GuiJournalPerkTree) {
            ((GuiJournalPerkTree) origin).expectReinit = true;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (Minecraft.getMinecraft().currentScreen != this &&
                Minecraft.getMinecraft().currentScreen != origin) { //Something changed..
            Minecraft.getMinecraft().displayGuiScreen(origin);
        }
    }
}
