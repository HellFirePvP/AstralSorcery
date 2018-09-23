/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryKnowledgeFragments
 * Created by HellFirePvP
 * Date: 23.09.2018 / 13:42
 */
public class RegistryKnowledgeFragments {

    public static void init() {
        KnowledgeFragmentManager mgr = KnowledgeFragmentManager.getInstance();

        //Example Constellation info
        mgr.register(onConstellations("constellation.lucerna.ritual.corrupted", Constellations.lucerna))
                .setCanSeeTest(hasTier(ProgressionTier.TRAIT_CRAFT));

        //Example Research info
        mgr.register(onResearchNodes("research.chalice.interactions", ResearchProgression.findNode("C_CHALICE")));

        //Example PerkTree General info
        mgr.register(new KnowledgeFragment("perktree.pathing.info") {
            @Override
            @SideOnly(Side.CLIENT)
            public boolean isVisible(GuiScreenJournal journalGui) {
                return journalGui instanceof GuiJournalPerkTree;
            }
        });
    }

}
