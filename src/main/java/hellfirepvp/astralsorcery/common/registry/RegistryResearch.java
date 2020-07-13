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
import hellfirepvp.astralsorcery.common.data.journal.*;
import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import hellfirepvp.astralsorcery.common.data.research.ResearchProgression;
import hellfirepvp.astralsorcery.common.item.ItemResonator;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IItemProvider;

import java.util.function.Predicate;

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
        ResearchNode resAttuneCrystalTrait = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "ATT_TRAIT", 0, 0)
                .addPage(text("ATT_TRAIT.1"))
                .addPage(text("ATT_TRAIT.2"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode resObservatory = new ResearchNode(BlocksAS.OBSERVATORY, "OBSERVATORY", 1.5F, 0.25F)
                .addPage(text("OBSERVATORY.1"))
                .addPage(recipe(BlocksAS.OBSERVATORY))
                .addTomeLookup(BlocksAS.OBSERVATORY, 1, ResearchProgression.RADIANCE)
                .addPage(text("OBSERVATORY.3"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode resIrradiantStars = new ResearchNode(new IItemProvider[] {
                ItemsAS.SHIFTING_STAR_AEVITAS,
                ItemsAS.SHIFTING_STAR_ARMARA,
                ItemsAS.SHIFTING_STAR_DISCIDIA,
                ItemsAS.SHIFTING_STAR_EVORSIO,
                ItemsAS.SHIFTING_STAR_VICIO
        }, "ENH_SHIFTING_STAR", 3.25F, 0.5F)
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

        ResearchNode resRelayCrafting = new ResearchNode(BlocksAS.ALTAR_RADIANCE, "CRAFTING_FOCUS_HINT", 2.5F, 2F)
                .addPage(text("CRAFTING_FOCUS_HINT.1"))
                .addPage(text("CRAFTING_FOCUS_HINT.2"))
                .addPage(text("CRAFTING_FOCUS_HINT.3"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode resMantle = new ResearchNode(ItemsAS.MANTLE, "ATT_CAPE", 1.25F, 2.5F)
                .addPage(text("ATT_CAPE.1"))
                .addPage(recipe(stack -> ItemsAS.MANTLE.equals(stack.getItem()) && ItemsAS.MANTLE.getConstellation(stack) == null))
                .addTomeLookup(ItemsAS.MANTLE, 1, ResearchProgression.RADIANCE)
                .addPage(text("ATT_CAPE.3"))
                .addPage(text("ATT_CAPE.4"))
                .register(ResearchProgression.RADIANCE);

        ResearchNode resChalice = new ResearchNode(BlocksAS.CHALICE, "C_CHALICE", 3, 3)
                .addPage(text("C_CHALICE.1"))
                .addPage(recipe(BlocksAS.CHALICE))
                .addTomeLookup(BlocksAS.CHALICE, 1, ResearchProgression.RADIANCE)
                .addPage(text("C_CHALICE.3"))
                .addPage(text("C_CHALICE.4"))
                .register(ResearchProgression.RADIANCE);

        //TODO evershifting fountain
        ResearchNode resFountain = new ResearchNode(Items.APPLE, "BORE_CORE", 2.25F, 4F)
                .addPage(text("BORE_CORE.1"))
                .addPage(text("BORE_CORE.2"))
                .register(ResearchProgression.RADIANCE);

        //TODO neromantic prime
        ResearchNode resLiquidBore = new ResearchNode(Items.APPLE, "BORE_HEAD_LIQUID", 1.5F, 5F)
                .addPage(text("BORE_HEAD_LIQUID.1"))
                .addPage(text("BORE_HEAD_LIQUID.2"))
                .addPage(text("BORE_HEAD_LIQUID.4"))
                .register(ResearchProgression.RADIANCE);

        ItemStack liquidResonator = ItemResonator.setUpgradeUnlocked(new ItemStack(ItemsAS.RESONATOR), ItemResonator.ResonatorUpgrade.STARLIGHT, ItemResonator.ResonatorUpgrade.FLUID_FIELDS);
        ItemResonator.setCurrentUpgradeUnsafe(liquidResonator, ItemResonator.ResonatorUpgrade.FLUID_FIELDS);
        ResearchNode resLiquidResonator = new ResearchNode(liquidResonator, "ICHOSIC", 0, 4.5F)
                .addPage(text("ICHOSIC.1"))
                .addPage(recipe(stack -> ItemsAS.RESONATOR.equals(stack.getItem()) && ItemResonator.getCurrentUpgrade(null, stack) == ItemResonator.ResonatorUpgrade.FLUID_FIELDS))
                .register(ResearchProgression.RADIANCE);

        //TODO fysallidic prime
        ResearchNode resVortexBore = new ResearchNode(Items.APPLE, "BORE_HEAD_VORTEX", 3.5F, 4.75F)
                .addPage(text("BORE_HEAD_VORTEX.1"))
                .addPage(text("BORE_HEAD_VORTEX.3"))
                .register(ResearchProgression.RADIANCE);

        resIrradiantStars.addSourceConnectionFrom(resRelayCrafting);
        resMantle.addSourceConnectionFrom(resRelayCrafting);
        resObservatory.addSourceConnectionFrom(resRelayCrafting);
        resAttuneCrystalTrait.addSourceConnectionFrom(resObservatory);
        resChalice.addSourceConnectionFrom(resRelayCrafting);
        resFountain.addSourceConnectionFrom(resChalice);
        resVortexBore.addSourceConnectionFrom(resFountain);
        resLiquidBore.addSourceConnectionFrom(resFountain);
        resLiquidResonator.addSourceConnectionFrom(resLiquidBore);
    }

    private static void registerConstellation() {
        ResearchNode resLenses = new ResearchNode(ItemsAS.GLASS_LENS, "LENSES_EFFECTS", 6.25F, 1.75F)
                .addPage(text("LENSES_EFFECTS.1"))
                .addPage(text("LENSES_EFFECTS.2"))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensFire = new ResearchNode(ItemsAS.COLORED_LENS_FIRE, "IGNITION_LENS", 5.5F, 0.5F)
                .addPage(text("IGNITION_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_FIRE))
                .addTomeLookup(ItemsAS.COLORED_LENS_FIRE, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensBreak = new ResearchNode(ItemsAS.COLORED_LENS_BREAK, "BREAK_LENS", 6.75F, 0.25F)
                .addPage(text("BREAK_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_BREAK))
                .addTomeLookup(ItemsAS.COLORED_LENS_BREAK, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensDamage = new ResearchNode(ItemsAS.COLORED_LENS_DAMAGE, "DAMAGE_LENS", 7.5F, 1.25F)
                .addPage(text("DAMAGE_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_DAMAGE))
                .addTomeLookup(ItemsAS.COLORED_LENS_DAMAGE, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensPush = new ResearchNode(ItemsAS.COLORED_LENS_PUSH, "PUSH_LENS", 7.25F, 2.25F)
                .addPage(text("PUSH_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_PUSH))
                .addTomeLookup(ItemsAS.COLORED_LENS_PUSH, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensRegeneration = new ResearchNode(ItemsAS.COLORED_LENS_REGENERATION, "REGENERATION_LENS", 6.75F, 3F)
                .addPage(text("REGENERATION_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_REGENERATION))
                .addTomeLookup(ItemsAS.COLORED_LENS_REGENERATION, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensGrowth = new ResearchNode(ItemsAS.COLORED_LENS_GROWTH, "GROWTH_LENS", 5.75F, 2.75F)
                .addPage(text("GROWTH_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_GROWTH))
                .addTomeLookup(ItemsAS.COLORED_LENS_GROWTH, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resColoredLensSpectral = new ResearchNode(ItemsAS.COLORED_LENS_SPECTRAL, "SPECTRAL_LENS", 4.75F, 2)
                .addPage(text("SPECTRAL_LENS.1"))
                .addPage(recipe(ItemsAS.COLORED_LENS_SPECTRAL))
                .addTomeLookup(ItemsAS.COLORED_LENS_SPECTRAL, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resInfuser = new ResearchNode(BlocksAS.INFUSER, "INFUSER", 2, 1.75F)
                .addPage(text("INFUSER.1"))
                .addPage(recipe(BlocksAS.INFUSER))
                .addTomeLookup(BlocksAS.INFUSER, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("INFUSER.3"))
                .addPage(structure(StructureTypesAS.PTYPE_INFUSER))
                .addPage(recipeInfusion(ItemsAS.RESONATING_GEM))
                .addTomeLookup(ItemsAS.RESONATING_GEM, 4, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resEnchantmentAmulet = new ResearchNode(ItemsAS.ENCHANTMENT_AMULET, "ENCHANTMENT_AMULET", 1.75F, 3.25F)
                .addPage(text("ENCHANTMENT_AMULET.1"))
                .addPage(JournalPageRecipe.fromName(AstralSorcery.key("altar/enchantment_amulet_init")))
                .addTomeLookup(ItemsAS.ENCHANTMENT_AMULET, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("ENCHANTMENT_AMULET.3"))
                .addPage(JournalPageRecipe.fromName(AstralSorcery.key("altar/enchantment_amulet_reroll")))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resRitualLink = new ResearchNode(BlocksAS.RITUAL_LINK, "RITUAL_LINK", 0.5F, 3.5F)
                .addPage(text("RITUAL_LINK.1"))
                .addPage(recipe(BlocksAS.RITUAL_LINK))
                .addTomeLookup(BlocksAS.RITUAL_LINK, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resIlluminationWand = new ResearchNode(ItemsAS.ILLUMINATION_WAND, "ILLUMINATION_WAND", 0.25F, 2.5F)
                .addPage(text("ILLUMINATION_WAND.1"))
                .addPage(recipe(ItemsAS.ILLUMINATION_WAND))
                .addTomeLookup(ItemsAS.ILLUMINATION_WAND, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("ILLUMINATION_WAND.3"))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resInfusedTools = new ResearchNode(new IItemProvider[] {
                ItemsAS.INFUSED_CRYSTAL_SWORD,
                ItemsAS.INFUSED_CRYSTAL_PICKAXE,
                ItemsAS.INFUSED_CRYSTAL_AXE,
                ItemsAS.INFUSED_CRYSTAL_SHOVEL
        }, "CHARGED_TOOLS", 0.25F, 1.25F)
                .addPage(text("CHARGED_TOOLS.1"))
                .addPage(text("CHARGED_TOOLS.2"))
                .addPage(recipeInfusion(ItemsAS.INFUSED_CRYSTAL_SWORD))
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_SWORD, 2, ResearchProgression.CONSTELLATION)
                .addPage(recipeInfusion(ItemsAS.INFUSED_CRYSTAL_PICKAXE))
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_PICKAXE, 3, ResearchProgression.CONSTELLATION)
                .addPage(recipeInfusion(ItemsAS.INFUSED_CRYSTAL_AXE))
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_AXE, 4, ResearchProgression.CONSTELLATION)
                .addPage(recipeInfusion(ItemsAS.INFUSED_CRYSTAL_SHOVEL))
                .addTomeLookup(ItemsAS.INFUSED_CRYSTAL_SHOVEL, 5, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        //TODO tree beacon
        ResearchNode resTreeBeacon = new ResearchNode(Items.APPLE, "TREEBEACON", 1.25F, 0.5F)
                .addPage(text("TREEBEACON.1"))
                //.addPage(recipe(BlocksAS.TREE_BEACON))
                //.addTomeLookup(BlocksAS.TREE_BEACON, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("TREEBEACON.3"))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resPrism = new ResearchNode(BlocksAS.PRISM, "PRISM", 2.75F, 0)
                .addPage(text("PRISM.1"))
                .addPage(recipe(BlocksAS.PRISM))
                .addTomeLookup(BlocksAS.PRISM, 1, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resEngravingTable = new ResearchNode(BlocksAS.REFRACTION_TABLE, "DRAWING_TABLE", 3.5F, 1)
                .addPage(text("DRAWING_TABLE.1"))
                .addPage(recipe(BlocksAS.REFRACTION_TABLE))
                .addTomeLookup(BlocksAS.REFRACTION_TABLE, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("DRAWING_TABLE.3"))
                .addPage(text("DRAWING_TABLE.4"))
                .addPage(recipe(ItemsAS.INFUSED_GLASS))
                .addTomeLookup(ItemsAS.INFUSED_GLASS, 4, ResearchProgression.CONSTELLATION)
                .addPage(text("DRAWING_TABLE.6"))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resAltar4 = new ResearchNode(BlocksAS.ALTAR_RADIANCE, "ALTAR4", 3.5F, 3)
                .addPage(text("ALTAR4.1"))
                .addPage(recipe(BlocksAS.ALTAR_RADIANCE))
                .addTomeLookup(BlocksAS.ALTAR_RADIANCE, 1, ResearchProgression.CONSTELLATION)
                .addPage(structure(StructureTypesAS.PTYPE_ALTAR_TRAIT))
                .addPage(text("ALTAR4.4"))
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resCollectorCrystal = new ResearchNode(BlocksAS.ROCK_COLLECTOR_CRYSTAL, "COLL_CRYSTAL", 2.75F, 3.75F)
                .addPage(text("COLL_CRYSTAL.1"))
                .addPage(recipe(BlocksAS.ROCK_COLLECTOR_CRYSTAL))
                .addTomeLookup(BlocksAS.ROCK_COLLECTOR_CRYSTAL, 1, ResearchProgression.CONSTELLATION)
                .addTomeLookup(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL, 1, ResearchProgression.CONSTELLATION)
                .addPage(text("COLL_CRYSTAL.3"))
                .register(ResearchProgression.CONSTELLATION);

        ItemStack celestialCrystalCluster = new ItemStack(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER);
        celestialCrystalCluster.setDamage(4); //Growth stage 4
        ResearchNode resCelestialCrystalCluster = new ResearchNode(celestialCrystalCluster, "CEL_CRYSTAL_GROW", 6.25F, 4)
                .addPage(text("CEL_CRYSTAL_GROW.1"))
                .addPage(text("CEL_CRYSTAL_GROW.2"))
                .addPage(text("CEL_CRYSTAL_GROW.3"))
                .addPage(text("CEL_CRYSTAL_GROW.4"))
                .addTomeLookup(BlocksAS.CELESTIAL_CRYSTAL_CLUSTER, 0, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resCelestialCrystals = new ResearchNode(ItemsAS.CELESTIAL_CRYSTAL, "CEL_CRYSTALS", 5, 3.75F)
                .addPage(text("CEL_CRYSTALS.1"))
                .addTomeLookup(ItemsAS.CELESTIAL_CRYSTAL, 0, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.CONSTELLATION);

        ResearchNode resEnhancedCollectorCrystal = new ResearchNode(BlocksAS.CELESTIAL_COLLECTOR_CRYSTAL, "ENHANCED_COLLECTOR", 4, 4.5F)
                .addPage(text("ENHANCED_COLLECTOR.1"))
                .addPage(structure(StructureTypesAS.PTYPE_ENHANCED_COLLECTOR_CRYSTAL))
                .register(ResearchProgression.CONSTELLATION);

        resColoredLensFire.addSourceConnectionFrom(resLenses);
        resColoredLensBreak.addSourceConnectionFrom(resLenses);
        resColoredLensDamage.addSourceConnectionFrom(resLenses);
        resColoredLensPush.addSourceConnectionFrom(resLenses);
        resColoredLensRegeneration.addSourceConnectionFrom(resLenses);
        resColoredLensGrowth.addSourceConnectionFrom(resLenses);
        resColoredLensSpectral.addSourceConnectionFrom(resLenses);
        resColoredLensSpectral.addSourceConnectionFrom(resInfuser);
        resEngravingTable.addSourceConnectionFrom(resColoredLensSpectral);

        resEnchantmentAmulet.addSourceConnectionFrom(resInfuser);
        resRitualLink.addSourceConnectionFrom(resInfuser);
        resIlluminationWand.addSourceConnectionFrom(resInfuser);
        resInfusedTools.addSourceConnectionFrom(resInfuser);
        resTreeBeacon.addSourceConnectionFrom(resInfuser);
        resPrism.addSourceConnectionFrom(resInfuser);
        resAltar4.addSourceConnectionFrom(resInfuser);
        resEngravingTable.addSourceConnectionFrom(resInfuser);
        resCollectorCrystal.addSourceConnectionFrom(resInfuser);

        resCelestialCrystals.addSourceConnectionFrom(resCelestialCrystalCluster);
        resAltar4.addSourceConnectionFrom(resCelestialCrystals);
        resEnhancedCollectorCrystal.addSourceConnectionFrom(resCollectorCrystal);
        resEnhancedCollectorCrystal.addSourceConnectionFrom(resCelestialCrystals);
    }

    private static void registerAttunement() {
        ResearchNode resTelescope = new ResearchNode(BlocksAS.TELESCOPE, "TELESCOPE", 0.5F, 0)
                .addPage(text("TELESCOPE.1"))
                .addPage(recipe(BlocksAS.TELESCOPE))
                .addTomeLookup(BlocksAS.TELESCOPE, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("TELESCOPE.3"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resKnowledgeShare = new ResearchNode(ItemsAS.KNOWLEDGE_SHARE, "KNOWLEDGE_SHARE", 2.5F, 0.25F)
                .addPage(text("KNOWLEDGE_SHARE.1"))
                .addPage(recipe(ItemsAS.KNOWLEDGE_SHARE))
                .addTomeLookup(ItemsAS.KNOWLEDGE_SHARE, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resLens = new ResearchNode(BlocksAS.LENS, "LENS", 0, 1.25F)
                .addPage(text("LENS.1"))
                .addPage(recipe(BlocksAS.LENS))
                .addTomeLookup(BlocksAS.LENS, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("LENS.3"))
                .addPage(text("LENS.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resLinkingTool = new ResearchNode(ItemsAS.LINKING_TOOL, "LINKTOOL", 0.25F, 2.25F)
                .addPage(text("LINKTOOL.1"))
                .addPage(recipe(ItemsAS.LINKING_TOOL))
                .addTomeLookup(ItemsAS.LINKING_TOOL, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("LINKTOOL.3"))
                .addPage(text("LINKTOOL.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resStarlightNetwork = new ResearchNode(BlocksAS.LENS, "STARLIGHT_NETWORK", 1.5F, 1)
                .addPage(text("STARLIGHT_NETWORK.1"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resTransmutationOres = new ResearchNode(new IItemProvider[] {
                Blocks.MAGMA_BLOCK,
                Blocks.SAND,
                Blocks.DIAMOND_ORE,
                Blocks.NETHER_WART_BLOCK,
                Blocks.PUMPKIN,
                Blocks.SANDSTONE
        }, "TRANSMUTATION_ORES", 2.75F, 1.5F)
                .addPage(text("TRANSMUTATION_ORES.1"))
                .addPage(recipeTransmutation(Blocks.CLAY))
                .addPage(recipeTransmutation(Blocks.EMERALD_ORE))
                .addPage(recipeTransmutation(Blocks.CAKE))
                .addPage(recipeTransmutation(Blocks.LAPIS_BLOCK))
                .addPage(recipeTransmutation(Blocks.END_STONE))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resStarmetalOre = new ResearchNode(BlocksAS.STARMETAL_ORE, "STARMETAL_ORE", 4.25F, 1.75F)
                .addPage(text("STARMETAL_ORE.1"))
                .addPage(recipeTransmutation(BlocksAS.STARMETAL_ORE))
                .addTomeLookup(ItemsAS.STARMETAL_INGOT, 0, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(BlocksAS.STARMETAL_ORE, 0, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resCuttingTool = new ResearchNode(ItemsAS.CHISEL, "CUTTING_TOOL", 5, 1)
                .addPage(text("CUTTING_TOOL.1"))
                .addPage(recipe(ItemsAS.CHISEL))
                .addTomeLookup(ItemsAS.CHISEL, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("CUTTING_TOOL.3"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resStardust = new ResearchNode(ItemsAS.STARDUST, "STARDUST", 6, 0.5F)
                .addPage(text("STARDUST.1"))
                .addPage(text("STARDUST.2"))
                .addTomeLookup(ItemsAS.STARDUST, 0, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ItemStack structureResonator = ItemResonator.setUpgradeUnlocked(new ItemStack(ItemsAS.RESONATOR), ItemResonator.ResonatorUpgrade.STARLIGHT, ItemResonator.ResonatorUpgrade.AREA_SIZE);
        ItemResonator.setCurrentUpgradeUnsafe(structureResonator, ItemResonator.ResonatorUpgrade.AREA_SIZE);
        ResearchNode resResonatorStructure = new ResearchNode(structureResonator, "RESONATOR_AREA_SIZE", 6.5F, 2)
                .addPage(text("RESONATOR_AREA_SIZE.1"))
                .addPage(recipe(stack -> ItemsAS.RESONATOR.equals(stack.getItem()) && ItemResonator.getCurrentUpgrade(null, stack) == ItemResonator.ResonatorUpgrade.AREA_SIZE))
                .register(ResearchProgression.ATTUNEMENT);

        //TODO celestial gateway
        ResearchNode resCelestialGateway = new ResearchNode(Items.APPLE, "CELESTIAL_GATEWAY", 7.5F, 1.5F)
                .addPage(text("CELESTIAL_GATEWAY.1"))
                //.addPage(recipe(BlocksAS.CELESTIAL_GATEWAY))
                //.addTomeLookup(BlocksAS.CELESTIAL_GATEWAY, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("CELESTIAL_GATEWAY.3"))
                //.addPage(structure(StructureTypesAS.PTYPE_CELESTIAL_GATEWAY))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resAltar3 = new ResearchNode(BlocksAS.ALTAR_CONSTELLATION, "ALTAR3", 7.25F, 0)
                .addPage(text("ALTAR3.1"))
                .addPage(recipe(BlocksAS.ALTAR_CONSTELLATION))
                .addTomeLookup(BlocksAS.ALTAR_CONSTELLATION, 1, ResearchProgression.ATTUNEMENT)
                .addPage(structure(StructureTypesAS.PTYPE_ALTAR_CONSTELLATION))
                .addPage(text("ALTAR3.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resAttunePlayer = new ResearchNode(BlocksAS.ATTUNEMENT_ALTAR, "ATT_PLAYER", 3.75F, 2.75F)
                .addPage(text("ATT_PLAYER.1"))
                .addPage(text("ATT_PLAYER.2"))
                .addPage(recipe(BlocksAS.ATTUNEMENT_ALTAR))
                .addTomeLookup(BlocksAS.ATTUNEMENT_ALTAR, 2, ResearchProgression.ATTUNEMENT)
                .addPage(text("ATT_PLAYER.4"))
                .addPage(structure(StructureTypesAS.PTYPE_ATTUNEMENT_ALTAR))
                .addPage(text("ATT_PLAYER.6"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resPerks = new ResearchNode(BlocksAS.SPECTRAL_RELAY, "ATT_PERKS", 4.5F, 3.5F)
                .addPage(text("ATT_PERKS.1"))
                .addPage(text("ATT_PERKS.2"))
                .addPage(text("ATT_PERKS.3"))
                .addPage(text("ATT_PERKS.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resShiftingStar = new ResearchNode(ItemsAS.SHIFTING_STAR, "SHIFT_STAR", 5.75F, 3.25F)
                .addPage(text("SHIFT_STAR.1"))
                .addPage(recipe(ItemsAS.SHIFTING_STAR))
                .addTomeLookup(ItemsAS.SHIFTING_STAR, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resPerkSeal = new ResearchNode(ItemsAS.PERK_SEAL, "ATT_PERKS_SEAL", 5.5F, 4.5F)
                .addPage(text("ATT_PERKS_SEAL.1"))
                .addPage(recipe(ItemsAS.PERK_SEAL))
                .addTomeLookup(ItemsAS.PERK_SEAL, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resPerkGems = new ResearchNode(new IItemProvider[] {
                ItemsAS.PERK_GEM_DAY,
                ItemsAS.PERK_GEM_NIGHT,
                ItemsAS.PERK_GEM_SKY
        }, "ATT_PERK_GEMS", 4.75F, 5.25F)
                .addPage(text("ATT_PERK_GEMS.1"))
                .addPage(text("ATT_PERK_GEMS.2"))
                .addPage(text("ATT_PERK_GEMS.3"))
                .addTomeLookup(ItemsAS.PERK_GEM_DAY, 1, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.PERK_GEM_NIGHT, 1, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.PERK_GEM_SKY, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resAttuneCrystal = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "ATT_CRYSTAL", 3.5F, 4)
                .addPage(text("ATT_CRYSTAL.1"))
                .addPage(text("ATT_CRYSTAL.2"))
                .addTomeLookup(ItemsAS.ATTUNED_ROCK_CRYSTAL, 0, ResearchProgression.ATTUNEMENT)
                .addTomeLookup(ItemsAS.ATTUNED_CELESTIAL_CRYSTAL, 0, ResearchProgression.CONSTELLATION)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resRitualPedestal = new ResearchNode(BlocksAS.RITUAL_PEDESTAL, "RIT_PEDESTAL", 3, 5)
                .addPage(text("RIT_PEDESTAL.1"))
                .addPage(recipe(BlocksAS.RITUAL_PEDESTAL))
                .addTomeLookup(BlocksAS.RITUAL_PEDESTAL, 1, ResearchProgression.ATTUNEMENT)
                .addPage(structure(StructureTypesAS.PTYPE_RITUAL_PEDESTAL))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resRitualPedestalAcceleration = new ResearchNode(BlocksAS.RITUAL_PEDESTAL, "PED_ACCEL", 1.75F, 5.5F)
                .addPage(text("PED_ACCEL.1"))
                .addPage(text("PED_ACCEL.2"))
                .addPage(text("PED_ACCEL.3"))
                .addPage(text("PED_ACCEL.4"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resAlignmentCharge = new ResearchNode(new SpriteQuery(AssetLoader.TextureLocation.EFFECT, 6, 8, "relay_flare"),
                "QUICK_CHARGE", 0.75F, 4.5F)
                .addPage(text("QUICK_CHARGE.1"))
                .addPage(text("QUICK_CHARGE.2"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resToolChanneling = new ResearchNode(BlocksAS.SPECTRAL_RELAY, "TOOL_CHANNEL", 1.25F, 3.25F)
                .addPage(text("TOOL_CHANNEL.1"))
                .addPage(text("TOOL_CHANNEL.2"))
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resBlinkWand = new ResearchNode(ItemsAS.BLINK_WAND, "TRAVERSAL_WAND", 2.25F, 3.5F)
                .addPage(text("TRAVERSAL_WAND.1"))
                .addPage(recipe(ItemsAS.BLINK_WAND))
                .addTomeLookup(ItemsAS.BLINK_WAND, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resGrappleWand = new ResearchNode(ItemsAS.GRAPPLE_WAND, "GRAPPLE_WAND", 2.5F, 2.5F)
                .addPage(text("GRAPPLE_WAND.1"))
                .addPage(recipe(ItemsAS.GRAPPLE_WAND))
                .addTomeLookup(ItemsAS.GRAPPLE_WAND, 1, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        ResearchNode resToolWands = new ResearchNode(new IItemProvider[] {
                ItemsAS.ARCHITECT_WAND,
                ItemsAS.EXCHANGE_WAND
        }, "TOOL_WANDS", 1.5F, 2.25F)
                .addPage(text("TOOL_WANDS.1"))
                .addPage(recipe(ItemsAS.ARCHITECT_WAND))
                .addTomeLookup(ItemsAS.ARCHITECT_WAND, 1, ResearchProgression.ATTUNEMENT)
                .addPage(text("TOOL_WANDS.3"))
                .addPage(recipe(ItemsAS.EXCHANGE_WAND))
                .addTomeLookup(ItemsAS.EXCHANGE_WAND, 3, ResearchProgression.ATTUNEMENT)
                .register(ResearchProgression.ATTUNEMENT);

        resStarlightNetwork.addSourceConnectionFrom(resLens);
        resStarlightNetwork.addSourceConnectionFrom(resLinkingTool);
        resTransmutationOres.addSourceConnectionFrom(resStarlightNetwork);
        resStarmetalOre.addSourceConnectionFrom(resTransmutationOres);
        resCuttingTool.addSourceConnectionFrom(resStarmetalOre);
        resStardust.addSourceConnectionFrom(resCuttingTool);
        resResonatorStructure.addSourceConnectionFrom(resStardust);
        resCelestialGateway.addSourceConnectionFrom(resStardust);
        resAltar3.addSourceConnectionFrom(resStardust);

        resAttunePlayer.addSourceConnectionFrom(resStarmetalOre);
        resPerks.addSourceConnectionFrom(resAttunePlayer);
        resShiftingStar.addSourceConnectionFrom(resPerks);
        resPerkSeal.addSourceConnectionFrom(resPerks);
        resPerkGems.addSourceConnectionFrom(resPerks);
        resAttuneCrystal.addSourceConnectionFrom(resAttunePlayer);
        resRitualPedestal.addSourceConnectionFrom(resAttuneCrystal);
        resRitualPedestalAcceleration.addSourceConnectionFrom(resRitualPedestal);

        resToolChanneling.addSourceConnectionFrom(resAlignmentCharge);
        resBlinkWand.addSourceConnectionFrom(resToolChanneling);
        resGrappleWand.addSourceConnectionFrom(resToolChanneling);
        resToolWands.addSourceConnectionFrom(resToolChanneling);
    }

    private static void registerCrafting() {
        ResearchNode resHandTelescope = new ResearchNode(ItemsAS.HAND_TELESCOPE, "HAND_TELESCOPE", 0, 1)
                .addPage(text("HAND_TELESCOPE.1"))
                .addPage(recipe(ItemsAS.GLASS_LENS))
                .addTomeLookup(ItemsAS.GLASS_LENS, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.HAND_TELESCOPE))
                .addTomeLookup(ItemsAS.HAND_TELESCOPE, 2, ResearchProgression.BASIC_CRAFT)
                .addPage(text("HAND_TELESCOPE.4"))
                .addPage(text("HAND_TELESCOPE.5"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resIlluminationPowder = new ResearchNode(ItemsAS.ILLUMINATION_POWDER, "ILLUM_POWDER", 1, 2)
                .addPage(text("ILLUM_POWDER.1"))
                .addPage(recipe(ItemsAS.ILLUMINATION_POWDER))
                .addTomeLookup(ItemsAS.ILLUMINATION_POWDER, 1, ResearchProgression.BASIC_CRAFT)
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resNocturnalPowder = new ResearchNode(ItemsAS.NOCTURNAL_POWDER, "NOC_POWDER", 0, 2.5F)
                .addPage(text("NOC_POWDER.1"))
                .addPage(recipe(ItemsAS.NOCTURNAL_POWDER))
                .addTomeLookup(ItemsAS.NOCTURNAL_POWDER, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(text("NOC_POWDER.3"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resIlluminator = new ResearchNode(BlocksAS.ILLUMINATOR, "ILLUMINATOR", 0.75F, 3)
                .addPage(text("ILLUMINATOR.1"))
                .addPage(recipe(BlocksAS.ILLUMINATOR))
                .addTomeLookup(BlocksAS.ILLUMINATOR, 1, ResearchProgression.BASIC_CRAFT)
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resRockCrystals = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "ROCK_CRYSTALS", 2, 1.5F)
                .addPage(text("ROCK_CRYSTALS.1"))
                .addPage(text("ROCK_CRYSTALS.2"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resCrystalGrowth = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "CRYSTAL_GROWTH", 3, 2.5F)
                .addPage(text("CRYSTAL_GROWTH.1"))
                .addPage(text("CRYSTAL_GROWTH.2"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resTools = new ResearchNode(new IItemProvider[] {
                ItemsAS.CRYSTAL_SWORD,
                ItemsAS.CRYSTAL_PICKAXE,
                ItemsAS.CRYSTAL_AXE,
                ItemsAS.CRYSTAL_SHOVEL
        }, "TOOLS", 4.5F, 3)
                .addPage(text("TOOLS.1"))
                .addPage(text("TOOLS.3"))
                .addPage(recipe(ItemsAS.CRYSTAL_SWORD))
                .addTomeLookup(ItemsAS.CRYSTAL_SWORD, 2, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.CRYSTAL_PICKAXE))
                .addTomeLookup(ItemsAS.CRYSTAL_PICKAXE, 3, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.CRYSTAL_AXE))
                .addTomeLookup(ItemsAS.CRYSTAL_AXE, 4, ResearchProgression.BASIC_CRAFT)
                .addPage(recipe(ItemsAS.CRYSTAL_SHOVEL))
                .addTomeLookup(ItemsAS.CRYSTAL_SHOVEL, 5, ResearchProgression.BASIC_CRAFT)
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resLightwell = new ResearchNode(BlocksAS.WELL, "WELL", 3, 1)
                .addPage(text("WELL.1"))
                .addPage(recipe(BlocksAS.WELL))
                .addTomeLookup(BlocksAS.WELL, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(text("WELL.3"))
                .addPage(text("WELL.4"))
                .addPage(text("WELL.5"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resInfusedWood = new ResearchNode(new IItemProvider[] {
                BlocksAS.INFUSED_WOOD,
                BlocksAS.INFUSED_WOOD_ARCH,
                BlocksAS.INFUSED_WOOD_COLUMN,
                BlocksAS.INFUSED_WOOD_ENGRAVED,
                BlocksAS.INFUSED_WOOD_ENRICHED,
                BlocksAS.INFUSED_WOOD_PLANKS,
                BlocksAS.INFUSED_WOOD_STAIRS,
                BlocksAS.INFUSED_WOOD_SLAB
        }, "INFUSED_WOOD", 4, 0)
                .addPage(text("INFUSED_WOOD.1"))
                .addTomeLookup(BlocksAS.INFUSED_WOOD, 0, ResearchProgression.BASIC_CRAFT)
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

        ResearchNode resAltar2 = new ResearchNode(BlocksAS.ALTAR_ATTUNEMENT, "ALTAR2", 4, 1.5F)
                .addPage(text("ALTAR2.1"))
                .addPage(recipe(BlocksAS.ALTAR_ATTUNEMENT))
                .addTomeLookup(BlocksAS.ALTAR_ATTUNEMENT, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(structure(StructureTypesAS.PTYPE_ALTAR_ATTUNEMENT))
                .addPage(text("ALTAR2.4"))
                .register(ResearchProgression.BASIC_CRAFT);

        ItemStack starlightResonator = ItemResonator.setUpgradeUnlocked(new ItemStack(ItemsAS.RESONATOR), ItemResonator.ResonatorUpgrade.STARLIGHT);
        ItemResonator.setCurrentUpgradeUnsafe(starlightResonator, ItemResonator.ResonatorUpgrade.STARLIGHT);
        ResearchNode resResonator = new ResearchNode(starlightResonator, "SKY_RESO", 5, 0.5F)
                .addPage(text("SKY_RESO.1"))
                .addPage(recipe(stack -> ItemsAS.RESONATOR.equals(stack.getItem()) && ItemResonator.getCurrentUpgrade(null, stack) == ItemResonator.ResonatorUpgrade.STARLIGHT))
                .addPage(text("SKY_RESO.2"))
                .addPage(text("SKY_RESO.3"))
                .register(ResearchProgression.BASIC_CRAFT);

        ResearchNode resSpectralRelay = new ResearchNode(BlocksAS.SPECTRAL_RELAY, "SPEC_RELAY", 6, 1)
                .addPage(text("SPEC_RELAY.1"))
                .addPage(recipe(BlocksAS.SPECTRAL_RELAY))
                .addTomeLookup(BlocksAS.SPECTRAL_RELAY, 1, ResearchProgression.BASIC_CRAFT)
                .addPage(structure(StructureTypesAS.PTYPE_SPECTRAL_RELAY))
                .addPage(text("SPEC_RELAY.4"))
                .register(ResearchProgression.BASIC_CRAFT);

        resNocturnalPowder.addSourceConnectionFrom(resIlluminationPowder);
        resIlluminator.addSourceConnectionFrom(resIlluminationPowder);
        resCrystalGrowth.addSourceConnectionFrom(resRockCrystals);
        resTools.addSourceConnectionFrom(resCrystalGrowth);
        resLightwell.addSourceConnectionFrom(resRockCrystals);
        resInfusedWood.addSourceConnectionFrom(resLightwell);
        resAltar2.addSourceConnectionFrom(resLightwell);
        resResonator.addSourceConnectionFrom(resLightwell);
        resSpectralRelay.addSourceConnectionFrom(resResonator);
    }

    private static void registerDiscovery() {
        ResearchNode resWelcome = new ResearchNode(ItemsAS.TOME, "WELCOME", 1, 1)
                .addPage(text("WELCOME.1"))
                .addPage(text("WELCOME.2"))
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resShrines = new ResearchNode(ItemsAS.ROCK_CRYSTAL, "SHRINES", 2, 0)
                .addPage(text("SHRINES.1"))
                .addPage(text("SHRINES.2"))
                .addPage(text("SHRINES.3"))
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resConstellationPaper = new ResearchNode(ItemsAS.CONSTELLATION_PAPER, "CPAPER", 3, 1)
                .addPage(text("CPAPER.1"))
                .addPage(recipe(ItemsAS.TOME))
                .addPage(text("CPAPER.3"))
                .addPage(recipe(ItemsAS.PARCHMENT))
                .addTomeLookup(ItemsAS.CONSTELLATION_PAPER, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(ItemsAS.TOME, 1, ResearchProgression.DISCOVERY)
                .addTomeLookup(ItemsAS.PARCHMENT, 3, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resOres = new ResearchNode(new IItemProvider[] {
                BlocksAS.ROCK_CRYSTAL_ORE,
                BlocksAS.AQUAMARINE_SAND_ORE
        }, "ORES", 2, 2)
                .addPage(text("ORES.1"))
                .addPage(text("ORES.2"))
                .addTomeLookup(ItemsAS.AQUAMARINE, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(ItemsAS.ROCK_CRYSTAL, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(BlocksAS.AQUAMARINE_SAND_ORE, 0, ResearchProgression.DISCOVERY)
                .addTomeLookup(BlocksAS.ROCK_CRYSTAL_ORE, 0, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resResonatingWand = new ResearchNode(ItemsAS.WAND, "WAND", 3, 3)
                .addPage(text("WAND.1"))
                .addPage(recipeVanilla(ItemsAS.WAND))
                .addTomeLookup(ItemsAS.WAND, 1, ResearchProgression.DISCOVERY)
                .addPage(text("WAND.3"))
                .addPage(text("WAND.4"))
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resAltar1 = new ResearchNode(BlocksAS.ALTAR_DISCOVERY, "ALTAR1", 4, 2)
                .addPage(text("ALTAR1.1"))
                .addPage(text("ALTAR1.2"))
                .addPage(recipeTransmutation(BlocksAS.ALTAR_DISCOVERY))
                .addTomeLookup(BlocksAS.ALTAR_DISCOVERY, 1, ResearchProgression.DISCOVERY)
                .addPage(text("ALTAR1.4"))
                .addPage(text("ALTAR1.5"))
                .addPage(text("ALTAR1.6"))
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resMarbles = new ResearchNode(new IItemProvider[] {
                BlocksAS.MARBLE_RAW,
                BlocksAS.MARBLE_PILLAR,
                BlocksAS.MARBLE_ARCH,
                BlocksAS.MARBLE_BRICKS,
                BlocksAS.MARBLE_CHISELED,
                BlocksAS.MARBLE_ENGRAVED,
                BlocksAS.MARBLE_RUNED,
                BlocksAS.MARBLE_SLAB,
                BlocksAS.MARBLE_STAIRS
        }, "MARBLETYPES", 0, 2.5F)
                .addPage(text("MARBLETYPES.1"))
                .addPage(text("MARBLETYPES.2"))
                .addTomeLookup(BlocksAS.MARBLE_RAW, 1, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_PILLAR))
                .addTomeLookup(BlocksAS.MARBLE_PILLAR, 2, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_ARCH))
                .addTomeLookup(BlocksAS.MARBLE_ARCH, 3, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_BRICKS))
                .addTomeLookup(BlocksAS.MARBLE_BRICKS, 4, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_CHISELED))
                .addTomeLookup(BlocksAS.MARBLE_CHISELED, 5, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_ENGRAVED))
                .addTomeLookup(BlocksAS.MARBLE_ENGRAVED, 6, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_RUNED))
                .addTomeLookup(BlocksAS.MARBLE_RUNED, 7, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_SLAB))
                .addTomeLookup(BlocksAS.MARBLE_SLAB, 8, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.MARBLE_STAIRS))
                .addTomeLookup(BlocksAS.MARBLE_STAIRS, 9, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        ResearchNode resSootyMarble = new ResearchNode(new IItemProvider[] {
                BlocksAS.BLACK_MARBLE_RAW,
                BlocksAS.BLACK_MARBLE_PILLAR,
                BlocksAS.BLACK_MARBLE_ARCH,
                BlocksAS.BLACK_MARBLE_BRICKS,
                BlocksAS.BLACK_MARBLE_CHISELED,
                BlocksAS.BLACK_MARBLE_ENGRAVED,
                BlocksAS.BLACK_MARBLE_RUNED,
                BlocksAS.BLACK_MARBLE_SLAB,
                BlocksAS.BLACK_MARBLE_STAIRS
        }, "SOOTYMARBLE", 1, 3)
                .addPage(text("SOOTYMARBLE.1"))
                .addPage(text("SOOTYMARBLE.2"))
                .addPage(recipe(BlocksAS.BLACK_MARBLE_RAW))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_RAW, 2, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_PILLAR))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_PILLAR, 3, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_ARCH))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_ARCH, 4, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_BRICKS))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_BRICKS, 5, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_CHISELED))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_CHISELED, 6, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_ENGRAVED))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_ENGRAVED, 7, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_RUNED))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_RUNED, 8, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_SLAB))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_SLAB, 9, ResearchProgression.DISCOVERY)
                .addPage(recipe(BlocksAS.BLACK_MARBLE_STAIRS))
                .addTomeLookup(BlocksAS.BLACK_MARBLE_STAIRS, 10, ResearchProgression.DISCOVERY)
                .register(ResearchProgression.DISCOVERY);

        resShrines.addSourceConnectionFrom(resWelcome);
        resConstellationPaper.addSourceConnectionFrom(resShrines);
        resMarbles.addSourceConnectionFrom(resWelcome);
        resSootyMarble.addSourceConnectionFrom(resMarbles);
        resOres.addSourceConnectionFrom(resWelcome);
        resResonatingWand.addSourceConnectionFrom(resOres);
        resAltar1.addSourceConnectionFrom(resResonatingWand);
    }

    private static JournalPage recipe(IItemProvider outputItem) {
        return JournalPageRecipe.fromOutputPreferAltarRecipes(stack -> !stack.isEmpty() && stack.getItem().equals(outputItem.asItem()));
    }

    private static JournalPage recipeVanilla(IItemProvider outputItem) {
        return JournalPageRecipe.fromOutputPreferVanillaRecipes(stack -> !stack.isEmpty() && stack.getItem().equals(outputItem.asItem()));
    }

    private static JournalPage recipeTransmutation(IItemProvider outputItem) {
        return JournalPageBlockTransmutation.fromOutput(stack -> !stack.isEmpty() && stack.getItem().equals(outputItem.asItem()));
    }

    private static JournalPage recipeInfusion(IItemProvider outputItem) {
        return JournalPageLiquidInfusion.fromOutput(stack -> !stack.isEmpty() && stack.getItem().equals(outputItem.asItem()));
    }

    private static JournalPage recipe(Predicate<ItemStack> itemStackFilter) {
        return JournalPageRecipe.fromOutputPreferAltarRecipes(itemStackFilter);
    }

    private static JournalPage recipeVanilla(Predicate<ItemStack> itemStackFilter) {
        return JournalPageRecipe.fromOutputPreferVanillaRecipes(itemStackFilter);
    }

    private static JournalPageBlockTransmutation recipeTransmutation(Predicate<ItemStack> itemStackFilter) {
        return JournalPageBlockTransmutation.fromOutput(itemStackFilter);
    }

    private static JournalPageLiquidInfusion recipeInfusion(Predicate<ItemStack> itemStackFilter) {
        return JournalPageLiquidInfusion.fromOutput(itemStackFilter);
    }

    private static JournalPage structure(StructureType structure) {
        return new JournalPageStructure(structure.getStructure());
    }

    private static JournalPage text(String identifier) {
        return new JournalPageText(AstralSorcery.MODID.toLowerCase() + ".journal." + identifier + ".text");
    }
}
