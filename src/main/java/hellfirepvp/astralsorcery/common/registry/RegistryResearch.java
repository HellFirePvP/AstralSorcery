package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageDiscoveryRecipe;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageStructure;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageText;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryResearch
 * Created by HellFirePvP
 * Date: 12.08.2016 / 20:47
 */
public class RegistryResearch {

    //Only non-negative numbers please :V
    public static void init() {
        ResearchProgression.Registry resDiscovery = ResearchProgression.DISCOVERY.getRegistry();

        ResearchNode test1 = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "UnlocName", 1, 1);
        ResearchNode test2 = new ResearchNode(new ItemStack(ItemsAS.constellationPaper), "UnlocName2", 3, 3);
        ResearchNode test3 = new ResearchNode(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()), "UnlocName2", 5, 2);
        test1.addPage(new JournalPageText("astralsorcery.journal.text.test"));
        test1.addPage(new JournalPageStructure(MultiBlockArrays.patternRitualPedestal));
        test2.addPage(new JournalPageText("astralsorcery.journal.text.test2"));
        test2.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rAltar));

        resDiscovery.register(test1);
        resDiscovery.register(test2);
        resDiscovery.register(test3);

        test2.addConnectionTo(test1);
        test3.addConnectionTo(test2);
        test3.addConnectionTo(test1);

        ResearchProgression.Registry resStarlight = ResearchProgression.STARLIGHT.getRegistry();

        ResearchNode test4 = new ResearchNode(new ItemStack(BlocksAS.ritualPedestal), "UnlocName3", 1, 1);

        resStarlight.register(test4);
    }

}
