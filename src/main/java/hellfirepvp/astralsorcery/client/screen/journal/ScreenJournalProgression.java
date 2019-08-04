/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.journal;

import hellfirepvp.astralsorcery.client.screen.journal.progression.ScreenJournalProgressionRenderer;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ScreenJournalProgression
 * Created by HellFirePvP
 * Date: 03.08.2019 / 16:48
 */
public class ScreenJournalProgression extends ScreenJournal {

    private static ScreenJournalProgression currentInstance = null;
    private boolean expectReinit = false;
    private boolean rescaleAndRefresh = true;

    private static ScreenJournalProgressionRenderer progressionRenderer;

    private int bufX, bufY;
    private boolean dragging = false;

    private ScreenJournalProgression() {
        super(new TranslationTextComponent("gui.journal.progression"), 10);
    }

    public static ScreenJournalProgression getJournalInstance() {
        if(currentInstance != null) {
            return currentInstance;
        }
        return new ScreenJournalProgression();
    }

    public static ScreenJournal getOpenJournalInstance() {
        ScreenJournal gui = ScreenJournalPages.getClearOpenGuiInstance();
        if(gui == null) {
            gui = getJournalInstance();
        }
        return gui;
    }

    public void expectReInit() {
        this.expectReinit = true;
    }

    public void preventRefresh() {
        this.rescaleAndRefresh = false;
    }

    //TODO on disconnect
    public static void resetJournal() {
        currentInstance = null;
        ScreenJournalPages.getClearOpenGuiInstance();
    }

    @Override
    public void removed() {
        super.removed();
        rescaleAndRefresh = false;
    }

    @Override
    protected void init() {
        super.init();

        if(expectReinit) {
            expectReinit = false;
            return; //We ASSUME, that the state is valid.
        }

        if(currentInstance == null || progressionRenderer == null) {
            currentInstance = this;
            progressionRenderer = new ScreenJournalProgressionRenderer(currentInstance, guiHeight - 10, guiWidth - 10);
            progressionRenderer.centerMouse();
        }

        progressionRenderer.updateOffset(guiLeft + 10, guiTop + 10);
        progressionRenderer.setBox(10, 10, guiWidth - 10, guiHeight - 10);
        //progressionRenderer.resetOverlayText();

        if(rescaleAndRefresh) {
            progressionRenderer.resetZoom();
            progressionRenderer.unfocus();
            progressionRenderer.refreshSize();
            progressionRenderer.updateMouseState();
        } else {
            rescaleAndRefresh = true;
        }
    }
}
