/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.FeatureGenerationConfig;
import hellfirepvp.astralsorcery.common.world.StructureGenerationConfig;
import hellfirepvp.astralsorcery.common.world.TemplateStructureFeature;
import hellfirepvp.astralsorcery.common.world.feature.config.ReplaceBlockConfig;
import hellfirepvp.astralsorcery.common.world.structure.AncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.SmallShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureAncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureDesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureSmallShrineStructure;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Config.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Features.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Placements.*;
import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.Structures.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryWorldGeneration
 * Created by HellFirePvP
 * Date: 18.11.2020 / 21:17
 */
public class RegistryWorldGeneration {

    private static final Map<StructureFeature<?, ?>, StructureGenerationConfig> STRUCTURES = new HashMap<>();
    private static final Map<ConfiguredFeature<?, ?>, FeatureGenerationConfig> FEATURES = new HashMap<>();
    private static final Map<ConfiguredFeature<?, ?>, GenerationStage.Decoration> FEATURE_STAGE = new HashMap<>();

    public static void init() {
        registerFeature(KEY_FEATURE_REPLACE_BLOCK, REPLACE_BLOCK);
        registerFeature(KEY_FEATURE_ROCK_CRYSTAL, ROCK_CRYSTAL);

        registerPlacement(KEY_PLACEMENT_CHANCE, CHANCE);
        registerPlacement(KEY_PLACEMENT_RIVERBED, RIVERBED);
        registerPlacement(KEY_PLACEMENT_WORLD_FILTER, WORLD_FILTER);

        ANCIENT_SHRINE_PIECE = registerStructurePiece(KEY_ANCIENT_SHRINE, AncientShrineStructure::new);
        DESERT_SHRINE_PIECE  = registerStructurePiece(KEY_DESERT_SHRINE,  DesertShrineStructure::new);
        SMALL_SHRINE_PIECE   = registerStructurePiece(KEY_SMALL_SHRINE,   SmallShrineStructure::new);

        STRUCTURE_ANCIENT_SHRINE = registerStructure(KEY_ANCIENT_SHRINE, CFG_ANCIENT_SHRINE, new FeatureAncientShrineStructure());
        STRUCTURE_DESERT_SHRINE  = registerStructure(KEY_DESERT_SHRINE, CFG_DESERT_SHRINE, new FeatureDesertShrineStructure());
        STRUCTURE_SMALL_SHRINE   = registerStructure(KEY_SMALL_SHRINE, CFG_SMALL_SHRINE, new FeatureSmallShrineStructure());

        GEN_GLOW_FLOWER = registerConfiguredFeature(KEY_GLOW_FLOWER, GenerationStage.Decoration.VEGETAL_DECORATION, CFG_GLOW_FLOWER,
                Feature.FLOWER.withConfiguration(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(BlocksAS.GLOW_FLOWER.getDefaultState()), SimpleBlockPlacer.PLACER)
                        .tries(12)
                        .build())
                        .func_242732_c(6)
                        .withPlacement(Features.Placements.VEGETATION_PLACEMENT)
                        .withPlacement(Features.Placements.HEIGHTMAP_PLACEMENT)
                        .withPlacement(WORLD_FILTER.configure(CFG_GLOW_FLOWER.worldFilterConfig())));
        GEN_ROCK_CRYSTAL = registerConfiguredFeature(KEY_ROCK_CRYSTAL, GenerationStage.Decoration.UNDERGROUND_ORES, CFG_ROCK_CRYSTAL,
                ROCK_CRYSTAL.withConfiguration(new ReplaceBlockConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, BlocksAS.ROCK_CRYSTAL_ORE.getDefaultState()))
                        .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(5, 0, 2)))
                        .withPlacement(CHANCE.withChance(1F / 25F))
                        .withPlacement(WORLD_FILTER.configure(CFG_ROCK_CRYSTAL.worldFilterConfig())));
        GEN_AQUAMARINE = registerConfiguredFeature(KEY_AQUAMARINE, GenerationStage.Decoration.UNDERGROUND_ORES, CFG_AQUAMARINE,
                REPLACE_BLOCK.withConfiguration(new ReplaceBlockConfig(new TagMatchRuleTest(BlockTags.SAND), BlocksAS.AQUAMARINE_SAND_ORE.getDefaultState()))
                        .withPlacement(RIVERBED.configure(NoPlacementConfig.INSTANCE))
                        .func_242732_c(8)
                        .withPlacement(WORLD_FILTER.configure(CFG_AQUAMARINE.worldFilterConfig())));
        GEN_MARBLE = registerConfiguredFeature(KEY_MARBLE, GenerationStage.Decoration.UNDERGROUND_ORES, CFG_MARBLE,
                Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD, BlocksAS.MARBLE_RAW.getDefaultState(), 26))
                        .range(96)
                        .square()
                        .func_242732_c(10)
                        .withPlacement(WORLD_FILTER.configure(CFG_MARBLE.worldFilterConfig())));
    }

    public static void registerStructureGeneration() {
        List<Map<Structure<?>, StructureSeparationSettings>> structureSettings = new ArrayList<>();
        structureSettings.add(DimensionSettings.field_242740_q.getStructures().func_236195_a_());
        WorldGenRegistries.NOISE_SETTINGS.forEach(settings -> structureSettings.add(settings.getStructures().func_236195_a_()));

        ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> builder = ImmutableMap.builder();
        builder.putAll(DimensionStructuresSettings.field_236191_b_);
        STRUCTURES.forEach((structureFeature, cfg) -> {
            if (cfg.isEnabled()) {
                StructureSeparationSettings settings = cfg.createSettings();
                builder.put(structureFeature.field_236268_b_, settings);
                structureSettings.forEach(noiseStructureSettings -> noiseStructureSettings.put(structureFeature.field_236268_b_, settings));
            }
        });
        DimensionStructuresSettings.field_236191_b_ = builder.build();

        Structure.field_236384_t_ = ImmutableList.<Structure<?>>builder()
                .addAll(Structure.field_236384_t_)
                .add(STRUCTURE_ANCIENT_SHRINE, STRUCTURE_DESERT_SHRINE, STRUCTURE_SMALL_SHRINE)
                .build();
    }

    public static void loadBiomeFeatures(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder gen = event.getGeneration();
        STRUCTURES.forEach((structureFeature, cfg) -> {
            if (cfg.isEnabled() && cfg.canGenerateIn(event.getCategory())) {
                gen.withStructure(structureFeature);
            }
        });
        FEATURES.forEach((feature, cfg) -> {
            if (cfg.isEnabled() && cfg.canGenerateIn(event.getCategory())) {
                GenerationStage.Decoration stage = FEATURE_STAGE.get(feature);
                if (stage == null) {
                    ResourceLocation key = WorldGenRegistries.CONFIGURED_FEATURE.getOptionalKey(feature)
                            .map(RegistryKey::getLocation)
                            .orElse(new ResourceLocation("not_registered"));
                    throw new IllegalArgumentException("Unknown generation stage for feature " + key + "!");
                }
                gen.withFeature(stage, feature);
            }
        });
    }

    public static void addConfigEntries(Consumer<ConfigEntry> registrar) {
        registrar.accept(CFG_ANCIENT_SHRINE);
        registrar.accept(CFG_DESERT_SHRINE);
        registrar.accept(CFG_SMALL_SHRINE);

        registrar.accept(CFG_GLOW_FLOWER);
        registrar.accept(CFG_ROCK_CRYSTAL);
        registrar.accept(CFG_AQUAMARINE);
        registrar.accept(CFG_MARBLE);
    }

    private static ConfiguredFeature<?, ?> registerConfiguredFeature(ResourceLocation key, GenerationStage.Decoration stage, FeatureGenerationConfig cfg, ConfiguredFeature<?, ?> feature) {
        FEATURE_STAGE.put(feature, stage);
        FEATURES.put(feature, cfg);
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, key, feature);
    }

    private static void registerFeature(ResourceLocation key, Feature<?> feature) {
        AstralSorcery.getProxy().getRegistryPrimer().register(feature.setRegistryName(key));
    }

    private static void registerPlacement(ResourceLocation key, Placement<?> placement) {
        AstralSorcery.getProxy().getRegistryPrimer().register(placement.setRegistryName(key));
    }

    private static <T extends IStructurePieceType> T registerStructurePiece(ResourceLocation key, T type) {
        return Registry.register(Registry.STRUCTURE_PIECE, key, type);
    }

    private static <S extends TemplateStructureFeature> S registerStructure(ResourceLocation key, StructureGenerationConfig cfg, S structure) {
        AstralSorcery.getProxy().getRegistryPrimer().register(structure.setRegistryName(key));
        Structure.NAME_STRUCTURE_BIMAP.put(structure.getStructureName(), structure);
        StructureFeature<?, ?> structureFeature = structure.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG);
        STRUCTURES.put(structureFeature, cfg);
        WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, key, structureFeature);
        return structure;
    }
}
