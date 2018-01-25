/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.gui.journal.page.*;
import hellfirepvp.astralsorcery.client.util.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.util.resource.SpriteQuery;
import hellfirepvp.astralsorcery.client.util.resource.TextureQuery;
import hellfirepvp.astralsorcery.common.block.*;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.AttunementRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.ConstellationRecipe;
import hellfirepvp.astralsorcery.common.crafting.altar.recipes.TraitRecipe;
import hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipeSlot;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.item.ItemColoredLens;
import hellfirepvp.astralsorcery.common.item.ItemCraftingComponent;
import hellfirepvp.astralsorcery.common.item.block.ItemCollectorCrystal;
import hellfirepvp.astralsorcery.common.item.tool.wand.ItemWand;
import hellfirepvp.astralsorcery.common.item.tool.wand.WandAugment;
import hellfirepvp.astralsorcery.common.item.useables.ItemUsableDust;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.tile.TileBore;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;

import static hellfirepvp.astralsorcery.common.crafting.altar.AltarRecipeRegistry.registerTraitRecipe;
import static hellfirepvp.astralsorcery.common.crafting.helper.ShapedRecipe.Builder.newShapedRecipe;
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

        ResearchNode resHintRecipes = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()), "CRAFTING_FOCUS_HINT", 4, 5);
        resHintRecipes.addPage(getTextPage("CRAFTING_FOCUS_HINT.1"));
        resHintRecipes.addPage(getTextPage("CRAFTING_FOCUS_HINT.2"));

        ResearchNode resAttWands = new ResearchNode(new ItemStack(ItemsAS.wand), "ATT_WANDS", 3, 4);
        resAttWands.addPage(getTextPage("ATT_WANDS.1"));
        resAttWands.addPage(getTextPage("ATT_WANDS.2"));

        ResearchNode resAttWandArmara = new ResearchNode(ItemWand.setAugment(new ItemStack(ItemsAS.wand), WandAugment.ARMARA), "ATT_WAND_ARMARA", 1, 5);
        resAttWandArmara.addPage(getTextPage("ATT_WAND_ARMARA.1"));
        resAttWandArmara.addPage(new JournalPageTraitRecipe(RegistryRecipes.rWandAugmentArmara));

        ResearchNode resAttWandDiscidia = new ResearchNode(ItemWand.setAugment(new ItemStack(ItemsAS.wand), WandAugment.DISCIDIA), "ATT_WAND_DISCIDIA", 1, 3);
        resAttWandDiscidia.addPage(getTextPage("ATT_WAND_DISCIDIA.1"));
        resAttWandDiscidia.addPage(new JournalPageTraitRecipe(RegistryRecipes.rWandAugmentDiscidia));

        ResearchNode resAttWandAevitas = new ResearchNode(ItemWand.setAugment(new ItemStack(ItemsAS.wand), WandAugment.AEVITAS), "ATT_WAND_AEVITAS", 4, 2);
        resAttWandAevitas.addPage(getTextPage("ATT_WAND_AEVITAS.1"));
        resAttWandAevitas.addPage(new JournalPageTraitRecipe(RegistryRecipes.rWandAugmentAevitas));

        ResearchNode resAttWandVicio = new ResearchNode(ItemWand.setAugment(new ItemStack(ItemsAS.wand), WandAugment.VICIO), "ATT_WAND_VICIO", 5, 3);
        resAttWandVicio.addPage(getTextPage("ATT_WAND_VICIO.1"));
        resAttWandVicio.addPage(new JournalPageTraitRecipe(RegistryRecipes.rWandAugmentVicio));

        ResearchNode resAttWandEvorsio = new ResearchNode(ItemWand.setAugment(new ItemStack(ItemsAS.wand), WandAugment.EVORSIO), "ATT_WAND_EVORSIO", 2, 2);
        resAttWandEvorsio.addPage(getTextPage("ATT_WAND_EVORSIO.1"));
        resAttWandEvorsio.addPage(new JournalPageTraitRecipe(RegistryRecipes.rWandAugmentEvorsio));

        ResearchNode resCape = new ResearchNode(new ItemStack(ItemsAS.armorImbuedCape), "ATT_CAPE", 3, 6);
        resCape.addPage(getTextPage("ATT_CAPE.1"));
        resCape.addPage(new JournalPageTraitRecipe(RegistryRecipes.rCapeBase));
        resCape.addPage(getTextPage("ATT_CAPE.3"));
        resCape.addPage(getTextPage("ATT_CAPE.4"));

        ResearchNode resChalice = new ResearchNode(new ItemStack(BlocksAS.blockChalice), "C_CHALICE", 5, 4);
        resChalice.addPage(getTextPage("C_CHALICE.1"));
        resChalice.addPage(new JournalPageTraitRecipe(RegistryRecipes.rChalice));
        resChalice.addPage(getTextPage("C_CHALICE.3"));
        resChalice.addPage(getTextPage("C_CHALICE.4"));
        resChalice.addPage(getTextPage("C_CHALICE.5"));

        ResearchNode resBore = new ResearchNode(new ItemStack(BlocksAS.blockBore), "BORE_CORE", 7, 5);
        resBore.addPage(getTextPage("BORE_CORE.1"));
        resBore.addPage(getTextPage("BORE_CORE.2"));
        resBore.addPage(new JournalPageTraitRecipe(RegistryRecipes.rBore));
        resBore.addPage(getTextPage("BORE_CORE.4"));
        resBore.addPage(new JournalPageStructure(MultiBlockArrays.patternFountain));

        ResearchNode resBoreLiquid = new ResearchNode(new ItemStack(BlocksAS.blockBoreHead, 1, TileBore.BoreType.LIQUID.ordinal()), "BORE_HEAD_LIQUID", 8, 4);
        resBoreLiquid.addPage(getTextPage("BORE_HEAD_LIQUID.1"));
        resBoreLiquid.addPage(getTextPage("BORE_HEAD_LIQUID.2"));
        resBoreLiquid.addPage(new JournalPageTraitRecipe(RegistryRecipes.rBoreHeadLiquid));
        resBoreLiquid.addPage(getTextPage("BORE_HEAD_LIQUID.4"));
        resBoreLiquid.addPage(new JournalPageTraitRecipe(RegistryRecipes.rResonatorLiquid));

        registerItemLookup(new ItemStack(ItemsAS.armorImbuedCape),                      resCape,       1, ResearchProgression.RADIANCE);
        registerItemLookup(new ItemStack(BlocksAS.blockChalice),                        resChalice,    1, ResearchProgression.RADIANCE);
        registerItemLookup(new ItemStack(BlocksAS.blockBore),                           resBore,       2, ResearchProgression.RADIANCE);
        registerItemLookup(new ItemStack(BlocksAS.blockBoreHead, 1, 0),   resBoreLiquid, 1, ResearchProgression.RADIANCE);

        resAttWandArmara.addSourceConnectionFrom(resAttWands);
        resAttWandDiscidia.addSourceConnectionFrom(resAttWands);
        resAttWandAevitas.addSourceConnectionFrom(resAttWands);
        resAttWandVicio.addSourceConnectionFrom(resAttWands);
        resAttWandEvorsio.addSourceConnectionFrom(resAttWands);
        resAttWands.addSourceConnectionFrom(resHintRecipes);
        resCape.addSourceConnectionFrom(resHintRecipes);
        resChalice.addSourceConnectionFrom(resHintRecipes);
        resBore.addSourceConnectionFrom(resChalice);
        resBoreLiquid.addSourceConnectionFrom(resBore);

        regRadiance.register(resAttWands);
        regRadiance.register(resAttWandArmara);
        regRadiance.register(resAttWandAevitas);
        regRadiance.register(resAttWandDiscidia);
        regRadiance.register(resAttWandVicio);
        regRadiance.register(resAttWandEvorsio);
        regRadiance.register(resCape);
        regRadiance.register(resHintRecipes);
        regRadiance.register(resChalice);
        regRadiance.register(resBore);
        regRadiance.register(resBoreLiquid);
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

        ResearchNode resIllWand = new ResearchNode(new ItemStack(ItemsAS.illuminationWand), "ILLUMINATION_WAND", 6, 1);
        resIllWand.addPage(getTextPage("ILLUMINATION_WAND.1"));
        resIllWand.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rIlluminationWand));
        resIllWand.addPage(getTextPage("ILLUMINATION_WAND.3"));

        ResearchNode resPrism = new ResearchNode(new ItemStack(BlocksAS.lensPrism), "PRISM", 4, 1);
        resPrism.addPage(getTextPage("PRISM.1"));
        resPrism.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rPrism));

        ResearchNode resCollCrystal = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "COLL_CRYSTAL", 5, 3);
        resCollCrystal.addPage(getTextPage("COLL_CRYSTAL.1"));
        resCollCrystal.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rCollectRock));

        ItemStack cel = new ItemStack(BlocksAS.celestialCollectorCrystal);
        ItemCollectorCrystal.setType(cel, BlockCollectorCrystalBase.CollectorCrystalType.CELESTIAL_CRYSTAL);
        ResearchNode resEnhancedCollCrystal = new ResearchNode(cel, "ENHANCED_COLLECTOR", 4, 5);
        resEnhancedCollCrystal.addPage(getTextPage("ENHANCED_COLLECTOR.1"));
        resEnhancedCollCrystal.addPage(new JournalPageStructure(MultiBlockArrays.patternCollectorEnhancement, null, new Vector3(0, -36, 0)));

        ResearchNode resCelCrystalCluster = new ResearchNode(new ItemStack(BlocksAS.celestialCrystals, 1, 3), "CEL_CRYSTAL_GROW", 2, 3);
        resCelCrystalCluster.addPage(getTextPage("CEL_CRYSTAL_GROW.1"));
        resCelCrystalCluster.addPage(getTextPage("CEL_CRYSTAL_GROW.2"));

        ResearchNode resCelCrystals = new ResearchNode(new ItemStack(ItemsAS.celestialCrystal), "CEL_CRYSTALS", 3, 4);
        resCelCrystals.addPage(getTextPage("CEL_CRYSTALS.1"));

        ResearchNode resDrawing = new ResearchNode(new ItemStack(BlocksAS.drawingTable), "DRAWING_TABLE", 3, 3);
        resDrawing.addPage(getTextPage("DRAWING_TABLE.1"));
        resDrawing.addPage(getTextPage("DRAWING_TABLE.2"));
        resDrawing.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rDrawingTable));
        resDrawing.addPage(getTextPage("DRAWING_TABLE.4"));
        resDrawing.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rInfusedGlass));
        resDrawing.addPage(getTextPage("DRAWING_TABLE.6"));
        resDrawing.addPage(getTextPage("DRAWING_TABLE.7"));
        resDrawing.addPage(getTextPage("DRAWING_TABLE.8"));

        ResearchNode resTraitUpgrade = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()), "ALTAR4", 6, 3);
        resTraitUpgrade.addPage(getTextPage("ALTAR4.1"));
        resTraitUpgrade.addPage(new JournalPageConstellationRecipe(RegistryRecipes.rAltarUpgradeTrait));
        resTraitUpgrade.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarTrait));
        resTraitUpgrade.addPage(getTextPage("ALTAR4.4"));

        registerItemLookup(new ItemStack(ItemsAS.celestialCrystal, 1, OreDictionary.WILDCARD_VALUE),      resCelCrystalCluster, 0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.celestialCrystals, 1, OreDictionary.WILDCARD_VALUE),    resCelCrystalCluster, 0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.collectorCrystal, 1, OreDictionary.WILDCARD_VALUE),     resCollCrystal,       0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.lensPrism, 1, OreDictionary.WILDCARD_VALUE),            resPrism,             0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.FIRE.asStack(),                                              resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.BREAK.asStack(),                                             resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.GROW.asStack(),                                              resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.DAMAGE.asStack(),                                            resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.REGEN.asStack(),                                             resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.PUSH.asStack(),                                              resColoredLenses,     0, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemColoredLens.ColorType.SPECTRAL.asStack(),                                          resSpectralLens,      0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(ItemsAS.illuminationWand, 1, OreDictionary.WILDCARD_VALUE),      resIllWand,           0, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.starlightInfuser, 1, OreDictionary.WILDCARD_VALUE),     resInfuser,           1, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.treeBeacon, 1, OreDictionary.WILDCARD_VALUE),           resTreeBeacon,        1, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.ritualLink, 1, OreDictionary.WILDCARD_VALUE),           resRitualLink,        1, ResearchProgression.CONSTELLATION);
        registerItemLookup(ItemCraftingComponent.MetaType.RESO_GEM.asStack(),                                     resInfuser,           1, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.drawingTable, 1, OreDictionary.WILDCARD_VALUE),         resDrawing,           1, ResearchProgression.CONSTELLATION);
        registerItemLookup(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_4.ordinal()), resTraitUpgrade,      1, ResearchProgression.CONSTELLATION);

        resCelCrystals.addSourceConnectionFrom(resCelCrystalCluster);
        resPrism.addSourceConnectionFrom(resInfuser);
        resIllWand.addSourceConnectionFrom(resInfuser);
        resCollCrystal.addSourceConnectionFrom(resInfuser);
        resTreeBeacon.addSourceConnectionFrom(resInfuser);
        resChargedTools.addSourceConnectionFrom(resInfuser);
        resSpectralLens.addSourceConnectionFrom(resColoredLenses);
        resEnhancedCollCrystal.addSourceConnectionFrom(resCollCrystal);
        resDrawing.addSourceConnectionFrom(resColoredLenses);
        resDrawing.addSourceConnectionFrom(resInfuser);
        resEnhancedCollCrystal.addSourceConnectionFrom(resCelCrystals);
        resTraitUpgrade.addSourceConnectionFrom(resInfuser);

        regConstellation.register(resColoredLenses);
        regConstellation.register(resPrism);
        regConstellation.register(resCollCrystal);
        regConstellation.register(resCelCrystalCluster);
        regConstellation.register(resCelCrystals);
        regConstellation.register(resIllWand);
        regConstellation.register(resInfuser);
        regConstellation.register(resTreeBeacon);
        regConstellation.register(resChargedTools);
        regConstellation.register(resRitualLink);
        regConstellation.register(resSpectralLens);
        regConstellation.register(resEnhancedCollCrystal);
        regConstellation.register(resDrawing);
        regConstellation.register(resTraitUpgrade);
    }

    private static void initAttunement() {
        ResearchProgression.Registry regAttunement = ResearchProgression.ATTUNEMENT.getRegistry();

        ResearchNode resLinkTool = new ResearchNode(new ItemStack(ItemsAS.linkingTool), "LINKTOOL", 1, 3);
        resLinkTool.addPage(getTextPage("LINKTOOL.1"));
        resLinkTool.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rLinkTool));
        resLinkTool.addPage(getTextPage("LINKTOOL.3"));
        resLinkTool.addPage(getTextPage("LINKTOOL.4"));

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

        ResearchNode resPlayerAtt = new ResearchNode(new ItemStack(BlocksAS.attunementAltar), "ATT_PLAYER", 5, 3);
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
        }, "TOOL_WANDS", 4, 3);
        resToolWands.addPage(getTextPage("TOOL_WANDS.1"));
        resToolWands.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rArchitectWand));
        resToolWands.addPage(getTextPage("TOOL_WANDS.3"));
        resToolWands.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rExchangeWand));

        ResearchNode resToolGrapple = new ResearchNode(new ItemStack(ItemsAS.grapplingWand), "GRAPPLE_WAND", 3, 2);
        resToolGrapple.addPage(getTextPage("GRAPPLE_WAND.1"));
        resToolGrapple.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rGrappleWand));

        ResearchNode resQuickCharge = new ResearchNode(new SpriteQuery(AssetLoader.TextureLocation.EFFECT, "star1", 6, 8),
                "QUICK_CHARGE", 4, 4);
        resQuickCharge.addPage(getTextPage("QUICK_CHARGE.1"));

        ResearchNode resStarlightNetwork = new ResearchNode(new ItemStack(BlocksAS.lens), "STARLIGHT_NETWORK", 3, 5);
        resStarlightNetwork.addPage(getTextPage("STARLIGHT_NETWORK.1"));

        ResearchNode resCelestialGateway = new ResearchNode(new ItemStack(BlocksAS.celestialGateway), "CELESTIAL_GATEWAY", 6, 5);
        resCelestialGateway.addPage(getTextPage("CELESTIAL_GATEWAY.1"));
        resCelestialGateway.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rCelestialGateway));
        resCelestialGateway.addPage(getTextPage("CELESTIAL_GATEWAY.3"));
        resCelestialGateway.addPage(new JournalPageStructure(MultiBlockArrays.patternCelestialGateway));

        ResearchNode resShiftStar = new ResearchNode(new ItemStack(ItemsAS.shiftingStar), "SHIFT_STAR", 6, 2);
        resShiftStar.addPage(getTextPage("SHIFT_STAR.1"));
        resShiftStar.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rShiftStar));

        ResearchNode resKnowledgeShare = new ResearchNode(new ItemStack(ItemsAS.knowledgeShare), "KNOWLEDGE_SHARE", 2, 6);
        resKnowledgeShare.addPage(getTextPage("KNOWLEDGE_SHARE.1"));
        resKnowledgeShare.addPage(new JournalPageAttunementRecipe(RegistryRecipes.rKnowledgeShare));

        registerItemLookup(new ItemStack(BlocksAS.lens, 1, OreDictionary.WILDCARD_VALUE),                 resLens,                 0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.linkingTool, 1, OreDictionary.WILDCARD_VALUE),           resLinkTool,             0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(BlockCustomOre.OreType.STARMETAL.asStack(),                                            resStarOre,              0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(ItemCraftingComponent.MetaType.STARMETAL_INGOT.asStack(),                              resStarResult,           0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(ItemCraftingComponent.MetaType.STARDUST.asStack(),                                     resStarResult,           0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_3.ordinal()), resConstellationUpgrade, 1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.ritualPedestal, 1, OreDictionary.WILDCARD_VALUE),       resRitPedestal,          1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.attunementAltar, 1, OreDictionary.WILDCARD_VALUE),      resPlayerAtt,            2, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.attunementRelay, 1, OreDictionary.WILDCARD_VALUE),      resPlayerAtt,            1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.tunedRockCrystal, 1, OreDictionary.WILDCARD_VALUE),      resCrystalAtt,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.tunedCelestialCrystal, 1, OreDictionary.WILDCARD_VALUE), resCrystalAtt,           1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.architectWand, 1, OreDictionary.WILDCARD_VALUE),         resToolWands,            1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.exchangeWand, 1, OreDictionary.WILDCARD_VALUE),          resToolWands,            2, ResearchProgression.ATTUNEMENT);
        registerItemLookup(BlockMachine.MachineType.TELESCOPE.asStack(),                                          resMountedTelescope,     1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(BlocksAS.celestialGateway, 1, OreDictionary.WILDCARD_VALUE),     resCelestialGateway,     1, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.shiftingStar, 1, OreDictionary.WILDCARD_VALUE),          resShiftStar,            0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.grapplingWand, 1, OreDictionary.WILDCARD_VALUE),         resToolGrapple,          0, ResearchProgression.ATTUNEMENT);
        registerItemLookup(new ItemStack(ItemsAS.knowledgeShare, 1, OreDictionary.WILDCARD_VALUE),        resKnowledgeShare,       0, ResearchProgression.ATTUNEMENT);

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
        regAttunement.register(resCelestialGateway);
        regAttunement.register(resShiftStar);
        regAttunement.register(resToolGrapple);
        regAttunement.register(resKnowledgeShare);

        resStarOre.addSourceConnectionFrom(resLinkTool);
        resStarOre.addSourceConnectionFrom(resLens);
        resStarResult.addSourceConnectionFrom(resStarOre);
        resConstellationUpgrade.addSourceConnectionFrom(resStarResult);
        resOtherOres.addSourceConnectionFrom(resStarOre);
        resPlayerAtt.addSourceConnectionFrom(resQuickCharge);
        resCrystalAtt.addSourceConnectionFrom(resPlayerAtt);
        resAttPerks.addSourceConnectionFrom(resPlayerAtt);
        resRitPedestal.addSourceConnectionFrom(resCrystalAtt);
        resRitualAccel.addSourceConnectionFrom(resRitPedestal);
        resToolWands.addSourceConnectionFrom(resQuickCharge);
        resQuickCharge.addSourceConnectionFrom(resStarResult);
        resStarlightNetwork.addSourceConnectionFrom(resStarOre);
        resCelestialGateway.addSourceConnectionFrom(resStarResult);
        resShiftStar.addSourceConnectionFrom(resPlayerAtt);
        resToolGrapple.addSourceConnectionFrom(resQuickCharge);
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
        resTools.addPage(getTextPage("TOOLS.3"));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolSword));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolPick));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolAxe));
        resTools.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rCToolShovel));

        ResearchNode resIlluminator = new ResearchNode(new ItemStack(BlocksAS.blockIlluminator), "ILLUMINATOR", 4, 1);
        resIlluminator.addPage(getTextPage("ILLUMINATOR.1"));
        resIlluminator.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rIlluminator));
        resIlluminator.addPage(getTextPage("ILLUMINATOR.3"));

        ResearchNode resNocturnalPowder = new ResearchNode(ItemUsableDust.DustType.NOCTURNAL.asStack(), "NOC_POWDER", 4, 4);
        resNocturnalPowder.addPage(getTextPage("NOC_POWDER.1"));
        resNocturnalPowder.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rNocturnalPowder));
        resNocturnalPowder.addPage(getTextPage("NOC_POWDER.3"));

        ResearchNode resRockCrystals = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "ROCK_CRYSTALS", 0, 4);
        resRockCrystals.addPage(getTextPage("ROCK_CRYSTALS.1"));

        ResearchNode resCrystalGrowth = new ResearchNode(new ItemStack(ItemsAS.rockCrystal), "CRYSTAL_GROWTH",1, 3);
        resCrystalGrowth.addPage(getTextPage("CRYSTAL_GROWTH.1"));

        ResearchNode resAltarUpgradeAttenuation = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_2.ordinal()), "ALTAR2", 2, 1);
        resAltarUpgradeAttenuation.addPage(getTextPage("ALTAR2.1"));
        resAltarUpgradeAttenuation.addPage(new JournalPageDiscoveryRecipe(RegistryRecipes.rAltarUpgradeAttenuation));
        resAltarUpgradeAttenuation.addPage(new JournalPageStructure(MultiBlockArrays.patternAltarAttunement));

        ResearchNode resIlluminationPowder = new ResearchNode(ItemUsableDust.DustType.ILLUMINATION.asStack(), "ILLUM_POWDER", 3, 3);
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
        registerItemLookup(ItemUsableDust.DustType.ILLUMINATION.asStack(),                                            resIlluminationPowder,      1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(ItemUsableDust.DustType.NOCTURNAL.asStack(),                                               resNocturnalPowder,         1, ResearchProgression.BASIC_CRAFT);
        registerItemLookup(new ItemStack(BlocksAS.blockIlluminator, 1, OreDictionary.WILDCARD_VALUE),         resIlluminator,             1, ResearchProgression.BASIC_CRAFT);

        regCrafting.register(resIlluminator);
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
        regCrafting.register(resNocturnalPowder);

        resGrindstone.addSourceConnectionFrom(resRockCrystals);
        resTools.addSourceConnectionFrom(resRockCrystals);
        resAltarUpgradeAttenuation.addSourceConnectionFrom(resWell);
        resResonator.addSourceConnectionFrom(resWell);
        resRelay.addSourceConnectionFrom(resResonator);
        resCrystalGrowth.addSourceConnectionFrom(resWell, resRockCrystals);
        resIlluminator.addSourceConnectionFrom(resIlluminationPowder);
        resNocturnalPowder.addSourceConnectionFrom(resIlluminationPowder);
    }

    private static void initDiscovery() {
        ResearchProgression.Registry regDiscovery = ResearchProgression.DISCOVERY.getRegistry();

        ResearchNode resShrines = new ResearchNode(new ItemStack(BlocksAS.collectorCrystal), "SHRINES", 0, 1);
        resShrines.addPage(getTextPage("SHRINES.1"));
        resShrines.addPage(getTextPage("SHRINES.2"));
        resShrines.addPage(getTextPage("SHRINES.3"));

        ResearchNode resConPaper = new ResearchNode(new ItemStack(ItemsAS.constellationPaper), "CPAPER", 1, 0);
        resConPaper.addPage(getTextPage("CPAPER.1"));
        resConPaper.addPage(new JournalPageRecipe(RecipesAS.rRJournal));
        resConPaper.addPage(getTextPage("CPAPER.3"));
        resConPaper.addPage(new JournalPageRecipe(RecipesAS.rCCParchment));

        ResearchNode resWand = new ResearchNode(new ItemStack(ItemsAS.wand), "WAND", 2, 2);
        resWand.addPage(getTextPage("WAND.1"));
        if(Config.lightProximityResonatingWandRecipe) {
            resWand.addPage(new JournalPageLightProximityRecipe(RecipesAS.rLPRWand));
        }
        resWand.addPage(getTextPage("WAND.3"));

        ResearchNode resOres = new ResearchNode(new ItemStack[] {
                new ItemStack(BlocksAS.customOre, 1, BlockCustomOre.OreType.ROCK_CRYSTAL.ordinal()),
                new ItemStack(BlocksAS.customSandOre, 1, BlockCustomSandOre.OreType.AQUAMARINE.ordinal())
        }, "ORES", 1, 3);
        resOres.addPage(getTextPage("ORES.1"));
        resOres.addPage(getTextPage("ORES.2"));

        ItemStack[] stacks = new ItemStack[BlockMarble.MarbleBlockType.values().length + 1];
        BlockMarble.MarbleBlockType[] values = BlockMarble.MarbleBlockType.values();
        for (int i = 0; i < values.length; i++) {
            BlockMarble.MarbleBlockType mbt = values[i];
            stacks[i] = new ItemStack(BlocksAS.blockMarble, 1, mbt.getMeta());
        }
        stacks[stacks.length - 1] = new ItemStack(BlocksAS.blockMarbleStairs);
        ResearchNode resMarbleTypes = new ResearchNode(stacks, "MARBLETYPES", 3, 1);
        resMarbleTypes.addPage(getTextPage("MARBLETYPES.1"));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleBricks));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarblePillar));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleChiseled));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleArch));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleRuned));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleEngraved));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleStairs));
        resMarbleTypes.addPage(new JournalPageRecipe(RecipesAS.rMarbleSlab));

        stacks = new ItemStack[BlockBlackMarble.BlackMarbleBlockType.values().length];
        BlockBlackMarble.BlackMarbleBlockType[] sValues = BlockBlackMarble.BlackMarbleBlockType.values();
        for (int i = 0; i < sValues.length; i++) {
            BlockBlackMarble.BlackMarbleBlockType mbt = sValues[i];
            stacks[i] = new ItemStack(BlocksAS.blockBlackMarble, 1, mbt.getMeta());
        }
        ResearchNode resSootyMarble = new ResearchNode(stacks, "SOOTYMARBLE", 5, 2);
        resSootyMarble.addPage(getTextPage("SOOTYMARBLE.1"));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarbleRaw));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarbleBricks));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarblePillar));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarbleChiseled));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarbleArch));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarbleRuned));
        resSootyMarble.addPage(new JournalPageRecipe(RecipesAS.rBlackMarbleEngraved));

        ResearchNode resTable = new ResearchNode(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()), "ALTAR1", 4, 3);
        resTable.addPage(getTextPage("ALTAR1.1"));
        resTable.addPage(getTextPage("ALTAR1.2"));
        if(Config.lightProximityAltarRecipe) {
            resTable.addPage(new JournalPageLightProximityRecipe(RecipesAS.rLPRAltar));
        }
        resTable.addPage(getTextPage("ALTAR1.4"));
        resTable.addPage(getTextPage("ALTAR1.5"));

        registerItemLookup(new ItemStack(BlocksAS.blockAltar, 1, BlockAltar.AltarType.ALTAR_1.ordinal()), resTable,       1, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.RAW.asStack(),                                   resSootyMarble, 1, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.BRICKS.asStack(),                                resSootyMarble, 2, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.PILLAR.asStack(),                                resSootyMarble, 3, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.CHISELED.asStack(),                              resSootyMarble, 4, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.ARCH.asStack(),                                  resSootyMarble, 5, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.RUNED.asStack(),                                 resSootyMarble, 6, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockBlackMarble.BlackMarbleBlockType.ENGRAVED.asStack(),                              resSootyMarble, 7, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.BRICKS.asStack(),                                          resMarbleTypes, 1, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.PILLAR.asStack(),                                          resMarbleTypes, 2, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.CHISELED.asStack(),                                        resMarbleTypes, 3, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.ARCH.asStack(),                                            resMarbleTypes, 4, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.RUNED.asStack(),                                           resMarbleTypes, 5, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockMarble.MarbleBlockType.ENGRAVED.asStack(),                                        resMarbleTypes, 6, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(BlocksAS.blockMarbleStairs, 1, OreDictionary.WILDCARD_VALUE),    resMarbleTypes, 7, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(BlocksAS.blockMarbleSlab, 1, OreDictionary.WILDCARD_VALUE),      resMarbleTypes, 8, ResearchProgression.DISCOVERY);
        registerItemLookup(ItemCraftingComponent.MetaType.AQUAMARINE.asStack(),                                   resOres,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(ItemCraftingComponent.MetaType.PARCHMENT.asStack(),                                    resConPaper,    3, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.rockCrystal, 1, OreDictionary.WILDCARD_VALUE),           resOres,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.wand, 1, OreDictionary.WILDCARD_VALUE),                  resWand,        0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.journal, 1, OreDictionary.WILDCARD_VALUE),               resConPaper,    0, ResearchProgression.DISCOVERY);
        registerItemLookup(new ItemStack(ItemsAS.constellationPaper, 1, OreDictionary.WILDCARD_VALUE),    resConPaper,    0, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockCustomOre.OreType.ROCK_CRYSTAL.asStack(),                                          resOres,       0, ResearchProgression.DISCOVERY);
        registerItemLookup(BlockCustomSandOre.OreType.AQUAMARINE.asStack(),                                        resOres,       0, ResearchProgression.DISCOVERY);

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
