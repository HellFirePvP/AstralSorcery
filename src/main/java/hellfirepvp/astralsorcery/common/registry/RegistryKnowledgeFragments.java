/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.GuiJournalPerkTree;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.IWeakConstellation;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment;
import hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragmentManager;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;
import net.minecraft.util.ResourceLocation;

import static hellfirepvp.astralsorcery.common.data.fragment.KnowledgeFragment.*;
import static hellfirepvp.astralsorcery.common.data.research.ResearchProgression.findNode;

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

        mgr.register(onResearchNodes("fragment.discovery.ancientshrine",
                findNode("SHRINES")));
        mgr.register(onResearchNodes("fragment.discovery.resowand",
                findNode("WAND")));
        mgr.register(onResearchNodes("fragment.misc.altar",
                findNode("ALTAR1"),
                findNode("ALTAR2"),
                findNode("ALTAR3"),
                findNode("ALTAR4")));
        mgr.register(onResearchNodes("fragment.discovery.startable",
                findNode("LINKTOOL"),
                findNode("LENS"),
                findNode("PRISM"),
                findNode("STARLIGHT_NETWORK"),
                findNode("COLL_CRYSTAL"),
                findNode("ENHANCED_COLLECTOR")));
        mgr.register(onResearchNodes("fragment.exploration.lightwellprod",
                findNode("WELL")));
        mgr.register(onResearchNodes("fragment.exploration.lightwelluses",
                findNode("WELL")));
        mgr.register(onResearchNodes("fragment.exploration.crystalgrowth",
                findNode("CRYSTAL_GROWTH")));
        mgr.register(onResearchNodes("fragment.exploration.crystaltools",
                findNode("TOOLS")));
        mgr.register(onResearchNodes("fragment.exploration.grindstone",
                findNode("GRINDSTONE")));
        mgr.register(onResearchNodes("fragment.misc.cannibalism",
                findNode("COLL_CRYSTAL"),
                findNode("ENHANCED_COLLECTOR"),
                findNode("SPEC_RELAY"),
                findNode("ALTAR1"),
                findNode("ALTAR2"),
                findNode("ALTAR3"),
                findNode("ALTAR4")));
        mgr.register(onResearchNodes("fragment.exploration.caveillumplace",
                findNode("ILLUMINATOR")));
        mgr.register(onResearchNodes("fragment.exploration.caveillumwand",
                findNode("ILLUMINATOR"),
                findNode("ILLUMINATION_WAND")));
        mgr.register(onResearchNodes("fragment.exploration.nocturnal",
                findNode("NOC_POWDER")));
        mgr.register(onResearchNodes("fragment.attunement.starlightchunks",
                findNode("LENS"),
                findNode("PRISM"),
                findNode("STARLIGHT_NETWORK"),
                findNode("COLL_CRYSTAL"),
                findNode("ENHANCED_COLLECTOR"),
                findNode("RIT_PEDESTAL")));
        mgr.register(onResearchNodes("fragment.attunement.alignmentcharge",
                findNode("QUICK_CHARGE"),
                findNode("TOOL_WANDS"),
                findNode("GRAPPLE_WAND")));
        mgr.register(onResearchNodes("fragment.attunement.attunement",
                findNode("ATT_PLAYER")));
        mgr.register(onResearchNodes("fragment.attunement.ritualpedestal",
                findNode("RIT_PEDESTAL"),
                findNode("PED_ACCEL")));
        mgr.register(onResearchNodes("fragment.constellation.colorlens",
                findNode("LENSES_EFFECTS")));
        mgr.register(onResearchNodes("fragment.constellation.refractiontable",
                findNode("DRAWING_TABLE")));
        mgr.register(onResearchNodes("fragment.constellation.treebeaconuse",
                findNode("TREEBEACON")));
        mgr.register(onResearchNodes("fragment.constellation.treebeaconboost",
                findNode("TREEBEACON")));
        mgr.register(onResearchNodes("fragment.constellation.inftool",
                findNode("CHARGED_TOOLS")));
        mgr.register(onResearchNodes("fragment.constellation.illumwand",
                findNode("ILLUMINATION_WAND")));
        mgr.register(onResearchNodes("fragment.constellation.prism",
                findNode("ENCHANTMENT_AMULET")));
        mgr.register(onResearchNodes("fragment.constellation.clusterbonus",
                findNode("CEL_CRYSTAL_GROW")));
        mgr.register(onResearchNodes("fragment.constellation.clusterspeed",
                findNode("CEL_CRYSTAL_GROW")));
        mgr.register(onResearchNodes("fragment.constellation.ec3",
                findNode("ENHANCED_COLLECTOR")));
        mgr.register(onResearchNodes("fragment.radiance.fysallidic",
                findNode("BORE_HEAD_VORTEX")));
        mgr.register(new KnowledgeFragment(new ResourceLocation(AstralSorcery.MODID, "fragment.misc.perks"), "gui.journal.bm.perks.name") {
            @Override
            public boolean isVisible(GuiScreenJournal journalGui) {
                return journalGui instanceof GuiJournalPerkTree;
            }
        }).setCanSeeTest(prog -> prog.getAttunedConstellation() != null);
        mgr.register(onResearchNodes("fragment.perk_gems",
                findNode("ATT_PERK_GEMS")));
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
