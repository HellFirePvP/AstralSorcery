/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.world;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.worldgen.feature.RockCrystalOreFeatureConfiguration;
import hellfirepvp.astralsorcery.common.worldgen.placement.RiverbedPlacement;
import hellfirepvp.astralsorcery.common.worldgen.structure.FocalPointStructure;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FocalPointRegisterProcessor;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralWorldGenProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralWorldGenProvider {

    public static final ResourceKey<ConfiguredFeature<?, ?>> MARBLE_ORE = configured("marble_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> ROCK_CRYSTAL_ORE = configured("rock_crystal_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> AQUAMARINE_SHALE_ORE = configured("aquamarine_shale_ore");
    public static final ResourceKey<ConfiguredFeature<?, ?>> GLIMMER_AMARANTH_FLOWERS = configured("glimmer_amaranth_flowers");

    public static final ResourceKey<PlacedFeature> PLACED_MARBLE_ORE = placed("placed_marble_ore");
    public static final ResourceKey<PlacedFeature> PLACED_ROCK_CRYSTAL_ORE = placed("placed_rock_crystal_ore");
    public static final ResourceKey<PlacedFeature> PLACED_AQUAMARINE_SHALE_ORE = placed("placed_aquamarine_shale_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GLIMMER_AMARANTH_FLOWERS = placed("placed_glimmer_amaranth_flowers");

    public static final ResourceKey<BiomeModifier> MARBLE_ORE_BIOME_MODIFIER = biomeModifier("generate_marble_ore");
    public static final ResourceKey<BiomeModifier> ROCK_CRYSTAL_ORE_BIOME_MODIFIER = biomeModifier("generate_rock_crystal_ore");
    public static final ResourceKey<BiomeModifier> AQUAMARINE_SHALE_ORE_BIOME_MODIFIER = biomeModifier("generate_aquamarine_shale_ore");
    public static final ResourceKey<BiomeModifier> GLIMMER_AMARANTH_FLOWERS_BIOME_MODIFIER = biomeModifier("generate_glimmer_amaranth_flowers");

    public static final ResourceKey<Structure> FOCAL_POINT = structure("focal_point");
    public static final ResourceKey<StructureSet> FOCAL_POINT_STRUCTURES = structureSet("focal_point_structures");
    public static final ResourceKey<StructureTemplatePool> FOCAL_POINT_STRUCTURE_POOL = structurePool("focal_point_pool");
    public static final ResourceKey<StructureProcessorList> FOCAL_POINT_REGISTER_PROCESSOR = structureProcessorList("focal_point_register_processor");

    //Set up the feature to begin with & its configuration
    public static void generateConfiguredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(MARBLE_ORE, new ConfiguredFeature<>(Feature.ORE,
                new OreConfiguration(new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD), BlocksAS.MARBLE_RAW.get().defaultBlockState(), 26)));
        context.register(ROCK_CRYSTAL_ORE, new ConfiguredFeature<>(WorldGenAS.ROCK_CRYSTAL_ORE.get(),
                new RockCrystalOreFeatureConfiguration(BlockPredicate.matchesTag(BlockTags.DEEPSLATE_ORE_REPLACEABLES))));
        context.register(AQUAMARINE_SHALE_ORE, new ConfiguredFeature<>(Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(BlocksAS.AQUAMARINE_SHALE.get()))));
        context.register(GLIMMER_AMARANTH_FLOWERS, new ConfiguredFeature<>(Feature.FLOWER,
                FeatureUtils.simpleRandomPatchConfiguration(56,
                        PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(BlocksAS.GLIMMER_AMARANTH.get()))))));
    }
    //Pair the configured feature with placement rules

    public static void generatePlacedFeatures(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(PLACED_MARBLE_ORE, new PlacedFeature(features.getOrThrow(MARBLE_ORE),
                List.of(
                        CountPlacement.of(UniformInt.of(0, 5)),
                        InSquarePlacement.spread(),
                        HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(100))
                )));
        context.register(PLACED_ROCK_CRYSTAL_ORE, new PlacedFeature(features.getOrThrow(ROCK_CRYSTAL_ORE),
                List.of(
                        RarityFilter.onAverageOnceEvery(24),
                        HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(4), VerticalAnchor.aboveBottom(7))
                )));
        context.register(PLACED_AQUAMARINE_SHALE_ORE, new PlacedFeature(features.getOrThrow(AQUAMARINE_SHALE_ORE),
                List.of(
                        CountPlacement.of(BiasedToBottomInt.of(3, 8)),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        new RiverbedPlacement()
                )));
        context.register(PLACED_GLIMMER_AMARANTH_FLOWERS, new PlacedFeature(features.getOrThrow(GLIMMER_AMARANTH_FLOWERS),
                List.of(
                        CountPlacement.of(UniformInt.of(0, 4)),
                        RarityFilter.onAverageOnceEvery(48),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP
                )));
    }

    //Actually add the placed features to biomes to generate
    public static void generateBiomeModifiers(BootstrapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> features = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderSet<Biome> inOverworldBiomes = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderSet<Biome> inSnowyBiomes = biomes.getOrThrow(Tags.Biomes.IS_SNOWY);

        context.register(MARBLE_ORE_BIOME_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                inOverworldBiomes,
                HolderSet.direct(features.getOrThrow(PLACED_MARBLE_ORE)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(ROCK_CRYSTAL_ORE_BIOME_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                inOverworldBiomes,
                HolderSet.direct(features.getOrThrow(PLACED_ROCK_CRYSTAL_ORE)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(AQUAMARINE_SHALE_ORE_BIOME_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                inOverworldBiomes,
                HolderSet.direct(features.getOrThrow(PLACED_AQUAMARINE_SHALE_ORE)),
                GenerationStep.Decoration.UNDERGROUND_ORES
        ));
        context.register(GLIMMER_AMARANTH_FLOWERS_BIOME_MODIFIER, new BiomeModifiers.AddFeaturesBiomeModifier(
                inSnowyBiomes,
                HolderSet.direct(features.getOrThrow(PLACED_GLIMMER_AMARANTH_FLOWERS)),
                GenerationStep.Decoration.VEGETAL_DECORATION
        ));
    }

    public static void generateStructures(BootstrapContext<Structure> context) {
        HolderSet<Biome> inOverworldBiomes = context.lookup(Registries.BIOME).getOrThrow(BiomeTags.IS_OVERWORLD);
        HolderGetter<StructureTemplatePool> structurePools = context.lookup(Registries.TEMPLATE_POOL);

        context.register(FOCAL_POINT,
                new FocalPointStructure(
                        new Structure.StructureSettings.Builder(inOverworldBiomes)
                                .generationStep(GenerationStep.Decoration.SURFACE_STRUCTURES)
                                .terrainAdapation(TerrainAdjustment.NONE)
                                .build(),
                        structurePools.getOrThrow(FOCAL_POINT_STRUCTURE_POOL)
                )
        );
    }

    public static void generateStructureSets(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        context.register(FOCAL_POINT_STRUCTURES, new StructureSet(
                structures.getOrThrow(FOCAL_POINT),
                new RandomSpreadStructurePlacement(
                        14,
                        4,
                        RandomSpreadType.LINEAR,
                        717324875
                )
        ));
    }

    public static void generateStructurePools(BootstrapContext<StructureTemplatePool> context) {
        HolderGetter<StructureProcessorList> structureProcessorLists = context.lookup(Registries.PROCESSOR_LIST);

        context.register(FOCAL_POINT_STRUCTURE_POOL, new StructureTemplatePool(
                context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY),
                List.of(
                        new Pair<>(StructurePoolElement.single(FOCAL_POINT.location().toString(), structureProcessorLists.getOrThrow(FOCAL_POINT_REGISTER_PROCESSOR)), 1)
                ),
                StructureTemplatePool.Projection.RIGID
        ));
    }

    public static void generateStructureProcessorLists(BootstrapContext<StructureProcessorList> context) {
        HolderSet<BaseConstellation> focalPointSet = context.lookup(RegistriesAS.KEY_CONSTELLATIONS).getOrThrow(TagsAS.Constellations.MAY_BE_FOCAL_POINT);

        context.register(FOCAL_POINT_REGISTER_PROCESSOR, new StructureProcessorList(
                List.of(
                        new FocalPointRegisterProcessor(focalPointSet)
                )
        ));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configured(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, AstralSorcery.key(name));
    }

    private static ResourceKey<PlacedFeature> placed(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, AstralSorcery.key(name));
    }

    private static ResourceKey<BiomeModifier> biomeModifier(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AstralSorcery.key(name));
    }

    private static ResourceKey<Structure> structure(String name) {
        return ResourceKey.create(Registries.STRUCTURE, AstralSorcery.key(name));
    }

    private static ResourceKey<StructureSet> structureSet(String name) {
        return ResourceKey.create(Registries.STRUCTURE_SET, AstralSorcery.key(name));
    }

    private static ResourceKey<StructureTemplatePool> structurePool(String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, AstralSorcery.key(name));
    }

    private static ResourceKey<StructureProcessorList> structureProcessorList(String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, AstralSorcery.key(name));
    }
}
