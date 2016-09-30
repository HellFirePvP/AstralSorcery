package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageStructure;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageText;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
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
        ResearchNode test1 = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "UnlocName", 1, 1).setSpecial();
        ResearchNode test2 = new ResearchNode(new ItemStack(ItemsAS.constellationPaper), "UnlocName2", 3, 3).setSpecial();
        ResearchNode test3 = new ResearchNode(new ItemStack(BlocksAS.blockMarble, 1, BlockMarble.MarbleBlockType.CHISELED.ordinal()), "UnlocName2", 4, -2).setSpecial();
        test1.addPage(new JournalPageText("astralsorcery.journal.text.test"));
        BlockArray ba = new BlockArray();
        ba.addBlockCube(BlocksAS.blockMarble.getDefaultState(), -2, 0, -2, 2, 0, 2);
        ba.addBlock(0, 1, 0, BlocksAS.ritualPedestal.getDefaultState());
        test1.addPage(new JournalPageStructure(ba));
        test1.registerTo(ResearchProgression.TEST_PROGRESS);
        test2.registerTo(ResearchProgression.TEST_PROGRESS);
        test3.registerTo(ResearchProgression.TEST_PROGRESS);

        test2.addConnectionTo(test1);
        test3.addConnectionTo(test2);
        test3.addConnectionTo(test1);
    }

}
