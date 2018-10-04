/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;

import java.awt.*;
import java.io.IOException;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalKnowledgeIndex
 * Created by HellFirePvP
 * Date: 04.10.2018 / 21:23
 */
public class GuiJournalKnowledgeIndex extends GuiScreenJournal {

    public GuiJournalKnowledgeIndex() {
        super(3);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefault(textureResBlank);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);

        if (handleBookmarkClick(p)) {
            return;
        }
    }
}
