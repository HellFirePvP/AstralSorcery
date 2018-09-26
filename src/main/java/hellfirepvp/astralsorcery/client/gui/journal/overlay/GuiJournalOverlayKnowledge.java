/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui.journal.overlay;

import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournalOverlay;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournalOverlayKnowledge
 * Created by HellFirePvP
 * Date: 26.09.2018 / 12:51
 */
public class GuiJournalOverlayKnowledge extends GuiScreenJournalOverlay {

    //TODO finish once textures are added..

    private final KnowledgeFragment knowledgeFragment;

    public GuiJournalOverlayKnowledge(GuiScreenJournal origin, KnowledgeFragment display) {
        super(origin);
        this.knowledgeFragment = display;
    }

    public KnowledgeFragment getKnowledgeFragment() {
        return knowledgeFragment;
    }

}
