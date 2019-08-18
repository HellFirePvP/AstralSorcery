/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.astralsorcery.common.lib.StructuresAS;
import hellfirepvp.astralsorcery.common.world.feature.AquamarineFeature;
import hellfirepvp.astralsorcery.common.world.feature.GlowFlowerFeature;
import hellfirepvp.astralsorcery.common.world.feature.RockCrystalFeature;
import hellfirepvp.astralsorcery.common.world.feature.StructureGenerationFeature;
import hellfirepvp.astralsorcery.common.world.placement.EvenStructurePlacement;
import hellfirepvp.astralsorcery.common.world.placement.RandomReplaceablePlacement;
import hellfirepvp.astralsorcery.common.world.placement.config.EvenStructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.placement.config.FeaturePlacementConfig;
import hellfirepvp.astralsorcery.common.world.placement.RandomFlowerPlacement;
import hellfirepvp.astralsorcery.common.world.placement.config.ReplacingFeaturePlacementConfig;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.registries.ForgeRegistries;

import static hellfirepvp.astralsorcery.common.lib.WorldGenerationAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryWorldGeneration
 * Created by HellFirePvP
 * Date: 21.07.2019 / 22:30
 */
public class RegistryWorldGeneration {

    private RegistryWorldGeneration() {}

    public static void registerFeatureConfigurations(ServerConfig cfg) {
        Placement.DESERT_SHRINE = cfg.addConfigEntry(new EvenStructurePlacementConfig(StructureTypesAS.STYPE_DESERT, 4,
                Lists.newArrayList(BiomeDictionary.Type.SANDY), Lists.newArrayList(0),
                20, 90, 140));
        Placement.MOUNTAIN_SHRINE = cfg.addConfigEntry(new EvenStructurePlacementConfig(StructureTypesAS.STYPE_MOUNTAIN, 10,
                Lists.newArrayList(BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.MOUNTAIN), Lists.newArrayList(0),
                50, 160, 140));
        Placement.SMALL_SHRINE = cfg.addConfigEntry(new EvenStructurePlacementConfig(StructureTypesAS.STYPE_SMALL, 4,
                Lists.newArrayList(BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.FOREST), Lists.newArrayList(0),
                20, 200, 140));

        Placement.GLOW_FLOWER = cfg.addConfigEntry(new FeaturePlacementConfig("glow_flower", true, true,
                Lists.newArrayList(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.COLD), Lists.newArrayList(0),
                50, 210, 40, 1));
        Placement.ROCK_CRYSTAL = cfg.addConfigEntry(new ReplacingFeaturePlacementConfig("rock_crystal", false, true,
                Lists.newArrayList(), Lists.newArrayList(0),
                2, 5, 25, 2,
                Lists.newArrayList(Blocks.STONE.getDefaultState(), Blocks.DIORITE.getDefaultState(), Blocks.GRANITE.getDefaultState(), Blocks.ANDESITE.getDefaultState())));
        Placement.AQUAMARINE = cfg.addConfigEntry(new ReplacingFeaturePlacementConfig("aquamarine", true, true,
                Lists.newArrayList(BiomeDictionary.Type.RIVER, BiomeDictionary.Type.BEACH), Lists.newArrayList(0),
                45, 25, 1, 64,
                Lists.newArrayList(Blocks.SAND.getDefaultState())));

        Placement.MARBLE = new CountRangeConfig(10, 0, 0, 64);
    }

    public static void registerFeatures() {
        registerFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
                new ConfiguredFeature<>(new StructureGenerationFeature(StructureTypesAS.STYPE_DESERT, StructuresAS.STRUCT_DESERT_SHRINE), NoFeatureConfig.NO_FEATURE_CONFIG),
                new ConfiguredPlacement<>(new EvenStructurePlacement(Placement.DESERT_SHRINE), Placement.DESERT_SHRINE));
        registerFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
                new ConfiguredFeature<>(new StructureGenerationFeature(StructureTypesAS.STYPE_MOUNTAIN, StructuresAS.STRUCT_MOUNTAIN_SHRINE), NoFeatureConfig.NO_FEATURE_CONFIG),
                new ConfiguredPlacement<>(new EvenStructurePlacement(Placement.MOUNTAIN_SHRINE), Placement.MOUNTAIN_SHRINE));
        registerFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
                new ConfiguredFeature<>(new StructureGenerationFeature(StructureTypesAS.STYPE_SMALL, StructuresAS.STRUCT_SMALL_SHRINE), NoFeatureConfig.NO_FEATURE_CONFIG),
                new ConfiguredPlacement<>(new EvenStructurePlacement(Placement.SMALL_SHRINE), Placement.SMALL_SHRINE));

        registerFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                new ConfiguredFeature<>(new GlowFlowerFeature(), NoFeatureConfig.NO_FEATURE_CONFIG),
                new ConfiguredPlacement<>(new RandomFlowerPlacement(Placement.GLOW_FLOWER), Placement.GLOW_FLOWER));
        registerFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                new ConfiguredFeature<>(new RockCrystalFeature(), NoFeatureConfig.NO_FEATURE_CONFIG),
                new ConfiguredPlacement<>(new RandomReplaceablePlacement(Placement.ROCK_CRYSTAL), Placement.ROCK_CRYSTAL));
        registerFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                new ConfiguredFeature<>(new AquamarineFeature(), NoFeatureConfig.NO_FEATURE_CONFIG),
                new ConfiguredPlacement<>(new RandomReplaceablePlacement(Placement.AQUAMARINE), Placement.AQUAMARINE));

        registerFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                new ConfiguredFeature<>(Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlocksAS.MARBLE_RAW.getDefaultState(), 33)),
                new ConfiguredPlacement<>(net.minecraft.world.gen.placement.Placement.COUNT_RANGE, Placement.MARBLE));
    }

    private static <FC extends IFeatureConfig, PC extends IPlacementConfig> void registerFeature(
            GenerationStage.Decoration stage,
            ConfiguredFeature<FC> feature,
            ConfiguredPlacement<PC> placement) {

        for (Biome b : ForgeRegistries.BIOMES) {
            b.addFeature(stage, Biome.createDecoratedFeature(feature.feature, feature.config, placement.decorator, placement.config));
        }
    }

}
