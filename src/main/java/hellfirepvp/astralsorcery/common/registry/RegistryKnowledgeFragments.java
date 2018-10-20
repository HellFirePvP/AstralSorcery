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
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
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

        ConstellationRegistry.getAllConstellations().forEach(RegistryKnowledgeFragments::registerConstellationFragment);
    }

    private static void registerConstellationFragment(IConstellation cst) {
        KnowledgeFragmentManager mgr = KnowledgeFragmentManager.getInstance();
        String cstKey = "fragment.constellation." + cst.getSimpleName();

        mgr.register(onConstellations(cstKey + ".showup", cst));
        mgr.register(onConstellations(cstKey + ".potions", cst)
                .setCanSeeTest(hasTier(ProgressionTier.CONSTELLATION_CRAFT)));
        mgr.register(onConstellations(cstKey + ".enchantments", cst)
                .setCanSeeTest(hasTier(ProgressionTier.CONSTELLATION_CRAFT)));

        if (cst instanceof IWeakConstellation) {
            mgr.register(onConstellations(cstKey + ".ritual", cst)
                    .setCanSeeTest(hasTier(ProgressionTier.ATTUNEMENT)));
            mgr.register(onConstellations(cstKey + ".ritual.corrupted", cst)
                    .setCanSeeTest(hasTier(ProgressionTier.TRAIT_CRAFT)));
            mgr.register(onConstellations(cstKey + ".mantle", cst)
                    .setCanSeeTest(hasTier(ProgressionTier.CONSTELLATION_CRAFT)));
        } else if (cst instanceof IMinorConstellation) {
            mgr.register(onConstellations(cstKey + ".trait", cst)
                    .setCanSeeTest(hasTier(ProgressionTier.CONSTELLATION_CRAFT)));
        }
    }

}
