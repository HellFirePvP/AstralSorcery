/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;
import hellfirepvp.astralsorcery.common.data.journal.JournalPage;
import hellfirepvp.astralsorcery.common.data.journal.JournalPageRecipe;
import hellfirepvp.astralsorcery.common.data.journal.JournalPageStructure;
import hellfirepvp.astralsorcery.common.data.journal.JournalPageText;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.block.Blocks;
import net.minecraft.util.IItemProvider;

import static hellfirepvp.astralsorcery.common.auxiliary.book.BookLookupRegistry.registerItemLookup;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryResearch
 * Created by HellFirePvP
 * Date: 13.04.2020 / 15:22
 */
public class RegistryResearch {

    public static void init() {
        registerDiscovery();
        registerCrafting();
        registerAttunement();
        registerConstellation();
        registerRadiance();
    }

    private static void registerRadiance() {
        ResearchNode nodeTraitRelayHint = new ResearchNode(BlocksAS.ALTAR_RADIANCE, "CRAFTING_FOCUS_HINT", 4, 5)
                .addPage(text("CRAFTING_FOCUS_HINT.1"))
                .addPage(text("CRAFTING_FOCUS_HINT.2"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode nodeObservatory = new ResearchNode(BlocksAS.OBSERVATORY, "OBSERVATORY", 4, 7)
                .addPage(text("OBSERVATORY.1"))
                .addPage(recipe(BlocksAS.OBSERVATORY))
                .addTomeLookup(BlocksAS.OBSERVATORY, 1, ResearchProgression.RADIANCE)
                .addPage(text("OBSERVATORY.3"))
                .addPage(text("OBSERVATORY.4"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode nodeCrystalTraitHint = new ResearchNode(ItemsAS.CELESTIAL_CRYSTAL, "ATT_TRAIT", 5, 8)
                .addPage(text("ATT_TRAIT.1"))
                .addPage(text("ATT_TRAIT.2"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode nodeIrradiantStars = new ResearchNode(new IItemProvider[] {
                ItemsAS.SHIFTING_STAR_AEVITAS,
                ItemsAS.SHIFTING_STAR_ARMARA,
                ItemsAS.SHIFTING_STAR_DISCIDIA,
                ItemsAS.SHIFTING_STAR_EVORSIO,
                ItemsAS.SHIFTING_STAR_VICIO
        }, "ENH_SHIFTING_STAR", 5, 6)
                .addPage(text("ENH_SHIFTING_STAR.1"))
                .addPage(recipe(ItemsAS.SHIFTING_STAR_AEVITAS))
                .addTomeLookup(ItemsAS.SHIFTING_STAR_AEVITAS, 1, ResearchProgression.RADIANCE)
                .addPage(recipe(ItemsAS.SHIFTING_STAR_ARMARA))
                .addTomeLookup(ItemsAS.SHIFTING_STAR_ARMARA, 2, ResearchProgression.RADIANCE)
                .addPage(recipe(ItemsAS.SHIFTING_STAR_DISCIDIA))
                .addTomeLookup(ItemsAS.SHIFTING_STAR_DISCIDIA, 3, ResearchProgression.RADIANCE)
                .addPage(recipe(ItemsAS.SHIFTING_STAR_EVORSIO))
                .addTomeLookup(ItemsAS.SHIFTING_STAR_EVORSIO, 4, ResearchProgression.RADIANCE)
                .addPage(recipe(ItemsAS.SHIFTING_STAR_VICIO))
                .addTomeLookup(ItemsAS.SHIFTING_STAR_VICIO, 5, ResearchProgression.RADIANCE)
                .register(ResearchProgression.RADIANCE);

        ResearchNode nodeBaseMantle = new ResearchNode(ItemsAS.MANTLE, "ATT_CAPE", 3, 6)
                .addPage(text("ATT_CAPE.1"))
                .addPage(recipe(ItemsAS.MANTLE))
                .addTomeLookup(ItemsAS.MANTLE, 1, ResearchProgression.RADIANCE)
                .addPage(text("ATT_CAPE.3"))
                .addPage(text("ATT_CAPE.4"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode nodeChalice = new ResearchNode(BlocksAS.CHALICE, "C_CHALICE", 5, 4)
                .addPage(text("C_CHALICE.1"))
                .addPage(recipe(BlocksAS.CHALICE))
                .addTomeLookup(BlocksAS.CHALICE, 1, ResearchProgression.RADIANCE)
                .addPage(text("C_CHALICE.3"))
                .addPage(text("C_CHALICE.4"))
                .addPage(text("C_CHALICE.5"))
                .register(ResearchProgression.RADIANCE);

        //TODO add bore nodes

        nodeBaseMantle.addSourceConnectionFrom(nodeTraitRelayHint);
        nodeChalice.addSourceConnectionFrom(nodeTraitRelayHint);
        nodeObservatory.addSourceConnectionFrom(nodeTraitRelayHint);
        nodeCrystalTraitHint.addSourceConnectionFrom(nodeObservatory);
        nodeIrradiantStars.addSourceConnectionFrom(nodeTraitRelayHint);
    }

    //TODO lens overlay coloration research node view not working
    private static void registerConstellation() {
        ResearchNode nodeInfuser = new ResearchNode(BlocksAS.INFUSER, "INFUSER", 4, 2)
                .addPage(text("INFUSER.1"))
                .addPage(recipe(BlocksAS.INFUSER))
                .addTomeLookup(BlocksAS.INFUSER, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("INFUSER.3"))
                .addPage(structure(StructureTypesAS.PTYPE_INFUSER))
                .register(ResearchProgression.CONSTELLATION);

        //TODO tree beacon

        ResearchNode nodeChargedTools = new ResearchNode(new IItemProvider[] {
                ItemsAS.INFUSED_CRYSTAL_SWORD,
                ItemsAS.INFUSED_CRYSTAL_PICKAXE,
                ItemsAS.INFUSED_CRYSTAL_AXE,
                ItemsAS.INFUSED_CRYSTAL_SHOVEL
        }, "CHARGED_TOOLS", 5, 0)
                .addPage(text("CHARGED_TOOLS.1"))
                .addPage(text("CHARGED_TOOLS.2"))
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_SWORD, 0, ResearchProgression.CONSTELLATION)
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_PICKAXE, 0, ResearchProgression.CONSTELLATION)
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_AXE, 0, ResearchProgression.CONSTELLATION)
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_SHOVEL, 0, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeColoredLenses = new ResearchNode(new IItemProvider[] {
                ItemsAS.COLORED_LENS_FIRE,
                ItemsAS.COLORED_LENS_BREAK,
                ItemsAS.COLORED_LENS_DAMAGE,
                ItemsAS.COLORED_LENS_GROWTH,
                ItemsAS.COLORED_LENS_REGENERATION,
                ItemsAS.COLORED_LENS_PUSH
        }, "LENSES_EFFECTS", 1, 0)
                .addPage(text("LENSES_EFFECTS.1"))
                .addPage(text("LENSES_EFFECTS.2"))
                .addPage(recipe(ItemsAS.COLORED_LENS_FIRE))
                .addTomeLookup(ItemsAS.COLORED_LENS_FIRE, 2, ResearchProgression.CONSTELLATION)
                .addPage(recipe(ItemsAS.COLORED_LENS_BREAK))
                .addTomeLookup(ItemsAS.COLORED_LENS_BREAK, 3, ResearchProgression.CONSTELLATION)
                .addPage(recipe(ItemsAS.COLORED_LENS_DAMAGE))
                .addTomeLookup(ItemsAS.COLORED_LENS_DAMAGE, 4, ResearchProgression.CONSTELLATION)
                .addPage(recipe(ItemsAS.COLORED_LENS_GROWTH))
                .addTomeLookup(ItemsAS.COLORED_LENS_GROWTH, 5, ResearchProgression.CONSTELLATION)
                .addPage(recipe(ItemsAS.COLORED_LENS_REGENERATION))
                .addTomeLookup(ItemsAS.COLORED_LENS_REGENERATION, 6, ResearchProgression.CONSTELLATION)
                .addPage(recipe(ItemsAS.COLORED_LENS_PUSH))
                .addTomeLookup(ItemsAS.COLORED_LENS_PUSH, 7, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeSpectralLens = new ResearchNode(ItemsAS.COLORED_LENS_SPECTRAL, "SPECTRAL_LENS", 0, 1)
                .addPage(text("SPECTRAL_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_SPECTRAL))
                .addTomeLookup(ItemsAS.COLORED_LENS_SPECTRAL, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeRitualLink = new ResearchNode(BlocksAS.RITUAL_LINK, "RITUAL_LINK", 5, 2)
                .addPage(text("RITUAL_LINK.1"))
                .addPage(recipe(BlocksAS.RITUAL_LINK))
                .addTomeLookup(BlocksAS.RITUAL_LINK, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeIlluminationWand = new ResearchNode(ItemsAS.ILLUMINATION_WAND, "ILLUMINATION_WAND", 6, 1)
                .addPage(text("ILLUMINATION_WAND.1"))
                .addPage(recipe(ItemsAS.ILLUMINATION_WAND))
                .addTomeLookup(ItemsAS.ILLUMINATION_WAND, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("ILLUMINATION_WAND.3"))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodePrism = new ResearchNode(BlocksAS.PRISM, "PRISM", 4, 1)
                .addPage(text("PRISM.1"))
                .addPage(recipe(BlocksAS.PRISM))
                .addTomeLookup(BlocksAS.PRISM, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeCollectorCrystal = new ResearchNode(BlocksAS.ROCK_COLLECTOR_CRYSTAL, "COLL_CRYSTAL", 3, 4)
                .addPage(text("COLL_CRYSTAL.1"))
                .addPage(recipe(BlocksAS.ROCK_COLLECTOR_CRYSTAL))
                .addTomeLookup(BlocksAS.ROCK_COLLECTOR_CRYSTAL, 1, ResearchProgression.CONSTELLATION)
                .addTomeLookup(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeEnhancedCollector = new ResearchNode(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL, "ENHANCED_COLLECTOR", 2, 5)
                .addPage(text("ENHANCED_COLLECTOR.1"))
                .addPage(structure(StructureTypesAS.PTYPE_ENHANCED_COLLECTOR_CRYSTAL))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeCrystalCluster = new ResearchNode(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER, "CEL_CRYSTAL_GROW", 0, 2)
                .addPage(text("CEL_CRYSTAL_GROW.1"))
                .addPage(text("CEL_CRYSTAL_GROW.2"))
                .addTomeLookup(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER, 0, ResearchProgression.CONSTELLATION)
                .addTomeLookup(ItemsAS.CELESTIAL_CRYSTAL, 0, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeCelestialCrystals = new ResearchNode(ItemsAS.CELESTIAL_CRYSTAL, "CEL_CRYSTALS", 1, 3)
                .addPage(text("CEL_CRYSTALS.1"))
                .register(ResearchProgression.CONSTELLATION);

        //TODO drawing table & infused glass

        ResearchNode nodeEnchantmentAmulet = new ResearchNode(ItemsAS.ENCHANTMENT_AMULET, "ENCHANTMENT_AMULET", 6, 3)
                .addPage(text("ENCHANTMENT_AMULET.1"))
                .addPage(JournalPageRecipe.fromName(AstralSorcery.key("altar/enchantment_amulet_init")))
                .addTomeLookup(ItemsAS.ENCHANTMENT_AMULET, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("ENCHANTMENT_AMULET.3"))
                .addPage(JournalPageRecipe.fromName(AstralSorcery.key("altar/enchantment_amulet_reroll")))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode nodeAltarUpgradeT4 = new ResearchNode(BlocksAS.ALTAR_RADIANCE, "ALTAR4", 3, 3)
                .addPage(text("ALTAR4.1"))
                .addPage(recipe(BlocksAS.ALTAR_RADIANCE))
                .addTomeLookup(BlocksAS.ALTAR_RADIANCE, 1, ResearchProgression.CONSTELLATION)
                //TODO add altar t4 .addPage(structure(StructureTypesAS.EMPTY))
                .addPage(text("ALTAR4.4"))
                .register(ResearchProgression.CONSTELLATION);

        nodeCelestialCrystals.addSourceConnectionFrom(nodeCrystalCluster);
        nodePrism.addSourceConnectionFrom(nodeInfuser);
        nodeIlluminationWand.addSourceConnectionFrom(nodeInfuser);
        nodeCollectorCrystal.addSourceConnectionFrom(nodeInfuser);
        //TODO tree beacon
        nodeChargedTools.addSourceConnectionFrom(nodeInfuser);
        nodeSpectralLens.addSourceConnectionFrom(nodeColoredLenses);
        nodeEnhancedCollector.addSourceConnectionFrom(nodeCollectorCrystal);
        //TODO drawing table
        nodeEnhancedCollector.addSourceConnectionFrom(nodeCelestialCrystals);
        nodeAltarUpgradeT4.addSourceConnectionFrom(nodeInfuser);
        nodeRitualLink.addSourceConnectionFrom(nodeInfuser);
        nodeAltarUpgradeT4.addSourceConnectionFrom(nodeCelestialCrystals);
        nodeEnchantmentAmulet.addSourceConnectionFrom(nodeInfuser);
    }

    private static void registerAttunement() {
        ResearchNode nodeLinkTool = new ResearchNode(ItemsAS.LINKING_TOOL, "LINKTOOL", 1, 3)
                .addPage(text("LINKTOOL.1"))
                .addPage(recipe(ItemsAS.LINKING_TOOL))
                .addTomeLookup(ItemsAS.LINKING_TOOL, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("LINKTOOL.3"))
                .addPage(text("LINKTOOL.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeLens = new ResearchNode(BlocksAS.LENS, "LENS", 0, 4)
                .addPage(text("LENS.1"))
                .addPage(recipe(BlocksAS.LENS))
                .addTomeLookup(BlocksAS.LENS, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("LENS.3"))
                .addPage(text("LENS.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeStarMetalOre = new ResearchNode(BlocksAS.STARMETAL_ORE, "STARMETAL_ORE", 2, 4)
                .addPage(text("STARMETAL_ORE.1"))
                .addTomeLookup(BlocksAS.STARMETAL_ORE, 0, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeBlockTransmutation = new ResearchNode(new IItemProvider[] {
                Blocks.MAGMA_BLOCK,
                Blocks.SAND,
                Blocks.DIAMOND_ORE,
                Blocks.NETHER_WART_BLOCK,
                Blocks.PUMPKIN,
                Blocks.SANDSTONE
        }, "TRANSMUTATION_ORES", 3, 3)
                .addPage(text("TRANSMUTATION_ORES.1"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeStarMetalResource = new ResearchNode(ItemsAS.STARMETAL_INGOT, "STARMETAL_RES", 4, 5)
                .addPage(text("STARMETAL_RES.1"))
                .addPage(text("STARMETAL_RES.2"))
                .addTomeLookup(ItemsAS.STARMETAL_INGOT, 0, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.STARDUST, 0, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodePlayerAttunement = new ResearchNode(BlocksAS.ATTUNEMENT_ALTAR, "ATT_PLAYER", 5, 3)
                .addPage(text("ATT_PLAYER.1"))
                .addPage(text("ATT_PLAYER.2"))
                .addPage(recipe(BlocksAS.ATTUNEMENT_ALTAR))
                .addTomeLookup(BlocksAS.ATTUNEMENT_ALTAR, 2, ResearchProgression.ATTUNEMENT)
                .addPage(text("ATT_PLAYER.4"))
                .addPage(structure(StructureTypesAS.PTYPE_ATTUNEMENT_ALTAR))
                .addPage(text("ATT_PLAYER.6"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodePlayerPerks = new ResearchNode(BlocksAS.SPECTRAL_RELAY, "ATT_PERKS", 6, 3)
                .addPage(text("ATT_PERKS.1"))
                .addPage(text("ATT_PERKS.2"))
                .addPage(text("ATT_PERKS.3"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodePerkGems = new ResearchNode(new IItemProvider[] {
                ItemsAS.PERK_GEM_DAY,
                ItemsAS.PERK_GEM_NIGHT,
                ItemsAS.PERK_GEM_SKY
        }, "ATT_PERK_GEMS", 7, 3)
                .addPage(text("ATT_PERK_GEMS.1"))
                .addPage(text("ATT_PERK_GEMS.2"))
                .addTomeLookup(ItemsAS.PERK_GEM_DAY, 1, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.PERK_GEM_NIGHT, 1, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.PERK_GEM_SKY, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodePerkSeals = new ResearchNode(ItemsAS.PERK_SEAL, "ATT_PERKS_SEAL", 7, 2)
                .addPage(text("ATT_PERKS_SEAL.1"))
                .addPage(recipe(ItemsAS.PERK_SEAL))
                .addTomeLookup(ItemsAS.PERK_SEAL, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeCrystalAttunement = new ResearchNode(ItemsAS.ATTUNED_ROCK_CRYSTAL, "ATT_CRYSTAL", 7, 4)
                .addPage(text("ATT_CRYSTAL.1"))
                .addPage(text("ATT_CRYSTAL.2"))
                .addTomeLookup(ItemsAS.ATTUNED_ROCK_CRYSTAL, 0, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL, 0, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeRitualPedestal = new ResearchNode(BlocksAS.RITUAL_PEDESTAL, "RIT_PEDESTAL", 8, 3)
                .addPage(text("RIT_PEDESTAL.1"))
                .addPage(recipe(BlocksAS.RITUAL_PEDESTAL))
                .addTomeLookup(BlocksAS.RITUAL_PEDESTAL, 1, ResearchProgression.ATTUNEMENT)
                .addPage(structure(StructureTypesAS.PTYPE_RITUAL_PEDESTAL))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeRitualPedestalAcceleration = new ResearchNode(BlocksAS.RITUAL_PEDESTAL, "PED_ACCEL", 8, 2)
                .addPage(text("PED_ACCEL.1"))
                .addPage(text("PED_ACCEL.2"))
                .addPage(text("PED_ACCEL.3"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeTelescope = new ResearchNode(BlocksAS.TELESCOPE, "TELESCOPE", 1, 5)
                .addPage(text("TELESCOPE.1"))
                .addPage(recipe(BlocksAS.TELESCOPE))
                .addTomeLookup(BlocksAS.TELESCOPE, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("TELESCOPE.3"))
                .addPage(text("TELESCOPE.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeBuildExchangeWands = new ResearchNode(new IItemProvider[] {
                ItemsAS.ARCHITECT_WAND, ItemsAS.EXCHANGE_WAND
        }, "TOOL_WANDS", 4, 3)
                .addPage(text("TOOL_WANDS.1"))
                .addPage(recipe(ItemsAS.ARCHITECT_WAND))
                .addTomeLookup(ItemsAS.ARCHITECT_WAND, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("TOOL_WANDS.3"))
                .addPage(recipe(ItemsAS.EXCHANGE_WAND))
                .addTomeLookup(ItemsAS.EXCHANGE_WAND, 3, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeGrapplingWand = new ResearchNode(ItemsAS.GRAPPLE_WAND, "GRAPPLE_WAND", 3, 2)
                .addPage(text("GRAPPLE_WAND.1"))
                .addPage(recipe(ItemsAS.GRAPPLE_WAND))
                .addTomeLookup(ItemsAS.GRAPPLE_WAND, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeAttunementCharge = new ResearchNode(new SpriteQuery(AssetLoader.TextureLocation.EFFECT, 6, 8, "relay_flare"),"QUICK_CHARGE", 4, 4)
                .addPage(text("QUICK_CHARGE.1"))
                .register(ResearchProgression.ATTUNEMENT);

        //TODO domic resonator

        ResearchNode nodeInfoFocusedStarlight = new ResearchNode(BlocksAS.LENS, "STARLIGHT_NETWORK", 3, 5)
                .addPage(text("STARLIGHT_NETWORK.1"))
                .register(ResearchProgression.ATTUNEMENT);

        //TODO celestial gateway

        ResearchNode nodeShiftingStar = new ResearchNode(ItemsAS.SHIFTING_STAR, "SHIFT_STAR", 6, 2)
                .addPage(text("SHIFT_STAR.1"))
                .addPage(recipe(ItemsAS.SHIFTING_STAR))
                .addTomeLookup(ItemsAS.SHIFTING_STAR, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeKnowledgeSharing = new ResearchNode(ItemsAS.KNOWLEDGE_SHARE, "KNOWLEDGE_SHARE", 2, 6)
                .addPage(text("KNOWLEDGE_SHARE.1"))
                .addPage(recipe(ItemsAS.SHIFTING_STAR))
                .addTomeLookup(ItemsAS.SHIFTING_STAR, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode nodeAltarUpgradeT3 = new ResearchNode(BlocksAS.ALTAR_CONSTELLATION, "ALTAR3", 5, 7)
                .addPage(text("ALTAR3.1"))
                .addPage(recipe(BlocksAS.ALTAR_CONSTELLATION))
                .addTomeLookup(BlocksAS.ALTAR_CONSTELLATION, 1, ResearchProgression.ATTUNEMENT)
                //TODO altar3 structure .addPage(structure(StructureTypesAS.EMPTY))
                .addPage(text("ALTAR3.4"))
                .register(ResearchProgression.ATTUNEMENT);

        nodeStarMetalOre.addSourceConnectionFrom(nodeLinkTool);
        nodeStarMetalOre.addSourceConnectionFrom(nodeLens);
        nodeStarMetalResource.addSourceConnectionFrom(nodeStarMetalOre);
        nodeAltarUpgradeT3.addSourceConnectionFrom(nodeStarMetalResource);
        nodeBlockTransmutation.addSourceConnectionFrom(nodeStarMetalOre);
        nodePlayerAttunement.addSourceConnectionFrom(nodeAttunementCharge);
        nodeCrystalAttunement.addSourceConnectionFrom(nodePlayerAttunement);
        nodePlayerPerks.addSourceConnectionFrom(nodePlayerAttunement);
        nodeAttunementCharge.addSourceConnectionFrom(nodeStarMetalResource);
        nodeRitualPedestal.addSourceConnectionFrom(nodeCrystalAttunement);
        nodeRitualPedestalAcceleration.addSourceConnectionFrom(nodeRitualPedestal);
        nodeBuildExchangeWands.addSourceConnectionFrom(nodeAttunementCharge);
        nodeInfoFocusedStarlight.addSourceConnectionFrom(nodeStarMetalOre);
        //TODO cel gateway from node starmetal resources
        nodeShiftingStar.addSourceConnectionFrom(nodePlayerAttunement);
        nodeGrapplingWand.addSourceConnectionFrom(nodeAttunementCharge);
        //TODO domic resonator from node starmetal resources
        nodePerkSeals.addSourceConnectionFrom(nodePlayerPerks);
        nodePerkGems.addSourceConnectionFrom(nodePlayerPerks);
    }

    private static void registerCrafting() {
        ResearchNode nodeHandTelescope = new ResearchNode(ItemsAS.HAND_TELESCOPE, "HAND_TELESCOPE", 2, 2)
                .addPage(text("HAND_TELESCOPE.1"))
                .addPage(recipe(ItemsAS.GLASS_LENS))
                .addTomeLookup(ItemsAS.GLASS_LENS, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.HAND_TELESCOPE))
                .addTomeLookup(ItemsAS.HAND_TELESCOPE, 2, ResearchProgression.BASIC_CRAFT)
                .addPage(text("HAND_TELESCOPE.4"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeRelay = new ResearchNode(BlocksAS.SPECTRAL_RELAY, "SPEC_RELAY", 2, 0)
                .addPage(text("SPEC_RELAY.1"))
                .addPage(recipe(BlocksAS.SPECTRAL_RELAY))
                .addTomeLookup(BlocksAS.SPECTRAL_RELAY, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(structure(StructureTypesAS.PTYPE_SPECTRAL_RELAY))
                .addPage(text("SPEC_RELAY.4"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeWell = new ResearchNode(BlocksAS.WELL, "WELL", 0, 2)
                .addPage(text("WELL.1"))
                .addPage(recipe(BlocksAS.WELL))
                .addTomeLookup(BlocksAS.WELL, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(text("WELL.3"))
                .register(ResearchProgression.BASIC_CRAFT);

        //TODO node resonator

        ResearchNode nodeTools = new ResearchNode(new IItemProvider[] {
                ItemsAS.CRYSTAL_SWORD,
                ItemsAS.CRYSTAL_PICKAXE,
                ItemsAS.CRYSTAL_AXE,
                ItemsAS.CRYSTAL_SHOVEL
        }, "TOOLS", 2, 5)
                .addPage(text("TOOLS.1"))
                .addPage(text("TOOLS.2"))
                .addPage(text("TOOLS.3"))
                .addPage(recipe(ItemsAS.CRYSTAL_SWORD))
                .addTomeLookup(ItemsAS.CRYSTAL_SWORD, 3, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.CRYSTAL_PICKAXE))
                .addTomeLookup(ItemsAS.CRYSTAL_PICKAXE, 4, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.CRYSTAL_AXE))
                .addTomeLookup(ItemsAS.CRYSTAL_AXE, 5, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.CRYSTAL_SHOVEL))
                .addTomeLookup(ItemsAS.CRYSTAL_SHOVEL, 6, ResearchProgression.BASIC_CRAFT)
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeIlluminationPowder = new ResearchNode(ItemsAS.ILLUMINATION_POWDER, "ILLUM_POWDER", 3, 3)
                .addPage(text("ILLUM_POWDER.1"))
                .addPage(recipe(ItemsAS.ILLUMINATION_POWDER))
                .addTomeLookup(ItemsAS.ILLUMINATION_POWDER, 1, ResearchProgression.BASIC_CRAFT)
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeNocturnalPowder = new ResearchNode(ItemsAS.NOCTURNAL_POWDER, "NOC_POWDER", 4, 4)
                .addPage(text("NOC_POWDER.1"))
                .addPage(recipe(ItemsAS.NOCTURNAL_POWDER))
                .addTomeLookup(ItemsAS.NOCTURNAL_POWDER, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(text("NOC_POWDER.3"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeIlluminator = new ResearchNode(BlocksAS.ILLUMINATOR, "ILLUMINATOR", 4, 1)
                .addPage(text("ILLUMINATOR.1"))
                .addPage(recipe(BlocksAS.ILLUMINATOR))
                .addTomeLookup(BlocksAS.ILLUMINATOR, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(text("ILLUMINATOR.3"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeCrystals = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "ROCK_CRYSTALS", 0, 4)
                .addPage(text("ROCK_CRYSTALS.1"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeCrystalGrowth = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "CRYSTAL_GROWTH", 1, 3)
                .addPage(text("CRYSTAL_GROWTH.1"))
                .addPage(text("CRYSTAL_GROWTH.2"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeInfusedWoods = new ResearchNode(new IItemProvider[] {
                BlocksAS.INFUSED_WOOD,
                BlocksAS.INFUSED_WOOD_ARCH,
                BlocksAS.INFUSED_WOOD_COLUMN,
                BlocksAS.INFUSED_WOOD_ENGRAVED,
                BlocksAS.INFUSED_WOOD_ENRICHED,
                BlocksAS.INFUSED_WOOD_PLANKS,
                BlocksAS.INFUSED_WOOD_STAIRS,
                BlocksAS.INFUSED_WOOD_SLAB
        }, "INFUSED_WOOD", 0, 1)
                .addPage(text("INFUSED_WOOD.1"))
                .addPage(recipe(BlocksAS.INFUSED_WOOD_PLANKS))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_PLANKS, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(BlocksAS.INFUSED_WOOD_ARCH))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_ARCH, 2, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(BlocksAS.INFUSED_WOOD_COLUMN))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_COLUMN, 3, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(BlocksAS.INFUSED_WOOD_ENGRAVED))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_ENGRAVED, 4, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(BlocksAS.INFUSED_WOOD_ENRICHED))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_ENRICHED, 5, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(BlocksAS.INFUSED_WOOD_STAIRS))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_STAIRS, 6, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(BlocksAS.INFUSED_WOOD_SLAB))
                .addTomeLookup(BlocksAS.INFUSED_WOOD_SLAB, 7, ResearchProgression.BASIC_CRAFT)
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode nodeAltarUpgradeT2 = new ResearchNode(BlocksAS.ALTAR_ATTUNEMENT, "ALTAR2", 2, 1)
                .addPage(text("ALTAR2.1"))
                .addPage(recipe(BlocksAS.ALTAR_ATTUNEMENT))
                .addTomeLookup(BlocksAS.ALTAR_ATTUNEMENT, 1, ResearchProgression.BASIC_CRAFT)
                //TODO altar2 structure .addPage(structure(StructureTypesAS.EMPTY))
                .addPage(text("ALTAR2.4"))
                .register(ResearchProgression.BASIC_CRAFT);

        nodeTools.addSourceConnectionFrom(nodeCrystals);
        nodeAltarUpgradeT2.addSourceConnectionFrom(nodeWell);
        //TODO add resonator -> well
        //TODO node -> resonator nodeRelay.addSourceConnectionFrom(noderes)
        nodeCrystalGrowth.addSourceConnectionFrom(nodeWell);
        nodeCrystalGrowth.addSourceConnectionFrom(nodeCrystals);
        nodeIlluminator.addSourceConnectionFrom(nodeIlluminationPowder);
        nodeNocturnalPowder.addSourceConnectionFrom(nodeIlluminationPowder);
        nodeInfusedWoods.addSourceConnectionFrom(nodeWell);
    }

    private static void registerDiscovery() {
        ResearchNode nodeShrines = new ResearchNode(BlocksAS.ROCK_COLLECTOR_CRYSTAL, "SHRINES", 0, 1)
                .addPage(text("SHRINES.1"))
                .addPage(text("SHRINES.2"))
                .addPage(text("SHRINES.3"))
                .register(ResearchProgression.DISCOVERY);

        ResearchNode nodeConstellationPapers = new ResearchNode(ItemsAS.CONSTELLATION_PAPER, "CPAPER", 1, 0)
                .addPage(text("CPAPER.1"))
                .addPage(recipe(ItemsAS.TOME))
                .addPage(text("CPAPER.3"))
                .addPage(recipe(ItemsAS.PARCHMENT))
                .addTomeLookup(ItemsAS.CONSTELLATION_PAPER, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(ItemsAS.TOME, 1, ResearchProgression.DISCOVERY)
                .addTomeLookup(ItemsAS.PARCHMENT, 3, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode nodeWand = new ResearchNode(ItemsAS.WAND, "WAND", 2, 2)
                .addPage(text("WAND.1"))
                .addPage(recipe(ItemsAS.WAND))
                .addTomeLookup(ItemsAS.WAND, 1, ResearchProgression.DISCOVERY)
                .addPage(text("WAND.3"))
                .register(ResearchProgression.DISCOVERY);

        ResearchNode nodeOres = new ResearchNode(new IItemProvider[] {
                BlocksAS.ROCK_CRYSTAL_ORE,
                BlocksAS.AQUAMARINE_SAND_ORE
        }, "ORES", 1, 3)
                .addPage(text("ORES.1"))
                .addPage(text("ORES.2"))
                .addTomeLookup(ItemsAS.AQUAMARINE, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(ItemsAS.ROCK_CRYSTAL, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(BlocksAS.AQUAMARINE_SAND_ORE, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(BlocksAS.ROCK_CRYSTAL_ORE, 0, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode nodeMarbleTypes = new ResearchNode(new IItemProvider[] {
                BlocksAS.MARBLE_RAW,
                BlocksAS.MARBLE_PILLAR,
                BlocksAS.MARBLE_ARCH,
                BlocksAS.MARBLE_BRICKS,
                BlocksAS.MARBLE_CHISELED,
                BlocksAS.MARBLE_ENGRAVED,
                BlocksAS.MARBLE_RUNED,
                BlocksAS.MARBLE_SLAB,
                BlocksAS.MARBLE_STAIRS
        }, "MARBLETYPES", 3, 1)
                .addPage(text("MARBLETYPES.1"))
                .addPage(recipe(BlocksAS.MARBLE_PILLAR))
                .addTomeLookup(BlocksAS.MARBLE_PILLAR, 1, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_ARCH))
                .addTomeLookup(BlocksAS.MARBLE_ARCH, 2, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_BRICKS))
                .addTomeLookup(BlocksAS.MARBLE_BRICKS, 3, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_CHISELED))
                .addTomeLookup(BlocksAS.MARBLE_CHISELED, 4, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_ENGRAVED))
                .addTomeLookup(BlocksAS.MARBLE_ENGRAVED, 5, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_RUNED))
                .addTomeLookup(BlocksAS.MARBLE_RUNED, 6, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_SLAB))
                .addTomeLookup(BlocksAS.MARBLE_SLAB, 7, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_STAIRS))
                .addTomeLookup(BlocksAS.MARBLE_STAIRS, 8, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode nodeSootyMarbleTypes = new ResearchNode(new IItemProvider[] {
                BlocksAS.BLACK_MARBLE_RAW,
                BlocksAS.BLACK_MARBLE_PILLAR,
                BlocksAS.BLACK_MARBLE_ARCH,
                BlocksAS.BLACK_MARBLE_BRICKS,
                BlocksAS.BLACK_MARBLE_CHISELED,
                BlocksAS.BLACK_MARBLE_ENGRAVED,
                BlocksAS.BLACK_MARBLE_RUNED,
                BlocksAS.BLACK_MARBLE_SLAB,
                BlocksAS.BLACK_MARBLE_STAIRS
        }, "SOOTYMARBLE", 5, 2)
                .addPage(text("SOOTYMARBLE.1"))
                .addPage(recipe(BlocksAS.BLACK_MARBLE_PILLAR))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_PILLAR, 1, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_ARCH))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_ARCH, 2, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_BRICKS))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_BRICKS, 3, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_CHISELED))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_CHISELED, 4, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_ENGRAVED))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_ENGRAVED, 5, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_RUNED))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_RUNED, 6, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_SLAB))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_SLAB, 7, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_STAIRS))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_STAIRS, 8, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode nodeAltarUpgradeT1 = new ResearchNode(BlocksAS.ALTAR_DISCOVERY, "ALTAR1", 4, 3)
                .addPage(text("ALTAR1.1"))
                .addPage(text("ALTAR1.2"))
                .addPage(recipe(BlocksAS.ALTAR_DISCOVERY))
                .addTomeLookup(BlocksAS.ALTAR_DISCOVERY, 2, ResearchProgression.DISCOVERY)
                .addPage(text("ALTAR1.4"))
                .addPage(text("ALTAR1.5"))
                .register(ResearchProgression.DISCOVERY);

        nodeWand.addSourceConnectionFrom(nodeShrines);
        nodeConstellationPapers.addSourceConnectionFrom(nodeShrines);
        nodeWand.addSourceConnectionFrom(nodeOres);
        nodeAltarUpgradeT1.addSourceConnectionFrom(nodeWand);
        nodeSootyMarbleTypes.addSourceConnectionFrom(nodeMarbleTypes);
    }

    private static JournalPage recipe(IItemProvider outputItem) {
        return JournalPageRecipe.fromOutput(stack -> !stack.isEmpty() && stack.getItem().equals(outputItem.asItem()));
    }

    private static JournalPage structure(StructureType structure) {
        return new JournalPageStructure(structure.getStructure());
    }

    private static JournalPage text(String identifier) {
        return new JournalPageText(AstralSorcery.MODID.toLowerCase() + ".journal." + identifier + ".text");
    }
}
