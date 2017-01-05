/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.journal.page.JournalPageAttunementRecipe;
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
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static hellfirepvp.astralsorcery.common.registry.RegistryBookLookups.*;

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
        initAttunement();
        initConstellation();
    }

    private static void initConstellation() {
        ResearchProgression.Registry regConstellation = ResearchProgression.CONSTELLATION.getRegistry();

        ResearchNode resLens = new ResearchNode(new ItemStack(BlocksAS.lens), "LENS", 0, 1);
        resLens.addPage(getTextPage("LENS.1"));
        resLens.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rLensRock));

        ItemStack[] stacks = new ItemStack[ItemColoredLens.ColorType.values().length];
        ItemColoredLens.ColorType[] values = ItemColoredLens.ColorType.values();
        for (int i = 0; i < values.length; i++) {
            ItemColoredLens.ColorType ct = values[i];
            stacks[i] = ct.asStack();
        }
        ResearchNode resColoredLenses = new ResearchNode(stacks, "LENSES_EFFECTS", 1, 0);
        resColoredLenses.addPage(getTextPage("LENSES_EFFECTS.1"));
        resColoredLenses.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGlassLensFire));
        resColoredLenses.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGlassLensBreak));
        resColoredLenses.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGlassLensDamage));
        resColoredLenses.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGlassLensGrowth));
        resColoredLenses.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGlassLensRegeneration));
        resColoredLenses.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGlassLensNightvision));

        ResearchNode resLinkTool = new ResearchNode(new ItemStack(ItemsAS.linkingTool), "LINKTOOL", 1, 2);
        resLinkTool.addPage(getTextPage("LINKTOOL.1"));
        resLinkTool.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rLinkToolRock));

        ResearchNode resStarOre = new ResearchNode(new ItemStack(Blocks.IRON_ORE), "STARMETAL_ORE", 2, 1);
        resStarOre.addPage(getTextPage("STARMETAL_ORE.1"));

        ResearchNode resStarResult = new ResearchNode(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(), "STARMETAL_RES", 3, 2);
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

        ResearchNode resRitualAccel = new ResearchNode(new ItemStack(BlocksAS.ritualPedestal), "PED_ACCEL", -1, 3);
        resRitualAccel.addPage(getTextPage("PED_ACCEL.1"));

        registerItemLookup(new ItemStack(ItemsAS.celestialCrystal, 1, OreDictionary.WILDCARD_VALUE),   resCelCrystalCluster, 0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.celestialCrystals, 1, OreDictionary.WILDCARD_VALUE), resCelCrystalCluster, 0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.collectorCrystal, 1, OreDictionary.WILDCARD_VALUE),  resCollCrystal,       0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.lensPrism, 1, OreDictionary.WILDCARD_VALUE),         resPrism,             0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.lens, 1, OreDictionary.WILDCARD_VALUE),              resLens,              0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(ItemsAS.linkingTool, 1, OreDictionary.WILDCARD_VALUE),        resLinkTool,          0, ResearchProgression.CONSTELLATION);
        registerItemLookup(BlockCustomOre.OreType.STARMETAL.asStack(),                                 resStarOre,           0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),                   resStarResult,        0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemCraftingComponent.MetaType.STARDUST.asStack(),                          resStarResult,        0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(ItemsAS.coloredLens, 1, OreDictionary.WILDCARD_VALUE),        resColoredLenses,     0, ResearchProgression.CONSTELLATION);

        resStarOre.addSourceConnectionFrom(resLinkTool);
        resStarOre.addSourceConnectionFrom(resLens);
        resColoredLenses.addSourceConnectionFrom(resLens);
        resRitualAccel.addSourceConnectionFrom(resLens);
        resRitualAccel.addSourceConnectionFrom(resLinkTool);
        resPrism.addSourceConnectionFrom(resStarResult);
        resStarResult.addSourceConnectionFrom(resStarOre);
        resCollCrystal.addSourceConnectionFrom(resStarResult);
        resCelCrystalCluster.addSourceConnectionFrom(resStarResult);
        resCelCrystals.addSourceConnectionFrom(resCelCrystalCluster);

        regConstellation.register(resLens);
        regConstellation.register(resColoredLenses);
        regConstellation.register(resLinkTool);
        regConstellation.register(resStarOre);
        regConstellation.register(resStarResult);
        regConstellation.register(resPrism);
        regConstellation.register(resCollCrystal);
        regConstellation.register(resCelCrystalCluster);
        regConstellation.register(resCelCrystals);
        regConstellation.register(resRitualAccel);
    }

    private static void initAttunement() {
        ResearchProgression.Registry regAttunement = ResearchProgression.ATTUNEMENT.getRegistry();

        ResearchNode resIlluminator = new ResearchNode(new ItemStack(BlocksAS.blockIlluminator), "ILLUMINATOR", 1, -1);
        resIlluminator.addPage(getTextPage("ILLUMINATOR.1"));
        resIlluminator.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rIlluminatorRock));

        ResearchNode resPlayerAtt = new ResearchNode(new ItemStack(BlocksAS.attunementAltar), "ATT_PLAYER", 0, 0);
        resPlayerAtt.addPage(getTextPage("ATT_PLAYER.1"));
        resPlayerAtt.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rAttenuationAltarRelay));
        resPlayerAtt.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rAttunementAltarRock));
        resPlayerAtt.addPage(new JournalPageStructure(MultiBlockArrays.patternAttunementFrame));

        ResearchNode resCrystalAtt = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "ATT_CRYSTAL", 2, 1);
        resCrystalAtt.addPage(getTextPage("ATT_CRYSTAL.1"));
        resCrystalAtt.addPage(getTextPage("ATT_CRYSTAL.2"));

        ResearchNode resRitPedestal = new ResearchNode(new ItemStack(BlocksAS.ritualPedestal), "RIT_PEDESTAL", 1, 2);
        resRitPedestal.addPage(getTextPage("RIT_PEDESTAL.1"));
        resRitPedestal.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rRitualPedestalRock));
        resRitPedestal.addPage(new JournalPageStructure(MultiBlockArrays.patternRitualPedestal));

        ResearchNode resConstellationUpgrade = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), "ALTAR3", 3, 3);
        resConstellationUpgrade.addPage(getTextPage("ALTAR3.1"));
        resConstellationUpgrade.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rAltarUpgradeConstellationRock));
        resConstellationUpgrade.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarConstellation));

        ResearchNode resInfuser = new ResearchNode(new ItemStack(BlocksAS.starlightInfuser), "INFUSER", 3, -1);
        resInfuser.addPage(getTextPage("INFUSER.1"));
        resInfuser.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rStarlightInfuserRock));
        //TODO add reso gem infusion recipe

        ResearchNode resTreeBeacon = new ResearchNode(new ItemStack(BlocksAS.treeBeacon), "TREEBEACON", 4, 1);
        resTreeBeacon.addPage(getTextPage("TREEBEACON.1"));
        resTreeBeacon.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rTreeBeacon));

        registerItemLookup(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), resConstellationUpgrade, 1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.ritualPedestal, 1, OreDictionary.WILDCARD_VALUE),       resRitPedestal,          1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.attunementAltar, 1, OreDictionary.WILDCARD_VALUE),      resPlayerAtt,            2, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.attunementRelay, 1, OreDictionary.WILDCARD_VALUE),      resPlayerAtt,            1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.blockIlluminator, 1, OreDictionary.WILDCARD_VALUE),     resIlluminator,          1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.tunedRockCrystal, 1, OreDictionary.WILDCARD_VALUE),      resCrystalAtt,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.tunedCelestialCrystal, 1, OreDictionary.WILDCARD_VALUE), resCrystalAtt,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.starlightInfuser, 1, OreDictionary.WILDCARD_VALUE),     resInfuser,              1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.treeBeacon, 1, OreDictionary.WILDCARD_VALUE),           resTreeBeacon,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),                             resInfuser,              1, ResearchProgression.ATTUNEMENT); //TODO change to 2 after adding recipe gui

        regAttunement.register(resIlluminator);
        regAttunement.register(resPlayerAtt);
        regAttunement.register(resCrystalAtt);
        regAttunement.register(resRitPedestal);
        regAttunement.register(resConstellationUpgrade);
        regAttunement.register(resInfuser);
        regAttunement.register(resTreeBeacon);

        resRitPedestal.addSourceConnectionFrom(resCrystalAtt);
        resCrystalAtt.addSourceConnectionFrom(resPlayerAtt);
        resConstellationUpgrade.addSourceConnectionFrom(resCrystalAtt);
        resTreeBeacon.addSourceConnectionFrom(resInfuser);
    }

    private static void initCrafting() {
        ResearchProgression.Registry regCrafting = ResearchProgression.BASIC_CRAFT.getRegistry();

        ResearchNode resTelescope = new ResearchNode(new ItemStack(ItemsAS.handTelescope), "HAND_TELESCOPE", 0, 0);
        resTelescope.addPage(getTextPage("HAND_TELESCOPE.1"));
        resTelescope.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCCGlassLens));
        resTelescope.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rHandTelescope));
        resTelescope.addPage(getTextPage("HAND_TELESCOPE.4"));

        ResearchNode resWell = new ResearchNode(new ItemStack(BlocksAS.blockWell), "WELL", 1, 1);
        resWell.addPage(getTextPage("WELL.1"));
        resWell.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rLightwell));

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

        ResearchNode resAltarUpgradeAttenuation = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()), "ALTAR2", 3, 0);
        resAltarUpgradeAttenuation.addPage(getTextPage("ALTAR2.1"));
        resAltarUpgradeAttenuation.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rAltarUpgradeAttenuationRock));
        resAltarUpgradeAttenuation.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarAttunement));

        registerItemLookup(new ItemStack(BlocksAS.blockAltar,    1, BlockAltar.AltarType.ALTAR_2.ordinal()), resAltarUpgradeAttenuation, 1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalShovel,  1, OreDictionary.WILDCARD_VALUE),           resTools,                   4, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalAxe,     1, OreDictionary.WILDCARD_VALUE),           resTools,                   3, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalPickaxe, 1, OreDictionary.WILDCARD_VALUE),           resTools,                   2, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalSword,   1, OreDictionary.WILDCARD_VALUE),           resTools,                   1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(BlockMachine.MachineType.GRINDSTONE.asStack(),                                    resGrindstone,              1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(BlocksAS.blockWell,     1, OreDictionary.WILDCARD_VALUE),           resWell,                    1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),                              resTelescope,               1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.handTelescope,  1, OreDictionary.WILDCARD_VALUE),           resTelescope,               2, ResearchProgression.BASIC_CRAFT);

        regCrafting.register(resTelescope);
        regCrafting.register(resGrindstone);
        regCrafting.register(resTools);
        regCrafting.register(resWell);
        regCrafting.register(resAltarUpgradeAttenuation);

        resGrindstone.addSourceConnectionFrom(resTools);
        resAltarUpgradeAttenuation.addSourceConnectionFrom(resWell);
    }

    private static void initDiscovery() {
        ResearchProgression.Registry regDiscovery = ResearchProgression.DISCOVERY.getRegistry();

        ResearchNode resShrines = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "SHRINES", 0, 0);
        resShrines.addPage(getTextPage("SHRINES.1"));
        resShrines.addPage(getTextPage("SHRINES.2"));

        ResearchNode resConPaper = new ResearchNode(new ItemStack(ItemsAS.constellationPaper), "CPAPER", 1, -1);
        resConPaper.addPage(getTextPage("CPAPER.1"));
        resConPaper.addPage(new JournalPageRecipe(RegistryRecipes.rRJournal));
        resConPaper.addPage(getTextPage("CPAPER.3"));

        ResearchNode resWand = new ResearchNode(new ItemStack(ItemsAS.wand), "WAND", 2, 1);
        resWand.addPage(getTextPage("WAND.1"));
        resWand.addPage(new JournalPageLightProximityRecipe(RegistryRecipes.rLPRWand));
        resWand.addPage(getTextPage("WAND.3"));

        ResearchNode resOres = new ResearchNode(new ItemStack[] {
                new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.ROCK_CRYSTAL.ordinal()),
                new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal())
        }, "ORES", 1, 2);
        resOres.addPage(getTextPage("ORES.1"));
        resOres.addPage(getTextPage("ORES.2"));

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

        registerItemLookup(new ItemStack(BlocksAS.blockAltar,        1, BlockAltar.AltarType.ALTAR_1.ordinal()), resTable,        1, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(BlocksAS.blockBlackMarble,  1, OreDictionary.WILDCARD_VALUE),            resSootyMarble, 0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(BlocksAS.blockMarble,       1, OreDictionary.WILDCARD_VALUE),            resMarbleTypes, 0, ResearchProgression.DISCOVERY);
        registerItemLookup(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),                                   resOres,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.rockCrystal,        1, OreDictionary.WILDCARD_VALUE),            resOres,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.wand,               1, OreDictionary.WILDCARD_VALUE),            resWand,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.journal,            1, OreDictionary.WILDCARD_VALUE),            resConPaper,    0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.constellationPaper, 1, OreDictionary.WILDCARD_VALUE),            resConPaper,    0, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockCustomOre.OreType.ROCK_CRYSTAL.asStack(),                                         resOres,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockCustomSandOre.OreType.AQUAMARINE.asStack(),                                       resOres,        0, ResearchProgression.DISCOVERY);

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
