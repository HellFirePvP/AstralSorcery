/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import com.google.common.collect.Lists;
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
import hellfirepvp.astralsorcery.common.world.placement.SimpleCountRangePlacement;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureAncientShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureDesertShrineStructure;
import hellfirepvp.astralsorcery.common.world.structure.feature.FeatureSmallShrineStructure;
import hellfirepvp.observerlib.common.util.RegistryUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

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
                true, true,
                Lists.newArrayList(Biome.Category.DESERT.getName(), Biome.Category.MESA.getName(), Biome.Category.SAVANNA.getName()),
                Lists.newArrayList(DimensionType.field_242710_a),
                20, 90, 90,
                12, 5));
        WorldGenerationAS.Placement.MOUNTAIN_SHRINE = cfg.addConfigEntry(new StructurePlacementConfig("struct_mountain", 19,
                true, true,
                Lists.newArrayList(Biome.Category.EXTREME_HILLS.getName(), Biome.Category.ICY.getName()),
                Lists.newArrayList(DimensionType.field_242710_a),
                50, 160, 90,
                12, 5));
        WorldGenerationAS.Placement.SMALL_SHRINE = cfg.addConfigEntry(new StructurePlacementConfig("struct_small", 9,
                true, true,
                Lists.newArrayList(Biome.Category.PLAINS.getName(), Biome.Category.FOREST.getName(), Biome.Category.TAIGA.getName()),
                Lists.newArrayList(DimensionType.field_242710_a),
                20, 200, 140,
                12, 8));

        WorldGenerationAS.Placement.GLOW_FLOWER = cfg.addConfigEntry(new FeaturePlacementConfig("glow_flower", true, true,
                Lists.newArrayList(Biome.Category.EXTREME_HILLS.getName(), Biome.Category.ICY.getName()),
                Lists.newArrayList(DimensionType.field_242710_a),
                50, 210, 12, 5));
        WorldGenerationAS.Placement.ROCK_CRYSTAL = cfg.addConfigEntry(new ReplacingFeaturePlacementConfig("rock_crystal", false, true,
                Lists.newArrayList(),
                Lists.newArrayList(DimensionType.field_242710_a),
                2, 8, 14, 2,
                new BlockStateList().add(Blocks.STONE).add(Blocks.DIORITE).add(Blocks.GRANITE).add(Blocks.ANDESITE)));
        WorldGenerationAS.Placement.AQUAMARINE = cfg.addConfigEntry(new ReplacingFeaturePlacementConfig("aquamarine", false, true,
                Lists.newArrayList(),
                Lists.newArrayList(DimensionType.field_242710_a),
                52, 67, 2, 40,
                new BlockStateList().add(Blocks.SAND)));

        WorldGenerationAS.Placement.MARBLE = cfg.addConfigEntry(new FeaturePlacementConfig("marble", false, false,
                Lists.newArrayList(),
                Lists.newArrayList(),
                0, 64, 1, 8));
    }

    public static void registerFeatures() {
        ANCIENT_SHRINE_PIECE = Registry.register(Registry.STRUCTURE_PIECE, KEY_ANCIENT_SHRINE, hellfirepvp.astralsorcery.common.world.structure.AncientShrineStructure::new);
        DESERT_SHRINE_PIECE  = Registry.register(Registry.STRUCTURE_PIECE, KEY_DESERT_SHRINE,  hellfirepvp.astralsorcery.common.world.structure.DesertShrineStructure::new);
        SMALL_SHRINE_PIECE   = Registry.register(Registry.STRUCTURE_PIECE, KEY_SMALL_SHRINE,   hellfirepvp.astralsorcery.common.world.structure.SmallShrineStructure::new);

        STRUCTURE_ANCIENT_SHRINE = new FeatureAncientShrineStructure(WorldGenerationAS.Placement.MOUNTAIN_SHRINE).func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG);
        STRUCTURE_DESERT_SHRINE  = new FeatureDesertShrineStructure(WorldGenerationAS.Placement.DESERT_SHRINE).func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG);
        STRUCTURE_SMALL_SHRINE   = new FeatureSmallShrineStructure(WorldGenerationAS.Placement.SMALL_SHRINE).func_236391_a_(IFeatureConfig.NO_FEATURE_CONFIG);
    }

    public static void addFeaturesToBiomes() {
        registerStructure(STRUCTURE_ANCIENT_SHRINE);
        registerStructure(STRUCTURE_DESERT_SHRINE);
        registerStructure(STRUCTURE_SMALL_SHRINE);

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
                Feature.ORE, new OreFeatureConfig(OreFeatureConfig.FillerBlockType.field_241882_a, BlocksAS.MARBLE_RAW.getDefaultState(), 27),
                new SimpleCountRangePlacement(WorldGenerationAS.Placement.MARBLE), WorldGenerationAS.Placement.MARBLE);
    }

    private static <FC extends IFeatureConfig, PC extends IPlacementConfig> void registerFeature(
            GenerationStage.Decoration stage,
            Feature<FC> feature,
            FC featureConfig,
            Placement<PC> placement,
            PC placementConfig) {

        for (Biome b : RegistryUtil.side(FMLEnvironment.dist.isClient() ? LogicalSide.CLIENT : LogicalSide.SERVER).getValues(Registry.BIOME_KEY)) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = b.func_242440_e().func_242498_c();
            ensureFeatureCapacity(biomeFeatures, stage.ordinal());
            biomeFeatures.get(stage.ordinal()).add(() -> feature.withConfiguration(featureConfig).withPlacement(placement.configure(placementConfig)));
        }
    }

    private static void ensureFeatureCapacity(List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures, int size) {
        while(biomeFeatures.size() <= size) {
            biomeFeatures.add(Lists.newArrayList());
        }
    }

    private static <FC extends IFeatureConfig, C extends StructureFeature<FC, ?>> void registerStructure(C structure) {
        for (Biome b : RegistryUtil.side(FMLEnvironment.dist.isClient() ? LogicalSide.CLIENT : LogicalSide.SERVER).getValues(Registry.BIOME_KEY)) {
            Collection<Supplier<StructureFeature<?, ?>>> biomeStructures = b.func_242440_e().func_242487_a();
            biomeStructures.add(() -> structure);
        }
    }

}
