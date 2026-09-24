/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.research;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.component.StoredPlayerProgressComponent;
import hellfirepvp.astralsorcery.common.item.ArtifactItem;
import hellfirepvp.astralsorcery.common.item.LumenCrystalItem;
import hellfirepvp.astralsorcery.common.lib.*;
import hellfirepvp.astralsorcery.common.research.ResearchFlag;
import hellfirepvp.astralsorcery.common.research.ResearchNode;
import hellfirepvp.astralsorcery.common.research.ResearchTier;
import hellfirepvp.astralsorcery.common.research.condition.ConditionResearchFlag;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeBuilder;
import hellfirepvp.astralsorcery.common.research.data.ResearchNodeDataProvider;
import hellfirepvp.astralsorcery.common.research.tome.TomePageRecipe;
import hellfirepvp.astralsorcery.common.research.tome.TomePageStructure;
import hellfirepvp.astralsorcery.common.research.tome.TomePageText;
import hellfirepvp.astralsorcery.datagen.data.GeneratedRecipeBuffer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralResearchNodeProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralResearchNodeProvider extends ResearchNodeDataProvider {

    public AstralResearchNodeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void registerResearchNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar) {
        this.registerShimmerNodes(registryLookup, registrar);
        this.registerIlluminationNodes(registryLookup, registrar);
        this.registerResonanceNodes(registryLookup, registrar);
        this.registerLuminanceNodes(registryLookup, registrar);
        this.registerRadianceNodes(registryLookup, registrar);
    }

    private void registerShimmerNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar) {
        ResearchNode start = ResearchNodeBuilder.create(AstralSorcery.key("welcome"), ResearchTier.SHIMMER, -5, 0)
                .addDisplayItem(ItemsAS.TOME)
                .addPage(new TomePageText(text("welcome", 0)))
                .addPage(new TomePageText(text("welcome", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.TOME))
                .addLookupIndexItem(ItemsAS.TOME)
                .build(registrar);

        ResearchNode marble = ResearchNodeBuilder.create(AstralSorcery.key("marble"), ResearchTier.SHIMMER, -3, -3)
                .addDisplayItem(ItemsAS.BLOCK_MARBLE_ARCH,
                        ItemsAS.BLOCK_MARBLE_BRICKS,
                        ItemsAS.BLOCK_MARBLE_CHISELED,
                        ItemsAS.BLOCK_MARBLE_ENGRAVED,
                        ItemsAS.BLOCK_MARBLE_PILLAR,
                        ItemsAS.BLOCK_MARBLE_RAW,
                        ItemsAS.BLOCK_MARBLE_RUNED)
                .addPage(new TomePageText(text("marble", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_MARBLE_BRICKS))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_MARBLE_ARCH))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_MARBLE_PILLAR))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_MARBLE_CHISELED))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_MARBLE_ENGRAVED))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_MARBLE_RUNED))
                .addLookupIndexItem(ItemsAS.BLOCK_MARBLE_RAW,
                        ItemsAS.BLOCK_MARBLE_BRICKS,
                        ItemsAS.BLOCK_MARBLE_ARCH,
                        ItemsAS.BLOCK_MARBLE_PILLAR,
                        ItemsAS.BLOCK_MARBLE_CHISELED,
                        ItemsAS.BLOCK_MARBLE_ENGRAVED,
                        ItemsAS.BLOCK_MARBLE_RUNED)
                .build(registrar);

        ResearchNode sootyMarble = ResearchNodeBuilder.create(AstralSorcery.key("sooty_marble"), ResearchTier.SHIMMER, -1, -4)
                .addDisplayItem(ItemsAS.BLOCK_SOOTY_MARBLE_ARCH,
                        ItemsAS.BLOCK_SOOTY_MARBLE_BRICKS,
                        ItemsAS.BLOCK_SOOTY_MARBLE_CHISELED,
                        ItemsAS.BLOCK_SOOTY_MARBLE_ENGRAVED,
                        ItemsAS.BLOCK_SOOTY_MARBLE_PILLAR,
                        ItemsAS.BLOCK_SOOTY_MARBLE_RAW,
                        ItemsAS.BLOCK_SOOTY_MARBLE_RUNED)
                .addPage(new TomePageText(text("sooty_marble", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_RAW))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_BRICKS))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_ARCH))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_PILLAR))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_CHISELED))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_ENGRAVED))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.BLOCK_SOOTY_MARBLE_RUNED))
                .addLookupIndexItem(ItemsAS.BLOCK_SOOTY_MARBLE_RAW,
                        ItemsAS.BLOCK_SOOTY_MARBLE_BRICKS,
                        ItemsAS.BLOCK_SOOTY_MARBLE_ARCH,
                        ItemsAS.BLOCK_SOOTY_MARBLE_PILLAR,
                        ItemsAS.BLOCK_SOOTY_MARBLE_CHISELED,
                        ItemsAS.BLOCK_SOOTY_MARBLE_ENGRAVED,
                        ItemsAS.BLOCK_SOOTY_MARBLE_RUNED)
                .build(registrar);

        ResearchNode cstPapers = ResearchNodeBuilder.create(AstralSorcery.key("constellation_paper"), ResearchTier.SHIMMER, -3, 4)
                .addDisplayItem(ItemsAS.CONSTELLATION_PAPER)
                .addPage(new TomePageText(text("constellation_paper", 0)))
                .addPage(new TomePageText(text("constellation_paper", 1)))
                .build(registrar);

        ResearchNode ores = ResearchNodeBuilder.create(AstralSorcery.key("ores"), ResearchTier.SHIMMER, -1, 1)
                .addDisplayItem(ItemsAS.BLOCK_AQUAMARINE_SHALE_ORE,
                        ItemsAS.BLOCK_ROCK_CRYSTAL_ORE)
                .addPage(new TomePageText(text("ores", 0)))
                .addPage(new TomePageText(text("ores", 1)))
                .addLookupIndexItem(ItemsAS.AQUAMARINE, ItemsAS.ROCK_CRYSTAL)
                .build(registrar);

        ResearchNode astrolabe = ResearchNodeBuilder.create(AstralSorcery.key("astrolabe"), ResearchTier.SHIMMER, 2, 0)
                .addDisplayItem(ItemsAS.ASTROLABE)
                .addPage(new TomePageText(text("astrolabe", 0)))
                .addPage(new TomePageText(text("astrolabe", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeType.CRAFTING, ItemsAS.ASTROLABE))
                .addPage(new TomePageText(text("astrolabe", 2)))
                .addLookupIndexItem(ItemsAS.ASTROLABE)
                .build(registrar);

        ResearchNode wand = ResearchNodeBuilder.create(AstralSorcery.key("wand"), ResearchTier.SHIMMER, 5, 1)
                .addDisplayItem(ItemsAS.WAND)
                .addPage(new TomePageText(text("wand", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.FOCAL_COMBINE_TYPE, ItemsAS.WAND))
                .addPage(new TomePageText(text("wand", 1)))
                .addLookupIndexItem(ItemsAS.WAND)
                .build(registrar);

        ResearchNode illumination_altar = ResearchNodeBuilder.create(AstralSorcery.key("illumination_altar"), ResearchTier.SHIMMER, 4, -2)
                .addDisplayItem(ItemsAS.BLOCK_ALTAR_ILLUMINATION)
                .addPage(new TomePageText(text("illumination_altar", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.FOCAL_COMBINE_TYPE, ItemsAS.BLOCK_ALTAR_ILLUMINATION))
                .addPage(new TomePageText(text("illumination_altar", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_ALTAR_ILLUMINATION)
                .build(registrar);

        marble.addConnection(sootyMarble);
        start.addConnection(marble);
        start.addConnection(cstPapers);
        start.addConnection(ores);
        ores.addConnection(astrolabe);
        astrolabe.addConnection(wand);
        astrolabe.addConnection(illumination_altar);
    }

    private void registerIlluminationNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar) {
        ResearchNode lightwell = ResearchNodeBuilder.create(AstralSorcery.key("lightwell"), ResearchTier.ILLUMINATION, -6, -3)
                .addDisplayItem(ItemsAS.BLOCK_LIGHTWELL)
                .addPage(new TomePageText(text("lightwell", 0)))
                .addPage(new TomePageText(text("lightwell", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_LIGHTWELL))
                .addLookupIndexItem(ItemsAS.BLOCK_LIGHTWELL)
                .build(registrar);

        ResearchNode liquidStarlight = ResearchNodeBuilder.create(AstralSorcery.key("liquid_starlight"), ResearchTier.ILLUMINATION, -3, -1)
                .addDisplayItem(FluidsAS.LIQUID_STARLIGHT.getBucket())
                .addPage(new TomePageText(text("liquid_starlight", 0)))
                .addPage(new TomePageText(text("liquid_starlight", 1)))
                .addPage(new TomePageText(text("liquid_starlight", 2)))
                .addLookupIndexItem(FluidsAS.LIQUID_STARLIGHT.getBucket())
                .build(registrar);

        ResearchNode crystalTools = ResearchNodeBuilder.create(AstralSorcery.key("crystal_tools"), ResearchTier.ILLUMINATION, -5, 0)
                .addDisplayItem(ItemsAS.CRYSTAL_PICKAXE,
                        ItemsAS.CRYSTAL_AXE,
                        ItemsAS.CRYSTAL_SHOVEL,
                        ItemsAS.CRYSTAL_SWORD)
                .addPage(new TomePageText(text("crystal_tools", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.CRYSTAL_PICKAXE))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.CRYSTAL_AXE))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.CRYSTAL_SHOVEL))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.CRYSTAL_SWORD))
                .addLookupIndexItem(ItemsAS.CRYSTAL_PICKAXE,
                        ItemsAS.CRYSTAL_AXE,
                        ItemsAS.CRYSTAL_SHOVEL,
                        ItemsAS.CRYSTAL_SWORD)
                .build(registrar);

        ResearchNode infusedWood = ResearchNodeBuilder.create(AstralSorcery.key("infused_wood"), ResearchTier.ILLUMINATION, -4, 2)
                .addDisplayItem(ItemsAS.BLOCK_INFUSED_WOOD_RAW,
                        ItemsAS.BLOCK_INFUSED_WOOD_ARCH,
                        ItemsAS.BLOCK_INFUSED_WOOD_COLUMN,
                        ItemsAS.BLOCK_INFUSED_WOOD_ENGRAVED,
                        ItemsAS.BLOCK_INFUSED_WOOD_ENRICHED,
                        ItemsAS.BLOCK_INFUSED_WOOD_PLANKS)
                .addPage(new TomePageText(text("infused_wood", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_INFUSED_WOOD_PLANKS))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_INFUSED_WOOD_ARCH))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_INFUSED_WOOD_COLUMN))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_INFUSED_WOOD_ENGRAVED))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_INFUSED_WOOD_ENRICHED))
                .addLookupIndexItem(ItemsAS.BLOCK_INFUSED_WOOD_RAW,
                        ItemsAS.BLOCK_INFUSED_WOOD_ARCH,
                        ItemsAS.BLOCK_INFUSED_WOOD_COLUMN,
                        ItemsAS.BLOCK_INFUSED_WOOD_ENGRAVED,
                        ItemsAS.BLOCK_INFUSED_WOOD_ENRICHED,
                        ItemsAS.BLOCK_INFUSED_WOOD_PLANKS)
                .build(registrar);

        ResearchNode throwableDusts = ResearchNodeBuilder.create(AstralSorcery.key("throwable_dusts"), ResearchTier.ILLUMINATION, -1, 1)
                .addDisplayItem(ItemsAS.ILLUMINATION_POWDER,
                        ItemsAS.NOCTURNAL_POWDER,
                        ItemsAS.VIVID_POWDER)
                .addPage(new TomePageText(text("throwable_dusts", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.ILLUMINATION_POWDER))
                .addPage(new TomePageText(text("throwable_dusts", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.NOCTURNAL_POWDER))
                .addPage(new TomePageText(text("throwable_dusts", 2)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.VIVID_POWDER))
                .addLookupIndexItem(ItemsAS.ILLUMINATION_POWDER,
                        ItemsAS.NOCTURNAL_POWDER,
                        ItemsAS.VIVID_POWDER)
                .build(registrar);

        ResearchNode caveIlluminator = ResearchNodeBuilder.create(AstralSorcery.key("cave_illuminator"), ResearchTier.ILLUMINATION, -2, 3)
                .addDisplayItem(ItemsAS.BLOCK_CAVE_ILLUMINATOR)
                .addPage(new TomePageText(text("cave_illuminator", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_CAVE_ILLUMINATOR))
                .addLookupIndexItem(ItemsAS.BLOCK_CAVE_ILLUMINATOR)
                .build(registrar);

        ResearchNode starmetal = ResearchNodeBuilder.create(AstralSorcery.key("starmetal"), ResearchTier.ILLUMINATION, -4, -5)
                .addDisplayItem(ItemsAS.STARMETAL_INGOT)
                .addPage(new TomePageText(text("starmetal", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.FOCAL_TRANSMUTATION_TYPE, ItemsAS.BLOCK_STARMETAL_ORE))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.FOCAL_COMBINE_TYPE, ItemsAS.RAW_STARMETAL))
                .addPage(new TomePageText(text("starmetal", 1)))
                .addLookupIndexItem(ItemsAS.STARMETAL_INGOT, ItemsAS.RAW_STARMETAL, ItemsAS.BLOCK_STARMETAL_ORE)
                .build(registrar);

        ResearchNode chisel = ResearchNodeBuilder.create(AstralSorcery.key("chisel"), ResearchTier.ILLUMINATION, -1, -3)
                .addDisplayItem(ItemsAS.CHISEL)
                .addPage(new TomePageText(text("chisel", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.CHISEL))
                .addPage(new TomePageText(text("chisel", 1)))
                .addLookupIndexItem(ItemsAS.CHISEL, ItemsAS.STARDUST)
                .build(registrar);

        ResearchNode celestialCrystals = ResearchNodeBuilder.create(AstralSorcery.key("celestial_crystals"), ResearchTier.ILLUMINATION, 1, -2)
                .addDisplayItem(ItemsAS.CELESTIAL_CRYSTAL)
                .addPage(new TomePageText(text("celestial_crystals", 0)))
                .addPage(new TomePageText(text("celestial_crystals", 1)))
                .addLookupIndexItem(ItemsAS.CELESTIAL_CRYSTAL, ItemsAS.BLOCK_CELESTIAL_CRYSTAL_CLUSTER)
                .build(registrar);

        ItemStack knowledgeShare = ItemsAS.KNOWLEDGE_SHARE.toStack();
        knowledgeShare.set(DataComponentsAS.STORED_PLAYER_PROGRESS, StoredPlayerProgressComponent.CREATIVE);
        ResearchNode knowledgeSharingScroll = ResearchNodeBuilder.create(AstralSorcery.key("knowledge_sharing_scroll"), ResearchTier.ILLUMINATION, 0, -5)
                .addDisplayItem(knowledgeShare)
                .addPage(new TomePageText(text("knowledge_sharing_scroll", 0)))
                .addPage(new TomePageText(text("knowledge_sharing_scroll", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.KNOWLEDGE_SHARE))
                .addLookupIndexItem(ItemsAS.KNOWLEDGE_SHARE)
                .build(registrar);

        ResearchNode focusRelay = ResearchNodeBuilder.create(AstralSorcery.key("focus_relay"), ResearchTier.ILLUMINATION, 2, -4)
                .addDisplayItem(ItemsAS.BLOCK_FOCUS_RELAY)
                .addPage(new TomePageText(text("focus_relay", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_FOCUS_RELAY))
                .addPage(new TomePageText(text("focus_relay", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.GLASS_LENS))
                .addLookupIndexItem(ItemsAS.BLOCK_FOCUS_RELAY, ItemsAS.GLASS_LENS)
                .build(registrar);

        ResearchNode resonanceAltar = ResearchNodeBuilder.create(AstralSorcery.key("resonance_altar"), ResearchTier.ILLUMINATION, 4, -3)
                .addDisplayItem(ItemsAS.BLOCK_ALTAR_RESONANCE)
                .addPage(new TomePageText(text("resonance_altar", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_ALTAR_RESONANCE))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_ALTAR_T2, 0))
                .addPage(new TomePageText(text("resonance_altar", 1)))
                .addPage(new TomePageText(text("resonance_altar", 2)))
                .addLookupIndexItem(ItemsAS.BLOCK_ALTAR_RESONANCE)
                .build(registrar);

        ResearchNode shootingStar = ResearchNodeBuilder.create(AstralSorcery.key("shooting_star"), ResearchTier.ILLUMINATION, -8, -1)
                .addDisplayItem(Items.SPYGLASS)
                .addPage(new TomePageText(text("shooting_star", 0)))
                .addPage(new TomePageText(text("shooting_star", 1)))
                .build(registrar);

        List<ItemStack> artifactItems = new ArrayList<>();
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.forEach(type -> {
            artifactItems.add(ArtifactItem.createForDisplay(type));
        });
        ResearchNode artifacts = ResearchNodeBuilder.create(AstralSorcery.key("artifacts"), ResearchTier.ILLUMINATION, -7, 1)
                .addDisplayItem(artifactItems)
                .addPage(new TomePageText(text("artifacts", 0)))
                .addPage(new TomePageText(text("artifacts", 1)))
                .addPage(new TomePageText(text("artifacts", 2)))
                .addCondition(new ConditionResearchFlag(ResearchFlag.HAS_OBTAINED_ARTIFACT))
                .addPage(TomePageRecipe.of(RecipeTypesAS.ALTAR_CRAFTING_TYPE.holder(), AstralSorcery.key("altar/akashic_singularity_with_generated_artifact_loot")))
                .addLookupIndexItem(ItemsAS.ARTIFACT, ItemsAS.ARTIFACT_SHARD)
                .build(registrar);

        ResearchNode formationWand = ResearchNodeBuilder.create(AstralSorcery.key("formation_wand"), ResearchTier.ILLUMINATION, -2, -6)
                .addDisplayItem(ItemsAS.ARCHITECT_WAND)
                .addPage(new TomePageText(text("formation_wand", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.ARCHITECT_WAND))
                .addPage(new TomePageText(text("formation_wand", 1)))
                .addLookupIndexItem(ItemsAS.ARCHITECT_WAND)
                .build(registrar);

        ResearchNode exchangeWand = ResearchNodeBuilder.create(AstralSorcery.key("exchange_wand"), ResearchTier.ILLUMINATION, 0, -7)
                .addDisplayItem(ItemsAS.EXCHANGE_WAND)
                .addPage(new TomePageText(text("exchange_wand", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.EXCHANGE_WAND))
                .addPage(new TomePageText(text("exchange_wand", 1)))
                .addLookupIndexItem(ItemsAS.EXCHANGE_WAND)
                .build(registrar);

        lightwell.addConnection(liquidStarlight);
        liquidStarlight.addConnection(chisel);
        liquidStarlight.addConnection(infusedWood);
        liquidStarlight.addConnection(throwableDusts);
        throwableDusts.addConnection(caveIlluminator);
        starmetal.addConnection(chisel);
        chisel.addConnection(focusRelay);
        chisel.addConnection(celestialCrystals);
        focusRelay.addConnection(resonanceAltar);
        shootingStar.addConnection(artifacts);
    }

    private void registerResonanceNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar) {
        ResearchNode starlightInfuser = ResearchNodeBuilder.create(AstralSorcery.key("starlight_infuser"), ResearchTier.RESONANCE, -3, 0)
                .addDisplayItem(ItemsAS.BLOCK_INFUSER)
                .addPage(new TomePageText(text("starlight_infuser", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_INFUSER))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_INFUSER))
                .addPage(new TomePageText(text("starlight_infuser", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.INFUSION_TYPE, ItemsAS.GLASS_LENS))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.INFUSION_TYPE, ItemsAS.BLOCK_INFUSED_WOOD_INFUSED))
                .addLookupIndexItem(ItemsAS.BLOCK_INFUSER, ItemsAS.RESONATING_GEM)
                .build(registrar);

        ResearchNode treeBeacon = ResearchNodeBuilder.create(AstralSorcery.key("tree_beacon"), ResearchTier.RESONANCE, -4, -3)
                .addDisplayItem(ItemsAS.BLOCK_TREE_BEACON)
                .addPage(new TomePageText(text("tree_beacon", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_TREE_BEACON))
                .addPage(new TomePageText(text("tree_beacon", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_TREE_BEACON)
                .build(registrar);

        ResearchNode enhancedResonanceStructure = ResearchNodeBuilder.create(AstralSorcery.key("enhanced_resonance_structure"), ResearchTier.RESONANCE, 0, -2)
                .addDisplayItem(ItemsAS.BLOCK_ALTAR_RESONANCE)
                .addPage(new TomePageText(text("enhanced_resonance_structure", 0)))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_ALTAR_T2, 1))
                .build(registrar);

        ResearchNode containmentChalice = ResearchNodeBuilder.create(AstralSorcery.key("containment_chalice"), ResearchTier.RESONANCE, 2, -1)
                .addDisplayItem(ItemsAS.BLOCK_CHALICE)
                .addPage(new TomePageText(text("containment_chalice", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_CHALICE))
                .addPage(new TomePageText(text("containment_chalice", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_CHALICE)
                .build(registrar);

        ResearchNode celestialGateway = ResearchNodeBuilder.create(AstralSorcery.key("celestial_gateway"), ResearchTier.RESONANCE, 4, -2)
                .addDisplayItem(ItemsAS.BLOCK_CELESTIAL_GATEWAY)
                .addPage(new TomePageText(text("celestial_gateway", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_CELESTIAL_GATEWAY))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_CELESTIAL_GATEWAY))
                .addPage(new TomePageText(text("celestial_gateway", 1)))
                .addPage(new TomePageText(text("celestial_gateway", 2)))
                .addLookupIndexItem(ItemsAS.BLOCK_CELESTIAL_GATEWAY)
                .build(registrar);

        ResearchNode luminousAltar = ResearchNodeBuilder.create(AstralSorcery.key("luminous_altar"), ResearchTier.RESONANCE, 4, 1)
                .addDisplayItem(ItemsAS.BLOCK_ALTAR_LUMINANCE)
                .addPage(new TomePageText(text("luminous_altar", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_ALTAR_LUMINANCE))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_ALTAR_T3))
                .addPage(new TomePageText(text("luminous_altar", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_ALTAR_LUMINANCE)
                .build(registrar);

        ResearchNode blinkWand = ResearchNodeBuilder.create(AstralSorcery.key("blink_wand"), ResearchTier.RESONANCE, 2, -3)
                .addDisplayItem(ItemsAS.BLINK_WAND)
                .addPage(new TomePageText(text("blink_wand", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLINK_WAND))
                .addPage(new TomePageText(text("blink_wand", 1)))
                .addLookupIndexItem(ItemsAS.BLINK_WAND)
                .build(registrar);

        ResearchNode impulsionWand = ResearchNodeBuilder.create(AstralSorcery.key("impulsion_wand"), ResearchTier.RESONANCE, 3, -5)
                .addDisplayItem(ItemsAS.GRAPPLING_WAND)
                .addPage(new TomePageText(text("impulsion_wand", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.GRAPPLING_WAND))
                .addPage(new TomePageText(text("impulsion_wand", 1)))
                .addLookupIndexItem(ItemsAS.GRAPPLING_WAND)
                .build(registrar);

        ResearchNode constellationFlowers = ResearchNodeBuilder.create(AstralSorcery.key("constellation_flowers"), ResearchTier.RESONANCE, -1, -4)
                .addDisplayItem(ItemsAS.BLOCK_HYACINTH,
                        ItemsAS.BLOCK_IRIS,
                        ItemsAS.BLOCK_ORCHID,
                        ItemsAS.BLOCK_PROTEA,
                        ItemsAS.BLOCK_THISTLE)
                .addPage(new TomePageText(text("constellation_flowers", 0)))
                .addPage(new TomePageText(text("constellation_flowers", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_HYACINTH,
                        ItemsAS.BLOCK_IRIS,
                        ItemsAS.BLOCK_ORCHID,
                        ItemsAS.BLOCK_PROTEA,
                        ItemsAS.BLOCK_THISTLE)
                .build(registrar);

        ResearchNode lumenArray = ResearchNodeBuilder.create(AstralSorcery.key("lumen_array"), ResearchTier.RESONANCE, 0, -6)
                .addDisplayItem(ItemsAS.BLOCK_LUMEN_ARRAY)
                .addPage(new TomePageText(text("lumen_array", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_LUMEN_ARRAY))
                .addPage(new TomePageText(text("lumen_array", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_LUMEN_ARRAY)
                .build(registrar);

        ResearchNode lumenFilament = ResearchNodeBuilder.create(AstralSorcery.key("lumen_filament"), ResearchTier.RESONANCE, 2, -7)
                .addDisplayItem(ItemsAS.BLOCK_LUMEN_FILAMENT)
                .addPage(new TomePageText(text("lumen_filament", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_LUMEN_FILAMENT))
                .addLookupIndexItem(ItemsAS.BLOCK_LUMEN_FILAMENT)
                .build(registrar);

        ResearchNode lumenCrystallizer = ResearchNodeBuilder.create(AstralSorcery.key("lumen_crystallizer"), ResearchTier.RESONANCE, -1, -8)
                .addDisplayItem(ItemsAS.BLOCK_LUMEN_CRYSTALLIZER)
                .addPage(new TomePageText(text("lumen_crystallizer", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_LUMEN_CRYSTALLIZER))
                .addPage(new TomePageText(text("lumen_crystallizer", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_LUMEN_CRYSTALLIZER, ItemsAS.LUMEN_CRYSTAL, ItemsAS.BLOCK_LUMEN_CRYSTAL_CLUSTER)
                .build(registrar);

        List<ItemStack> lumenCrystals = new ArrayList<>();
        lumenCrystals.add(LumenCrystalItem.getCrystal(LumenAS.AEVITAS));
        lumenCrystals.add(LumenCrystalItem.getCrystal(LumenAS.ARMARA));
        lumenCrystals.add(LumenCrystalItem.getCrystal(LumenAS.DISCIDIA));
        lumenCrystals.add(LumenCrystalItem.getCrystal(LumenAS.EVORSIO));
        lumenCrystals.add(LumenCrystalItem.getCrystal(LumenAS.VICIO));
        ResearchNode lumenBinding = ResearchNodeBuilder.create(AstralSorcery.key("lumen_binding"), ResearchTier.RESONANCE, 1, -9)
                .addDisplayItem(lumenCrystals)
                .addPage(new TomePageText(text("lumen_binding", 0)))
                .addPage(new TomePageText(text("lumen_binding", 1)))
                .addPage(new TomePageText(text("lumen_binding", 2)))
                .build(registrar);

        ResearchNode attunementAltar = ResearchNodeBuilder.create(AstralSorcery.key("attunement_altar"), ResearchTier.RESONANCE, -1, 1)
                .addDisplayItem(ItemsAS.BLOCK_ATTUNEMENT_ALTAR)
                .addPage(new TomePageText(text("attunement_altar", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_ATTUNEMENT_ALTAR))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_ATTUNEMENT_ALTAR))
                .addPage(new TomePageText(text("attunement_altar", 1)))
                .addPage(new TomePageText(text("attunement_altar", 2)))
                .addLookupIndexItem(ItemsAS.BLOCK_ATTUNEMENT_ALTAR)
                .build(registrar);

        ResearchNode perkGems = ResearchNodeBuilder.create(AstralSorcery.key("perk_gems"), ResearchTier.RESONANCE, -2, 3)
                .addDisplayItem(ItemsAS.DYNAMISM_GEM_DAY,
                        ItemsAS.DYNAMISM_GEM_SKY,
                        ItemsAS.DYNAMISM_GEM_NIGHT)
                .addPage(new TomePageText(text("perk_gems", 0)))
                .addPage(new TomePageText(text("perk_gems", 1)))
                .addPage(new TomePageText(text("perk_gems", 2)))
                .addLookupIndexItem(ItemsAS.DYNAMISM_GEM_DAY,
                        ItemsAS.DYNAMISM_GEM_SKY,
                        ItemsAS.DYNAMISM_GEM_NIGHT)
                .build(registrar);

        ResearchNode shiftingStar = ResearchNodeBuilder.create(AstralSorcery.key("shifting_star"), ResearchTier.RESONANCE, 1, 2)
                .addDisplayItem(ItemsAS.SHIFTING_STAR)
                .addPage(new TomePageText(text("shifting_star", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.SHIFTING_STAR))
                .addLookupIndexItem(ItemsAS.SHIFTING_STAR)
                .build(registrar);

        ResearchNode perkTools = ResearchNodeBuilder.create(AstralSorcery.key("perk_tools"), ResearchTier.RESONANCE, 0, 4)
                .addDisplayItem(ItemsAS.PERK_SEAL,
                        ItemsAS.PERK_NULLIFIER)
                .addPage(new TomePageText(text("perk_tools", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.PERK_SEAL))
                .addPage(new TomePageText(text("perk_tools", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.PERK_NULLIFIER))
                .addLookupIndexItem(ItemsAS.PERK_SEAL, ItemsAS.PERK_NULLIFIER)
                .build(registrar);

        starlightInfuser.addConnection(enhancedResonanceStructure);
        enhancedResonanceStructure.addConnection(containmentChalice);
        enhancedResonanceStructure.addConnection(constellationFlowers);
        constellationFlowers.addConnection(lumenArray);
        lumenArray.addConnection(lumenFilament);
        lumenArray.addConnection(lumenCrystallizer);
        lumenCrystallizer.addConnection(lumenBinding);
        enhancedResonanceStructure.addConnection(attunementAltar);
        attunementAltar.addConnection(perkGems);
        attunementAltar.addConnection(perkTools);
        attunementAltar.addConnection(shiftingStar);
        containmentChalice.addConnection(luminousAltar);
    }

    private void registerLuminanceNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar) {
        ResearchNode starlightFocusCrystal = ResearchNodeBuilder.create(AstralSorcery.key("starlight_focus_crystal"), ResearchTier.LUMINANCE, -3, 1)
                .addDisplayItem(ItemsAS.BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL,
                        ItemsAS.BLOCK_STARLIGHT_FOCUS_CELESTIAL_CRYSTAL)
                .addPage(new TomePageText(text("starlight_focus_crystal", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL))
                .addPage(new TomePageText(text("starlight_focus_crystal", 1)))
                .addPage(new TomePageText(text("starlight_focus_crystal", 2)))
                .addLookupIndexItem(ItemsAS.BLOCK_STARLIGHT_FOCUS_ROCK_CRYSTAL, ItemsAS.BLOCK_STARLIGHT_FOCUS_CELESTIAL_CRYSTAL)
                .build(registrar);

        ResearchNode lensPrism = ResearchNodeBuilder.create(AstralSorcery.key("lens_prism"), ResearchTier.LUMINANCE, -5, 0)
                .addDisplayItem(ItemsAS.BLOCK_LENS,
                        ItemsAS.BLOCK_PRISM)
                .addPage(new TomePageText(text("lens_prism", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_LENS))
                .addPage(new TomePageText(text("lens_prism", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_PRISM))
                .addLookupIndexItem(ItemsAS.BLOCK_LENS, ItemsAS.BLOCK_PRISM)
                .build(registrar);

        ResearchNode linkingTool = ResearchNodeBuilder.create(AstralSorcery.key("linking_tool"), ResearchTier.LUMINANCE, -2, -1)
                .addDisplayItem(ItemsAS.LINKING_TOOL)
                .addPage(new TomePageText(text("linking_tool", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.LINKING_TOOL))
                .addPage(new TomePageText(text("linking_tool", 1)))
                .addLookupIndexItem(ItemsAS.LINKING_TOOL)
                .build(registrar);

        ResearchNode radianceAltar = ResearchNodeBuilder.create(AstralSorcery.key("radiance_altar"), ResearchTier.LUMINANCE, -1, 3)
                .addDisplayItem(ItemsAS.BLOCK_ALTAR_RADIANCE)
                .addPage(new TomePageText(text("radiance_altar", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_ALTAR_RADIANCE))
                .addPage(TomePageStructure.of(ObserversAS.STRUCTURE_ALTAR_T4))
                .addPage(new TomePageText(text("radiance_altar", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_ALTAR_RADIANCE)
                .build(registrar);

        ResearchNode lumenAlchemyArray = ResearchNodeBuilder.create(AstralSorcery.key("lumen_alchemy_array"), ResearchTier.LUMINANCE, -3, -3)
                .addDisplayItem(ItemsAS.BLOCK_LUMEN_ALCHEMY_ARRAY)
                .addPage(new TomePageText(text("lumen_alchemy_array", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_LUMEN_ALCHEMY_ARRAY))
                .addPage(new TomePageText(text("lumen_alchemy_array", 1)))
                .addLookupIndexItem(ItemsAS.BLOCK_LUMEN_ALCHEMY_ARRAY)
                .build(registrar);

        List<ItemStack> lumenComplexCrystals = new ArrayList<>();
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.VIREL));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.SOLYN));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.NULLAE));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.CALDOR));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.HYLE));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.DYNAMIS));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.AION));
        lumenComplexCrystals.add(LumenCrystalItem.getCrystal(LumenAS.AKASHA));
        ResearchNode complexLumen = ResearchNodeBuilder.create(AstralSorcery.key("complex_lumen"), ResearchTier.LUMINANCE, -2, -5)
                .addDisplayItem(lumenComplexCrystals)
                .addPage(new TomePageText(text("complex_lumen", 0)))
                .addPage(new TomePageText(text("complex_lumen", 1)))
                .addPage(new TomePageText(text("complex_lumen", 2)))
                .build(registrar);

        ResearchNode illuminationWand = ResearchNodeBuilder.create(AstralSorcery.key("illumination_wand"), ResearchTier.LUMINANCE, 0, -3)
                .addDisplayItem(ItemsAS.ILLUMINATION_WAND)
                .addPage(new TomePageText(text("illumination_wand", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.ILLUMINATION_WAND))
                .addPage(new TomePageText(text("illumination_wand", 1)))
                .addLookupIndexItem(ItemsAS.ILLUMINATION_WAND)
                .build(registrar);

        ResearchNode iridescentTools = ResearchNodeBuilder.create(AstralSorcery.key("iridescent_tools"), ResearchTier.LUMINANCE, 1, -1)
                .addDisplayItem(ItemsAS.IRIDESCENT_CRYSTAL_SWORD,
                        ItemsAS.IRIDESCENT_CRYSTAL_PICKAXE,
                        ItemsAS.IRIDESCENT_CRYSTAL_AXE,
                        ItemsAS.IRIDESCENT_CRYSTAL_SHOVEL)
                .addPage(new TomePageText(text("iridescent_tools", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.IRIDESCENT_CRYSTAL_PICKAXE))
                .addPage(new TomePageText(text("iridescent_tools", 1)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.IRIDESCENT_CRYSTAL_AXE))
                .addPage(new TomePageText(text("iridescent_tools", 2)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.IRIDESCENT_CRYSTAL_SHOVEL))
                .addPage(new TomePageText(text("iridescent_tools", 3)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.IRIDESCENT_CRYSTAL_SWORD))
                .addPage(new TomePageText(text("iridescent_tools", 4)))
                .addLookupIndexItem(ItemsAS.IRIDESCENT_CRYSTAL_SWORD,
                        ItemsAS.IRIDESCENT_CRYSTAL_PICKAXE,
                        ItemsAS.IRIDESCENT_CRYSTAL_AXE,
                        ItemsAS.IRIDESCENT_CRYSTAL_SHOVEL)
                .build(registrar);

        ResearchNode resplendentPrism = ResearchNodeBuilder.create(AstralSorcery.key("resplendent_prism"), ResearchTier.LUMINANCE, 3, -2)
                .addDisplayItem(ItemsAS.ENCHANTMENT_AMULET)
                .addPage(new TomePageText(text("resplendent_prism", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.ENCHANTMENT_AMULET))
                .addPage(new TomePageText(text("resplendent_prism", 1)))
                .addPage(TomePageRecipe.of(RecipeTypesAS.ALTAR_CRAFTING_TYPE.holder(), AstralSorcery.key("altar/enchantment_amulet_reroll")))
                .addLookupIndexItem(ItemsAS.ENCHANTMENT_AMULET)
                .build(registrar);

        ResearchNode attunedShiftingStars = ResearchNodeBuilder.create(AstralSorcery.key("attuned_shifting_stars"), ResearchTier.LUMINANCE, 2, 2)
                .addDisplayItem(ItemsAS.SHIFTING_STAR_AEVITAS,
                        ItemsAS.SHIFTING_STAR_ARMARA,
                        ItemsAS.SHIFTING_STAR_DISCIDIA,
                        ItemsAS.SHIFTING_STAR_EVORSIO,
                        ItemsAS.SHIFTING_STAR_VICIO)
                .addPage(new TomePageText(text("attuned_shifting_stars", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.SHIFTING_STAR_AEVITAS))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.SHIFTING_STAR_ARMARA))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.SHIFTING_STAR_DISCIDIA))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.SHIFTING_STAR_EVORSIO))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.SHIFTING_STAR_VICIO))
                .addLookupIndexItem(ItemsAS.SHIFTING_STAR_AEVITAS,
                        ItemsAS.SHIFTING_STAR_ARMARA,
                        ItemsAS.SHIFTING_STAR_DISCIDIA,
                        ItemsAS.SHIFTING_STAR_EVORSIO,
                        ItemsAS.SHIFTING_STAR_VICIO)
                .build(registrar);

        starlightFocusCrystal.addConnection(lensPrism);
        starlightFocusCrystal.addConnection(linkingTool);
        starlightFocusCrystal.addConnection(radianceAltar);
        lumenAlchemyArray.addConnection(complexLumen);
    }

    private void registerRadianceNodes(HolderLookup.Provider registryLookup, Consumer<ResearchNode> registrar) {
        ResearchNode stellarFilament = ResearchNodeBuilder.create(AstralSorcery.key("stellar_filament"), ResearchTier.RADIANCE, -1, 0)
                .addDisplayItem(ItemsAS.BLOCK_STELLAR_FILAMENT)
                .addPage(new TomePageText(text("stellar_filament", 0)))
                .addPage(GeneratedRecipeBuffer.findRecipe(RecipeTypesAS.ALTAR_CRAFTING_TYPE, ItemsAS.BLOCK_STELLAR_FILAMENT))
                .addPage(new TomePageText(text("stellar_filament", 1)))
                .addPage(new TomePageText(text("stellar_filament", 2)))
                .addLookupIndexItem(ItemsAS.BLOCK_STELLAR_FILAMENT)
                .build(registrar);

        List<ItemStack> artifacts = new ArrayList<>();
        RegistriesAS.REGISTRY_ARTIFACT_TYPES.forEach(type -> {
            artifacts.add(ArtifactItem.createForDisplay(type));
        });
        ResearchNode artifactEnhancements = ResearchNodeBuilder.create(AstralSorcery.key("artifact_enhancements"), ResearchTier.RADIANCE, 1, 1)
                .addDisplayItem(artifacts)
                .addPage(new TomePageText(text("artifact_enhancements", 0)))
                .addPage(new TomePageText(text("artifact_enhancements", 1)))
                .addCondition(new ConditionResearchFlag(ResearchFlag.HAS_OBTAINED_ARTIFACT))
                .addPage(TomePageRecipe.of(RecipeTypesAS.ALTAR_CRAFTING_TYPE.holder(), AstralSorcery.key("altar/enchantment_amulet_artifact_enhance")))
                .addPage(TomePageRecipe.of(RecipeTypesAS.ALTAR_CRAFTING_TYPE.holder(), AstralSorcery.key("altar/enchanted_item_artifact_enhance")))
                .addPage(TomePageRecipe.of(RecipeTypesAS.ALTAR_CRAFTING_TYPE.holder(), AstralSorcery.key("altar/dynamism_gem_artifact_enhance")))
                .build(registrar);
    }

    private static String text(String key, int intendedPage) {
        return String.format("tome.research.node.%s.%s.text.%s", AstralSorcery.MODID, key, intendedPage);
    }
}
