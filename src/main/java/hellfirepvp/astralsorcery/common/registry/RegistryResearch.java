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
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteQuery;
import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.BlockMachine;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import hellfirepvp.astralsorcery.common.util.struct.BlockArray;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

import static hellfirepvp.astralsorcery.common.registry.RegistryBookLookups.registerItemLookup;

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
        initRadiance();
    }

    private static void initRadiance() {
        ResearchProgression.Registry regRadiance = ResearchProgression.RADIANCE.getRegistry();


    }

    private static void initConstellation() {
        ResearchProgression.Registry regConstellation = ResearchProgression.CONSTELLATION.getRegistry();

        ResearchNode resInfuser = new ResearchNode(new ItemStack(BlocksAS.starlightInfuser), "INFUSER", 4, 2);
        resInfuser.addPage(getTextPage("INFUSER.1"));
        resInfuser.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rStarlightInfuser));
        resInfuser.addPage(getTextPage("INFUSER.3"));
        resInfuser.addPage(new JournalPageStructure(MultiBlockArrays.patternStarlightInfuser));

        ResearchNode resTreeBeacon = new ResearchNode(new ItemStack(BlocksAS.treeBeacon), "TREEBEACON", 3, 0);
        resTreeBeacon.addPage(getTextPage("TREEBEACON.1"));
        resTreeBeacon.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rTreeBeacon));
        resTreeBeacon.addPage(getTextPage("TREEBEACON.3"));

        ResearchNode resChargedTools = new ResearchNode(new ItemStack[] {
                new ItemStack(ItemsAS.chargedCrystalAxe),
                new ItemStack(ItemsAS.chargedCrystalPickaxe),
                new ItemStack(ItemsAS.chargedCrystalShovel),
                new ItemStack(ItemsAS.chargedCrystalSword)
        }, "CHARGED_TOOLS", 5, 0);
        resChargedTools.addPage(getTextPage("CHARGED_TOOLS.1"));
        resChargedTools.addPage(getTextPage("CHARGED_TOOLS.2"));

        ItemStack[] stacks = new ItemStack[ItemColoredLens.ColorType.values().length - 1];
        ItemColoredLens.ColorType[] values = ItemColoredLens.ColorType.values();
        for (int i = 0; i < values.length - 1; i++) {
            ItemColoredLens.ColorType ct = values[i];
            stacks[i] = ct.asStack();
        }
        ResearchNode resColoredLenses = new ResearchNode(stacks, "LENSES_EFFECTS", 2, 1);
        resColoredLenses.addPage(getTextPage("LENSES_EFFECTS.1"));
        resColoredLenses.addPage(getTextPage("LENSES_EFFECTS.2"));
        resColoredLenses.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensFire));
        resColoredLenses.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensBreak));
        resColoredLenses.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensDamage));
        resColoredLenses.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensGrowth));
        resColoredLenses.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensRegeneration));
        resColoredLenses.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensPush));

        ResearchNode resSpectralLens = new ResearchNode(ItemColoredLens.ColorType.SPECTRAL.asStack(), "SPECTRAL_LENS", 1, 2);
        resSpectralLens.addPage(getTextPage("SPECTRAL_LENS.1"));
        resSpectralLens.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rGlassLensSpectral));

        ResearchNode resRitualLink = new ResearchNode(new ItemStack(BlocksAS.ritualLink), "RITUAL_LINK", 1, 0);
        resRitualLink.addPage(getTextPage("RITUAL_LINK.1"));
        resRitualLink.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rRitualLink));
        resRitualLink.addPage(new JournalPageStructure(MultiBlockArrays.patternRitualPedestalWithLink));

        ResearchNode resShiftStar = new ResearchNode(new ItemStack(ItemsAS.shiftingStar), "SHIFT_STAR", 6, 2) {
            @Override
            public boolean canSee(@Nullable PlayerProgress progress) {
                return super.canSee(progress) && progress != null && progress.wasOnceAttuned();
            }
        };
        resShiftStar.addPage(getTextPage("SHIFT_STAR.1"));
        resShiftStar.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rShiftStar));

        ResearchNode resIllWand = new ResearchNode(new ItemStack(ItemsAS.illuminationWand), "ILLUMINATION_WAND", 6, 1);
        resIllWand.addPage(getTextPage("ILLUMINATION_WAND.1"));
        resIllWand.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rIlluminationWand));

        ResearchNode resPrism = new ResearchNode(new ItemStack(BlocksAS.lensPrism), "PRISM", 3, 3);
        resPrism.addPage(getTextPage("PRISM.1"));
        resPrism.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rPrism));

        ResearchNode resCollCrystal = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "COLL_CRYSTAL", 5, 3);
        resCollCrystal.addPage(getTextPage("COLL_CRYSTAL.1"));
        resCollCrystal.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rCollectRock));

        ItemStack cel = new ItemStack(BlocksAS.celestialCollectorCrystal);
        ItemCollectorCrystal.setType(cel, BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL);
        ResearchNode resEnhancedCollCrystal = new ResearchNode(cel, "ENHANCED_COLLECTOR", 7, 4);
        resEnhancedCollCrystal.addPage(getTextPage("ENHANCED_COLLECTOR.1"));
        resEnhancedCollCrystal.addPage(new JournalPageStructure(MultiBlockArrays.patternCollectorEnhancement, null, new Vector3(0, -36, 0)));

        ResearchNode resCelCrystalCluster = new ResearchNode(new ItemStack(BlocksAS.celestialCrystals, 1, 3), "CEL_CRYSTAL_GROW", 2, 2);
        resCelCrystalCluster.addPage(getTextPage("CEL_CRYSTAL_GROW.1"));
        resCelCrystalCluster.addPage(getTextPage("CEL_CRYSTAL_GROW.2"));

        ResearchNode resCelCrystals = new ResearchNode(new ItemStack(ItemsAS.celestialCrystal), "CEL_CRYSTALS", 1, 4);
        resCelCrystals.addPage(getTextPage("CEL_CRYSTALS.1"));

        registerItemLookup(new ItemStack(ItemsAS.celestialCrystal, 1, OreDictionary.WILDCARD_VALUE),   resCelCrystalCluster, 0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.celestialCrystals, 1, OreDictionary.WILDCARD_VALUE), resCelCrystalCluster, 0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.collectorCrystal, 1, OreDictionary.WILDCARD_VALUE),  resCollCrystal,       0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.lensPrism, 1, OreDictionary.WILDCARD_VALUE),         resPrism,             0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.FIRE.asStack(),                                           resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.BREAK.asStack(),                                          resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.GROW.asStack(),                                           resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.DAMAGE.asStack(),                                         resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.REGEN.asStack(),                                          resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.PUSH.asStack(),                                           resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.SPECTRAL.asStack(),                                       resSpectralLens,      0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(ItemsAS.illuminationWand, 1, OreDictionary.WILDCARD_VALUE),   resIllWand,           0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(ItemsAS.shiftingStar, 1, OreDictionary.WILDCARD_VALUE),       resShiftStar,         0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.starlightInfuser, 1, OreDictionary.WILDCARD_VALUE),  resInfuser,           1, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.treeBeacon, 1, OreDictionary.WILDCARD_VALUE),        resTreeBeacon,        1, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.ritualLink, 1, OreDictionary.WILDCARD_VALUE),        resRitualLink,        1, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),                                  resInfuser,           1, ResearchProgression.CONSTELLATION);

        resCelCrystals.addSourceConnectionFrom(resCelCrystalCluster);
        resPrism.addSourceConnectionFrom(resInfuser);
        resShiftStar.addSourceConnectionFrom(resInfuser);
        resIllWand.addSourceConnectionFrom(resInfuser);
        resCollCrystal.addSourceConnectionFrom(resInfuser);
        resTreeBeacon.addSourceConnectionFrom(resInfuser);
        resChargedTools.addSourceConnectionFrom(resInfuser);
        resSpectralLens.addSourceConnectionFrom(resColoredLenses);
        resEnhancedCollCrystal.addSourceConnectionFrom(resCollCrystal);

        regConstellation.register(resColoredLenses);
        regConstellation.register(resPrism);
        regConstellation.register(resCollCrystal);
        regConstellation.register(resCelCrystalCluster);
        regConstellation.register(resCelCrystals);
        regConstellation.register(resIllWand);
        regConstellation.register(resShiftStar);
        regConstellation.register(resInfuser);
        regConstellation.register(resTreeBeacon);
        regConstellation.register(resChargedTools);
        regConstellation.register(resRitualLink);
        regConstellation.register(resSpectralLens);
        regConstellation.register(resEnhancedCollCrystal);
    }

    private static void initAttunement() {
        ResearchProgression.Registry regAttunement = ResearchProgression.ATTUNEMENT.getRegistry();

        ResearchNode resIlluminator = new ResearchNode(new ItemStack(BlocksAS.blockIlluminator), "ILLUMINATOR", 3, 6);
        resIlluminator.addPage(getTextPage("ILLUMINATOR.1"));
        resIlluminator.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rIlluminator));
        resIlluminator.addPage(getTextPage("ILLUMINATOR.3"));

        ResearchNode resLinkTool = new ResearchNode(new ItemStack(ItemsAS.linkingTool), "LINKTOOL", 1, 3);
        resLinkTool.addPage(getTextPage("LINKTOOL.1"));
        resLinkTool.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rLinkTool));
        resLinkTool.addPage(getTextPage("LINKTOOL.3"));

        ResearchNode resLens = new ResearchNode(new ItemStack(BlocksAS.lens), "LENS", 0, 4);
        resLens.addPage(getTextPage("LENS.1"));
        resLens.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rLens));
        resLens.addPage(getTextPage("LENS.3"));
        resLens.addPage(getTextPage("LENS.4"));

        ResearchNode resStarOre = new ResearchNode(new ItemStack(Blocks.IRON_ORE), "STARMETAL_ORE", 2, 4);
        resStarOre.addPage(getTextPage("STARMETAL_ORE.1"));

        ResearchNode resOtherOres = new ResearchNode(new ItemStack[] {
                new ItemStack(Blocks.COAL_BLOCK),
                new ItemStack(Blocks.SAND),
                new ItemStack(Blocks.DIAMOND_ORE),
                new ItemStack(Blocks.NETHER_WART_BLOCK),
                new ItemStack(Blocks.PUMPKIN),
                new ItemStack(Blocks.LAPIS_BLOCK)
        }, "TRANSMUTATION_ORES", 3, 3);
        resOtherOres.addPage(getTextPage("TRANSMUTATION_ORES.1"));

        ResearchNode resStarResult = new ResearchNode(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(), "STARMETAL_RES", 4, 5);
        resStarResult.addPage(getTextPage("STARMETAL_RES.1"));
        resStarResult.addPage(getTextPage("STARMETAL_RES.2"));

        ResearchNode resPlayerAtt = new ResearchNode(new ItemStack(BlocksAS.attunementAltar), "ATT_PLAYER", 5, 4);
        resPlayerAtt.addPage(getTextPage("ATT_PLAYER.1"));
        resPlayerAtt.addPage(getTextPage("ATT_PLAYER.2"));
        resPlayerAtt.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rAttunementAltar));
        resPlayerAtt.addPage(getTextPage("ATT_PLAYER.4"));
        resPlayerAtt.addPage(new JournalPageStructure(MultiBlockArrays.patternAttunementFrame));
        resPlayerAtt.addPage(getTextPage("ATT_PLAYER.6"));

        ResearchNode resAttPerks = new ResearchNode(new ItemStack(BlocksAS.attunementRelay), "ATT_PERKS", 6, 3);
        resAttPerks.addPage(getTextPage("ATT_PERKS.1"));
        resAttPerks.addPage(getTextPage("ATT_PERKS.2"));

        ResearchNode resCrystalAtt = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "ATT_CRYSTAL", 7, 4);
        resCrystalAtt.addPage(getTextPage("ATT_CRYSTAL.1"));
        resCrystalAtt.addPage(getTextPage("ATT_CRYSTAL.2"));

        ResearchNode resRitPedestal = new ResearchNode(new ItemStack(BlocksAS.ritualPedestal), "RIT_PEDESTAL", 8, 3);
        resRitPedestal.addPage(getTextPage("RIT_PEDESTAL.1"));
        resRitPedestal.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rRitualPedestal));
        resRitPedestal.addPage(new JournalPageStructure(MultiBlockArrays.patternRitualPedestal));

        ResearchNode resRitualAccel = new ResearchNode(new ItemStack(BlocksAS.ritualPedestal), "PED_ACCEL", 8, 2);
        resRitualAccel.addPage(getTextPage("PED_ACCEL.1"));
        resRitualAccel.addPage(getTextPage("PED_ACCEL.2"));

        ResearchNode resConstellationUpgrade = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), "ALTAR3", 5, 7);
        resConstellationUpgrade.addPage(getTextPage("ALTAR3.1"));
        resConstellationUpgrade.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rAltarUpgradeConstellation));
        resConstellationUpgrade.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarConstellation));
        resConstellationUpgrade.addPage(getTextPage("ALTAR3.4"));

        ResearchNode resMountedTelescope = new ResearchNode(BlockMachine.MachineType.TELESCOPE.asStack(), "TELESCOPE", 1, 5);
        resMountedTelescope.addPage(getTextPage("TELESCOPE.1"));
        resMountedTelescope.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rTelescope));
        resMountedTelescope.addPage(getTextPage("TELESCOPE.3"));
        resMountedTelescope.addPage(getTextPage("TELESCOPE.4"));

        ResearchNode resToolWands = new ResearchNode(new ItemStack[] {
                new ItemStack(ItemsAS.architectWand), new ItemStack(ItemsAS.exchangeWand)
        }, "TOOL_WANDS", 6, 2);
        resToolWands.addPage(getTextPage("TOOL_WANDS.1"));
        resToolWands.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rArchitectWand));
        resToolWands.addPage(getTextPage("TOOL_WANDS.3"));
        resToolWands.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rExchangeWand));

        ResearchNode resQuickCharge = new ResearchNode(new SpriteQuery(AssetLoader.TextureLocation.EFFECT, "star1", 6, 8),
                "QUICK_CHARGE", 5, 3);
        resQuickCharge.addPage(getTextPage("QUICK_CHARGE.1"));

        ResearchNode resStarlightNetwork = new ResearchNode(new ItemStack(BlocksAS.lens), "STARLIGHT_NETWORK", 3, 5);
        resStarlightNetwork.addPage(getTextPage("STARLIGHT_NETWORK.1"));

        registerItemLookup(new ItemStack(BlocksAS.lens, 1, OreDictionary.WILDCARD_VALUE),              resLens,              0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.linkingTool, 1, OreDictionary.WILDCARD_VALUE),        resLinkTool,          0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(BlockCustomOre.OreType.STARMETAL.asStack(),                                 resStarOre,           0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),                   resStarResult,        0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemCraftingComponent.MetaType.STARDUST.asStack(),                          resStarResult,        0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), resConstellationUpgrade, 1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.ritualPedestal, 1, OreDictionary.WILDCARD_VALUE),       resRitPedestal,          1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.attunementAltar, 1, OreDictionary.WILDCARD_VALUE),      resPlayerAtt,            2, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.attunementRelay, 1, OreDictionary.WILDCARD_VALUE),      resPlayerAtt,            1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.blockIlluminator, 1, OreDictionary.WILDCARD_VALUE),     resIlluminator,          1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.tunedRockCrystal, 1, OreDictionary.WILDCARD_VALUE),      resCrystalAtt,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.tunedCelestialCrystal, 1, OreDictionary.WILDCARD_VALUE), resCrystalAtt,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.architectWand, 1, OreDictionary.WILDCARD_VALUE),         resToolWands,            1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.exchangeWand, 1, OreDictionary.WILDCARD_VALUE),          resToolWands,            2, ResearchProgression.ATTUNEMENT);
        registerItemLookup(BlockMachine.MachineType.TELESCOPE.asStack(),                                  resMountedTelescope,     1, ResearchProgression.ATTUNEMENT);

        regAttunement.register(resIlluminator);
        regAttunement.register(resLens);
        regAttunement.register(resLinkTool);
        regAttunement.register(resStarOre);
        regAttunement.register(resStarResult);
        regAttunement.register(resOtherOres);
        regAttunement.register(resPlayerAtt);
        regAttunement.register(resCrystalAtt);
        regAttunement.register(resAttPerks);
        regAttunement.register(resRitPedestal);
        regAttunement.register(resRitualAccel);
        regAttunement.register(resConstellationUpgrade);
        regAttunement.register(resMountedTelescope);
        regAttunement.register(resToolWands);
        regAttunement.register(resQuickCharge);
        regAttunement.register(resStarlightNetwork);

        resStarOre.addSourceConnectionFrom(resLinkTool);
        resStarOre.addSourceConnectionFrom(resLens);
        resStarResult.addSourceConnectionFrom(resStarOre);
        resConstellationUpgrade.addSourceConnectionFrom(resStarResult);
        resOtherOres.addSourceConnectionFrom(resStarOre);
        resPlayerAtt.addSourceConnectionFrom(resStarResult);
        resCrystalAtt.addSourceConnectionFrom(resPlayerAtt);
        resAttPerks.addSourceConnectionFrom(resPlayerAtt);
        resRitPedestal.addSourceConnectionFrom(resCrystalAtt);
        resRitualAccel.addSourceConnectionFrom(resRitPedestal);
        resToolWands.addSourceConnectionFrom(resQuickCharge);
        resQuickCharge.addSourceConnectionFrom(resPlayerAtt);
        resStarlightNetwork.addSourceConnectionFrom(resStarOre);
    }

    private static void initCrafting() {
        ResearchProgression.Registry regCrafting = ResearchProgression.BASIC_CRAFT.getRegistry();

        ResearchNode resTelescope = new ResearchNode(new ItemStack(ItemsAS.handTelescope), "HAND_TELESCOPE", 2, 2);
        resTelescope.addPage(getTextPage("HAND_TELESCOPE.1"));
        resTelescope.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCCGlassLens));
        resTelescope.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rHandTelescope));
        resTelescope.addPage(getTextPage("HAND_TELESCOPE.4"));

        ResearchNode resRelay = new ResearchNode(new ItemStack(BlocksAS.attunementRelay), "SPEC_RELAY", 2, 0);
        resRelay.addPage(getTextPage("SPEC_RELAY.1"));
        resRelay.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rAttenuationAltarRelay));
        resRelay.addPage(new JournalPageStructure(MultiBlockArrays.patternCollectorRelay));
        resRelay.addPage(getTextPage("SPEC_RELAY.4"));

        ResearchNode resWell = new ResearchNode(new ItemStack(BlocksAS.blockWell), "WELL", 0, 2);
        resWell.addPage(getTextPage("WELL.1"));
        resWell.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rLightwell));
        resWell.addPage(getTextPage("WELL.3"));

        ResearchNode resResonator = new ResearchNode(new ItemStack(ItemsAS.skyResonator), "SKY_RESO", 1, 0);
        resResonator.addPage(getTextPage("SKY_RESO.1"));
        resResonator.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rSkyResonator));
        resResonator.addPage(getTextPage("SKY_RESO.3"));

        ResearchNode resGrindstone = new ResearchNode(BlockMachine.MachineType.GRINDSTONE.asStack(), "GRINDSTONE", 3, 4);
        resGrindstone.addPage(getTextPage("GRINDSTONE.1"));
        resGrindstone.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rGrindstone));
        resGrindstone.addPage(getTextPage("GRINDSTONE.3"));

        ResearchNode resTools = new ResearchNode(
                new ItemStack[] {
                        new ItemStack(ItemsAS.crystalPickaxe), new ItemStack(ItemsAS.crystalSword),
                        new ItemStack(ItemsAS.crystalAxe), new ItemStack(ItemsAS.crystalShovel)
                }, "TOOLS", 2, 5);
        resTools.addPage(getTextPage("TOOLS.1"));
        resTools.addPage(getTextPage("TOOLS.2"));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolSword));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolPick));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolAxe));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolShovel));

        ResearchNode resRockCrystals = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "ROCK_CRYSTALS", 0, 4);
        resRockCrystals.addPage(getTextPage("ROCK_CRYSTALS.1"));

        ResearchNode resCrystalGrowth = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "CRYSTAL_GROWTH",1, 3);
        resCrystalGrowth.addPage(getTextPage("CRYSTAL_GROWTH.1"));

        ResearchNode resAltarUpgradeAttenuation = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()), "ALTAR2", 2, 1);
        resAltarUpgradeAttenuation.addPage(getTextPage("ALTAR2.1"));
        resAltarUpgradeAttenuation.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rAltarUpgradeAttenuation));
        resAltarUpgradeAttenuation.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarAttunement));

        ResearchNode resIlluminationPowder = new ResearchNode(new ItemStack(ItemsAS.illuminationPowder), "ILLUM_POWDER", 3, 3);
        resIlluminationPowder.addPage(getTextPage("ILLUM_POWDER.1"));
        resIlluminationPowder.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rIlluminationPowder));

        registerItemLookup(new ItemStack(BlocksAS.blockAltar,     1, BlockAltar.AltarType.ALTAR_2.ordinal()), resAltarUpgradeAttenuation, 1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalShovel,   1, OreDictionary.WILDCARD_VALUE),           resTools,                   5, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalAxe,      1, OreDictionary.WILDCARD_VALUE),           resTools,                   4, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalPickaxe,  1, OreDictionary.WILDCARD_VALUE),           resTools,                   3, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.crystalSword,    1, OreDictionary.WILDCARD_VALUE),           resTools,                   2, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(BlockMachine.MachineType.GRINDSTONE.asStack(),                                             resGrindstone,              1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(BlocksAS.blockWell,      1, OreDictionary.WILDCARD_VALUE),           resWell,                    1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(ItemCraftingComponent.MetaType.GLASS_LENS.asStack(),                                       resTelescope,               1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.handTelescope,   1, OreDictionary.WILDCARD_VALUE),           resTelescope,               2, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.skyResonator,    1, OreDictionary.WILDCARD_VALUE),           resResonator,               1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(BlocksAS.attunementRelay,1, OreDictionary.WILDCARD_VALUE),           resRelay,                   1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(ItemsAS.illuminationPowder, 1, OreDictionary.WILDCARD_VALUE),        resIlluminationPowder,      1, ResearchProgression.BASIC_CRAFT);

        regCrafting.register(resTelescope);
        regCrafting.register(resGrindstone);
        regCrafting.register(resTools);
        regCrafting.register(resWell);
        regCrafting.register(resAltarUpgradeAttenuation);
        regCrafting.register(resResonator);
        regCrafting.register(resRockCrystals);
        regCrafting.register(resRelay);
        regCrafting.register(resCrystalGrowth);
        regCrafting.register(resIlluminationPowder);

        resGrindstone.addSourceConnectionFrom(resRockCrystals);
        resTools.addSourceConnectionFrom(resRockCrystals);
        resAltarUpgradeAttenuation.addSourceConnectionFrom(resWell);
        resResonator.addSourceConnectionFrom(resWell);
        resRelay.addSourceConnectionFrom(resResonator);
        resCrystalGrowth.addSourceConnectionFrom(resWell, resRockCrystals);
    }

    private static void initDiscovery() {
        ResearchProgression.Registry regDiscovery = ResearchProgression.DISCOVERY.getRegistry();

        ResearchNode resShrines = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "SHRINES", 0, 1);
        resShrines.addPage(getTextPage("SHRINES.1"));
        resShrines.addPage(getTextPage("SHRINES.2"));
        resShrines.addPage(getTextPage("SHRINES.3"));

        ResearchNode resConPaper = new ResearchNode(new ItemStack(ItemsAS.constellationPaper), "CPAPER", 1, 0);
        resConPaper.addPage(getTextPage("CPAPER.1"));
        resConPaper.addPage(new JournalPageRecipe(RegistryRecipes.rRJournal));
        resConPaper.addPage(getTextPage("CPAPER.3"));
        resConPaper.addPage(new JournalPageRecipe(RegistryRecipes.rCCParchment));

        ResearchNode resWand = new ResearchNode(new ItemStack(ItemsAS.wand), "WAND", 2, 2);
        resWand.addPage(getTextPage("WAND.1"));
        resWand.addPage(new JournalPageLightProximityRecipe(RegistryRecipes.rLPRWand));
        resWand.addPage(getTextPage("WAND.3"));

        ResearchNode resOres = new ResearchNode(new ItemStack[] {
                new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.ROCK_CRYSTAL.ordinal()),
                new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal())
        }, "ORES", 1, 3);
        resOres.addPage(getTextPage("ORES.1"));
        resOres.addPage(getTextPage("ORES.2"));

        ItemStack[] stacks = new ItemStack[BlockMarble.MarbleBlockType.values().length];
        BlockMarble.MarbleBlockType[] values = BlockMarble.MarbleBlockType.values();
        for (int i = 0; i < values.length; i++) {
            BlockMarble.MarbleBlockType mbt = values[i];
            stacks[i] = new ItemStack(BlocksAS.blockMarble, 1, mbt.getMeta());
        }
        ResearchNode resMarbleTypes = new ResearchNode(stacks, "MARBLETYPES", 3, 1);
        resMarbleTypes.addPage(getTextPage("MARBLETYPES.1"));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleBricks));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarblePillar));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleChiseled));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleArch));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleRuned));
        resMarbleTypes.addPage(new JournalPageRecipe(RegistryRecipes.rMarbleEngraved));

        ResearchNode resSootyMarble = new ResearchNode(new ItemStack(BlocksAS.blockBlackMarble), "SOOTYMARBLE", 5, 2);
        resSootyMarble.addPage(getTextPage("SOOTYMARBLE.1"));
        resSootyMarble.addPage(new JournalPageRecipe(RegistryRecipes.rBlackMarbleRaw));

        ResearchNode resTable = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()), "ALTAR1", 4, 3);
        resTable.addPage(getTextPage("ALTAR1.1"));
        resTable.addPage(new JournalPageLightProximityRecipe(RegistryRecipes.rLPRAltar));
        resTable.addPage(getTextPage("ALTAR1.3"));
        resTable.addPage(getTextPage("ALTAR1.4"));

        registerItemLookup(new ItemStack(BlocksAS.blockAltar,        1, BlockAltar.AltarType.ALTAR_1.ordinal()), resTable,        1, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(BlocksAS.blockBlackMarble,  1, OreDictionary.WILDCARD_VALUE),            resSootyMarble, 0, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.BRICKS.asStack(),            resMarbleTypes, 1, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.PILLAR.asStack(),            resMarbleTypes, 2, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.CHISELED.asStack(),            resMarbleTypes, 3, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.ARCH.asStack(),            resMarbleTypes, 4, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.RUNED.asStack(),            resMarbleTypes, 5, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.ENGRAVED.asStack(),            resMarbleTypes, 6, ResearchProgression.DISCOVERY);
        registerItemLookup(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),                                   resOres,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),                                    resConPaper,            3, ResearchProgression.DISCOVERY);
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
