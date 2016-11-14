package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageAttenuationRecipe;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageConstellationRecipe;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageDiscoveryRecipe;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageLightProximityRecipe;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageRecipe;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageStructure;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageText;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.ItemEntityPlacer;
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

    public static void init() {
        initDiscovery();
        initCrafting();
        initAttenuation();
        initConstellation();
    }

    private static void initConstellation() {
        ResearchProgression.Registry regConstellation = ResearchProgression.CONSTELLATION.getRegistry();

        ResearchNode resLens = new ResearchNode(new ItemStack(BlocksAS.lens), "LENS", 0, 1);
        resLens.addPage(getTextPage("LENS.1"));
        resLens.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rLensRock));

        ResearchNode resLinkTool = new ResearchNode(new ItemStack(ItemsAS.linkingTool), "LINKTOOL", 1, 2);
        resLinkTool.addPage(getTextPage("LINKTOOL.1"));
        resLinkTool.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rLinkToolRock));

        ResearchNode resStarOre = new ResearchNode(BlockCustomOre.OreType.STARMETAL.asStack(), "STARMETAL_ORE", 2, 1);
        resStarOre.addPage(getTextPage("STARMETAL_ORE.1"));

        ResearchNode resStarResult = new ResearchNode(new ItemStack[] {
                ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),
                ItemCraftingComponent.MetaType.STARDUST.asStack()}, "STARMETAL_RES", 3, 2);
        resStarResult.addPage(getTextPage("STARMETAL_RES.1"));

        ResearchNode resPrism = new ResearchNode(new ItemStack(BlocksAS.lensPrism), "PRISM", 2, 3);
        resPrism.addPage(getTextPage("PRISM.1"));
        resPrism.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rPrismRock));

        ResearchNode resCollCrystal = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "COLL_CRYSTAL", 4, 3);
        resCollCrystal.addPage(getTextPage("COLL_CRYSTAL.1"));
        resCollCrystal.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rCollectRock));

        ResearchNode resCelCrystalCluster = new ResearchNode(new ItemStack(BlocksAS.celestialCrystals, 1, 3), "CEL_CRYSTAL_GROW", 2, 4);
        resCelCrystalCluster.addPage(getTextPage("CEL_CRYSTAL_GROW.1"));
        resCelCrystalCluster.addPage(getTextPage("CEL_CRYSTAL_GROW.2"));

        ResearchNode resCelCrystals = new ResearchNode(new ItemStack(ItemsAS.celestialCrystal), "CEL_CRYSTALS", 1, 5);
        resCelCrystals.addPage(getTextPage("CEL_CRYSTALS.1"));
        resCelCrystals.addPage(getTextPage("CEL_CRYSTALS.2"));

        resStarOre.addSourceConnectionFrom(resLinkTool);
        resStarOre.addSourceConnectionFrom(resLens);
        resPrism.addSourceConnectionFrom(resStarResult);
        resStarResult.addSourceConnectionFrom(resStarOre);
        resCollCrystal.addSourceConnectionFrom(resStarResult);
        resCelCrystalCluster.addSourceConnectionFrom(resStarResult);
        resCelCrystals.addSourceConnectionFrom(resCelCrystalCluster);

        regConstellation.register(resLens);
        regConstellation.register(resLinkTool);
        regConstellation.register(resStarOre);
        regConstellation.register(resStarResult);
        regConstellation.register(resPrism);
        regConstellation.register(resCollCrystal);
        regConstellation.register(resCelCrystalCluster);
        regConstellation.register(resCelCrystals);
    }

    private static void initAttenuation() {
        ResearchProgression.Registry regAttenuation = ResearchProgression.ATTENUATION.getRegistry();

        ResearchNode resWell = new ResearchNode(new ItemStack(BlocksAS.blockWell), "WELL", 0, 0);
        resWell.addPage(getTextPage("WELL.1"));
        resWell.addPage(new JournalPageAttenuationRecipe(RegistryRecipes.rLightwell));

        ResearchNode resIlluminator = new ResearchNode(new ItemStack(BlocksAS.blockIlluminator), "ILLUMINATOR", 1, -1);
        resIlluminator.addPage(getTextPage("ILLUMINATOR.1"));
        resIlluminator.addPage(new JournalPageAttenuationRecipe(RegistryRecipes.rIlluminatorRock));

        ResearchNode resSimpleAtt = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "ATT_SIMPLE", 0, 2);
        resSimpleAtt.addPage(getTextPage("ATT_SIMPLE.1"));
        resSimpleAtt.addPage(new JournalPageAttenuationRecipe(RegistryRecipes.rAttuneRockCrystalBasic));

        ResearchNode resRitPedestal = new ResearchNode(new ItemStack(BlocksAS.ritualPedestal), "RIT_PEDESTAL", 1, 4);
        resRitPedestal.addPage(getTextPage("RIT_PEDESTAL.1"));
        resRitPedestal.addPage(new JournalPageAttenuationRecipe(RegistryRecipes.rRitualPedestalRock));
        resRitPedestal.addPage(new JournalPageStructure(MultiBlockArrays.patternRitualPedestal));

        ResearchNode resConstellationUpgrade = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), "ALTAR3", 3, 0);
        resConstellationUpgrade.addPage(getTextPage("ALTAR3.1"));
        resConstellationUpgrade.addPage(new JournalPageAttenuationRecipe(RegistryRecipes.rAltarUpgradeConstellation));
        resConstellationUpgrade.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarConstellation));

        resRitPedestal.addSourceConnectionFrom(resSimpleAtt);
        resConstellationUpgrade.addSourceConnectionFrom(resSimpleAtt);

        regAttenuation.register(resWell);
        regAttenuation.register(resIlluminator);
        regAttenuation.register(resSimpleAtt);
        regAttenuation.register(resRitPedestal);
        regAttenuation.register(resConstellationUpgrade);
    }

    private static void initCrafting() {
        ResearchProgression.Registry regCrafting = ResearchProgression.BASIC_CRAFT.getRegistry();

        ResearchNode resTelescope = new ResearchNode(BlockMachine.MachineType.TELESCOPE.asStack(), "TELESCOPE", 0, 0);
        resTelescope.addPage(getTextPage("TELESCOPE.1"));
        resTelescope.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCCGlassLens));
        resTelescope.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rTelescope));
        resTelescope.addPage(getTextPage("TELESCOPE.4"));

        ResearchNode resGrindstone = new ResearchNode(BlockMachine.MachineType.GRINDSTONE.asStack(), "GRINDSTONE", 0, 2);
        resGrindstone.addPage(getTextPage("GRINDSTONE.1"));
        resGrindstone.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rGrindstone));

        ResearchNode resTools = new ResearchNode(
                new ItemStack[] {
                        new ItemStack(ItemsAS.crystalPickaxe), new ItemStack(ItemsAS.crystalSword),
                        new ItemStack(ItemsAS.crystalAxe), new ItemStack(ItemsAS.crystalShovel)
                }, "TOOLS", -1, 3);
        resTools.addPage(getTextPage("TOOLS.1"));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolRockSword));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolRockPick));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolRockAxe));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolRockShovel));

        ItemStack[] stacks = new ItemStack[BlockMarble.MarbleBlockType.values().length];
        BlockMarble.MarbleBlockType[] values = BlockMarble.MarbleBlockType.values();
        for (int i = 0; i < values.length; i++) {
            BlockMarble.MarbleBlockType mbt = values[i];
            stacks[i] = new ItemStack(BlocksAS.blockMarble, 1, mbt.ordinal());
        }

        ResearchNode resAltarUpgradeAttenuation = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()), "ALTAR2", 2, 1);
        resAltarUpgradeAttenuation.addPage(getTextPage("ALTAR2.1"));
        resAltarUpgradeAttenuation.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rAltarUpgradeAttenuation));
        resAltarUpgradeAttenuation.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarAttenuation));

        regCrafting.register(resTelescope);
        regCrafting.register(resGrindstone);
        regCrafting.register(resTools);
        regCrafting.register(resAltarUpgradeAttenuation);

        resGrindstone.addSourceConnectionFrom(resTools);
    }

    private static void initDiscovery() {
        ResearchProgression.Registry regDiscovery = ResearchProgression.DISCOVERY.getRegistry();

        ResearchNode resShrines = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "SHRINES", 0, 0);
        resShrines.addPage(getTextPage("SHRINES.1"));
        resShrines.addPage(getTextPage("SHRINES.2"));

        ResearchNode resConPaper = new ResearchNode(new ItemStack(ItemsAS.constellationPaper), "CPAPER", 1, -1);
        resConPaper.addPage(getTextPage("CPAPER.1"));

        ResearchNode resWand = new ResearchNode(new ItemStack(ItemsAS.wand), "WAND", 2, 1);
        resWand.addPage(getTextPage("WAND.1"));
        resWand.addPage(new JournalPageLightProximityRecipe(RegistryRecipes.rLPRWand));
        resWand.addPage(getTextPage("WAND.3"));

        ResearchNode resOres = new ResearchNode(new ItemStack[] {
                new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.ROCK_CRYSTAL.ordinal()),
                new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal())
        }, "ORES", 1, 2);
        resOres.addPage(getTextPage("ORES.1"));

        ItemStack[] stacks = new ItemStack[BlockMarble.MarbleBlockType.values().length];
        BlockMarble.MarbleBlockType[] values = BlockMarble.MarbleBlockType.values();
        for (int i = 0; i < values.length; i++) {
            BlockMarble.MarbleBlockType mbt = values[i];
            stacks[i] = new ItemStack(BlocksAS.blockMarble, 1, mbt.getMeta());
        }
        ResearchNode resMarbleTypes = new ResearchNode(stacks, "MARBLETYPES", 3, 0);
        resMarbleTypes.addPage(getTextPage("MARBLETYPES.1"));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleBricks));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarblePillar));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleChiseled));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleArch));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleRuned));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleEngraved));

        ResearchNode resSootyMarble = new ResearchNode(new ItemStack(BlocksAS.blockBlackMarble), "SOOTYMARBLE", 5, 1);
        resSootyMarble.addPage(getTextPage("SOOTYMARBLE.1"));
        resSootyMarble.addPage(new JournalPageRecipe(RegistryRecipes.rBlackMarbleRaw));

        ResearchNode resTable = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()), "ALTAR1", 4, 2);
        resTable.addPage(getTextPage("ALTAR1.1"));
        resTable.addPage(new JournalPageLightProximityRecipe(RegistryRecipes.rLPRAltar));
        resTable.addPage(getTextPage("ALTAR1.3"));
        resTable.addPage(getTextPage("ALTAR1.4"));

        regDiscovery.register(resShrines);
        regDiscovery.register(resWand);
        regDiscovery.register(resOres);
        regDiscovery.register(resConPaper);
        regDiscovery.register(resTable);
        regDiscovery.register(resMarbleTypes);
        regDiscovery.register(resSootyMarble);

        resWand.addSourceConnectionFrom(resShrines);
        resConPaper.addSourceConnectionFrom(resShrines);
        resTable.addSourceConnectionFrom(resWand);
        resWand.addSourceConnectionFrom(resOres);
        resSootyMarble.addSourceConnectionFrom(resMarbleTypes);
    }

    private static JournalPageText getTextPage(String identifier) {
        return new JournalPageText(AstralSorcery.MODID.toLowerCase() + ".journal." + identifier + ".text");
    }

}
