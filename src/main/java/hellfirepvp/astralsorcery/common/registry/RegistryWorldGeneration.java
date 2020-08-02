/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.ServerConfig;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.WorldGenerationAS;
import hellfirepvp.astralsorcery.common.util.block.BlockStateList;
import hellfirepvp.astralsorcery.common.world.config.FeaturePlacementConfig;
import hellfirepvp.astralsorcery.common.world.config.ReplacingFeaturePlacementConfig;
import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.feature.AquamarineFeature;
import hellfirepvp.astralsorcery.common.world.feature.GlowFlowerFeature;
import hellfirepvp.astralsorcery.common.world.feature.RockCrystalFeature;
import hellfirepvp.astralsorcery.common.world.placement.RandomFlowerPlacement;
import hellfirepvp.astralsorcery.common.world.placement.RandomReplaceablePlacement;
import hellfirepvp.astralsorcery.common.world.placement.RiverbedPlacement;
import hellfirepvp.astralsorcery.common.world.structure.AncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.SmallShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureAncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureDesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureSmallShrineStructure;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
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
        WorldGenerationAS.Placement.DESERT_SHRINE = cfg.addConfigEntry(new StructurePlacementConfig("struct_desert", 9,
                Lists.newArrayList(BiomeDictionary.Type.SANDY), Lists.newArrayList(DimensionType.OVERWORLD),
                20, 90, 90,
                12, 5));
        WorldGenerationAS.Placement.MOUNTAIN_SHRINE = cfg.addConfigEntry(new StructurePlacementConfig("struct_mountain", 19,
                Lists.newArrayList(BiomeDictionary.Type.SNOWY, BiomeDictionary.Type.MOUNTAIN), Lists.newArrayList(DimensionType.OVERWORLD),
                50, 160, 90,
                12, 5));
        WorldGenerationAS.Placement.SMALL_SHRINE = cfg.addConfigEntry(new StructurePlacementConfig("struct_small", 9,
                Lists.newArrayList(BiomeDictionary.Type.PLAINS, BiomeDictionary.Type.FOREST), Lists.newArrayList(DimensionType.OVERWORLD),
                20, 200, 140,
                12, 8));

        WorldGenerationAS.Placement.GLOW_FLOWER = cfg.addConfigEntry(new FeaturePlacementConfig("glow_flower", true, true,
                Lists.newArrayList(BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.COLD), Lists.newArrayList(DimensionType.OVERWORLD),
                50, 210, 12, 5));
        WorldGenerationAS.Placement.ROCK_CRYSTAL = cfg.addConfigEntry(new ReplacingFeaturePlacementConfig("rock_crystal", false, true,
                Lists.newArrayList(), Lists.newArrayList(DimensionType.OVERWORLD),
                2, 8, 14, 2,
                new BlockStateList().add(Blocks.STONE).add(Blocks.DIORITE).add(Blocks.GRANITE).add(Blocks.ANDESITE)));
        WorldGenerationAS.Placement.AQUAMARINE = cfg.addConfigEntry(new ReplacingFeaturePlacementConfig("aquamarine", false, true,
                Lists.newArrayList(), Lists.newArrayList(DimensionType.OVERWORLD),
                52, 67, 2, 40,
                new BlockStateList().add(Blocks.SAND)));

        WorldGenerationAS.Placement.MARBLE = new CountRangeConfig(10, 0, 0, 64);
    }

    public static void registerFeatures() {
        ANCIENT_SHRINE_PIECE = Registry.register(Registry.STRUCTURE_PIECE, KEY_ANCIENT_SHRINE, AncientShrineStructure::new);
        DESERT_SHRINE_PIECE  = Registry.register(Registry.STRUCTURE_PIECE, KEY_DESERT_SHRINE,  DesertShrineStructure::new);
        SMALL_SHRINE_PIECE   = Registry.register(Registry.STRUCTURE_PIECE, KEY_SMALL_SHRINE,   SmallShrineStructure::new);

        STRUCTURE_ANCIENT_SHRINE = register(new FeatureAncientShrineStructure(WorldGenerationAS.Placement.MOUNTAIN_SHRINE), KEY_ANCIENT_SHRINE);
        STRUCTURE_DESERT_SHRINE  = register(new FeatureDesertShrineStructure(WorldGenerationAS.Placement.DESERT_SHRINE), KEY_DESERT_SHRINE);
        STRUCTURE_SMALL_SHRINE   = register(new FeatureSmallShrineStructure(WorldGenerationAS.Placement.SMALL_SHRINE), KEY_SMALL_SHRINE);
    }

    private static <FC extends IFeatureConfig, V extends Feature<FC>> V register(V feature, ResourceLocation key) {
        AstralSorcery.getProxy().getRegistryPrimer().register(feature.setRegistryName(key));
        return feature;
    }

    public static void addFeaturesToBiomes() {
        registerStructure(GenerationStage.Decoration.SURFACE_STRUCTURES, STRUCTURE_ANCIENT_SHRINE, NoFeatureConfig.NO_FEATURE_CONFIG);
        registerStructure(GenerationStage.Decoration.SURFACE_STRUCTURES, STRUCTURE_DESERT_SHRINE, NoFeatureConfig.NO_FEATURE_CONFIG);
        registerStructure(GenerationStage.Decoration.SURFACE_STRUCTURES, STRUCTURE_SMALL_SHRINE, NoFeatureConfig.NO_FEATURE_CONFIG);

        registerFeature(GenerationStage.Decoration.VEGETAL_DECORATION,
                new GlowFlowerFeature(), NoFeatureConfig.NO_FEATURE_CONFIG,
                new RandomFlowerPlacement(WorldGenerationAS.Placement.GLOW_FLOWER), WorldGenerationAS.Placement.GLOW_FLOWER);
        registerFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                new RockCrystalFeature(), NoFeatureConfig.NO_FEATURE_CONFIG,
                new RandomReplaceablePlacement(WorldGenerationAS.Placement.ROCK_CRYSTAL), WorldGenerationAS.Placement.ROCK_CRYSTAL);
        registerFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                new AquamarineFeature(), NoFeatureConfig.NO_FEATURE_CONFIG,
                new RiverbedPlacement(WorldGenerationAS.Placement.AQUAMARINE), WorldGenerationAS.Placement.AQUAMARINE);

        registerFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlocksAS.MARBLE_RAW.getDefaultState(), 33),
                Placement.COUNT_RANGE, WorldGenerationAS.Placement.MARBLE);
    }

    private static <FC extends IFeatureConfig, PC extends IPlacementConfig> void registerFeature(
            GenerationStage.Decoration stage,
            Feature<FC> feature,
            FC featureConfig,
            Placement<PC> placement,
            PC placementConfig) {

        for (Biome b : ForgeRegistries.BIOMES) {
            b.addFeature(stage, feature.withConfiguration(featureConfig).withPlacement(placement.configure(placementConfig)));
        }
    }

    private static <FC extends IFeatureConfig> void registerStructure(GenerationStage.Decoration stage, Structure<FC> structure, FC featureConfig) {
        for (Biome b : ForgeRegistries.BIOMES) {
            b.addFeature(stage, structure.withConfiguration(featureConfig).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
            b.addStructure(structure.withConfiguration(featureConfig));
        }
    }

}
