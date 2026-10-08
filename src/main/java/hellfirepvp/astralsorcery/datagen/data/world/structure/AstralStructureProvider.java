/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.world.structure;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.LootTablesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lib.constants.TagsAS;
import hellfirepvp.astralsorcery.common.worldgen.structure.marker.*;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FlareLightColorizationProcessor;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FlareLightExtinguishProcessor;
import hellfirepvp.astralsorcery.common.worldgen.structure.processor.FocalPointRegisterProcessor;
import net.minecraft.core.HolderSet;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralStructureProvider
 * Created by HellFirePvP
 * Date: 06.10.2026 / 23:12
 */
public class AstralStructureProvider {

    private static final List<StructureDefinition> ALL = new ArrayList<>();

    public static final StructureDefinition FOCAL_POINT = definition("focal_point", builder -> {
            builder.biomes(TagsAS.Biomes.FOCAL_POINT_BIOMES)
                    .spread(10, 4, 0x2AC1824B)
                    .spreadType(RandomSpreadType.TRIANGULAR)
                    .verticalOffset(8)
                    .requiredClearance(16)
                    .boundingBoxExpansion(6)
                    .extendBoundsToBuildHeight()
                    .rotation(Rotation.NONE)
                    .liquidSettings(LiquidSettings.IGNORE_WATERLOGGING)
                    .processors(ctx -> {
                        HolderSet<BaseConstellation> focalPointConstellations = ctx.lookup(RegistriesAS.KEY_CONSTELLATIONS)
                                .getOrThrow(TagsAS.Constellations.MAY_BE_FOCAL_POINT);
                        return List.of(new FocalPointRegisterProcessor(focalPointConstellations));
                    });
    });
    public static final StructureDefinition OBLITERATION = definition("obliteration", builder -> {
        builder.biomes(TagsAS.Biomes.OBLITERATION_BIOMES)
                .spread(80, 14, 0x52E3C1F)
                .heightmap(Heightmap.Types.OCEAN_FLOOR_WG)
                .terrainAdjustment(TerrainAdjustment.BEARD_BOX)
                .requiredClearance(10)
                .boundingBoxExpansion(1)
                .marker("chest", MarkerReplacementRandom.builder()
                        .entry(1, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST))
                        .entry(3, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST_SMALL))
                        .entry(16, MarkerReplacementBlock.of(BlocksAS.MARBLE_RAW))
                        .build())
                .marker("chest_bricks", MarkerReplacementRandom.builder()
                        .entry(1, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST))
                        .entry(3, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST_SMALL))
                        .entry(16, MarkerReplacementBlock.of(BlocksAS.MARBLE_BRICKS))
                        .build())
                .processors(ctx -> List.of(FlareLightExtinguishProcessor.getInstance()));
    });
    public static final StructureDefinition DIG_SITE = definition("dig_site", builder -> {
        builder.biomes(TagsAS.Biomes.DIG_SITE_BIOMES)
                .spread(24, 6, 0x794A12E0)
                .terrainAdjustment(TerrainAdjustment.BEARD_THIN)
                .verticalOffset(-2)
                .requiredClearance(6)
                .marker("dig_sand", MarkerReplacementBiome.builder()
                        .add(Tags.Biomes.IS_MOUNTAIN, MarkerReplacementRandom.builder()
                                .entry(1, MarkerReplacementLootContainer.suspiciousGravel(LootTablesAS.DIG_SITE_ARCHAEOLOGY))
                                .entry(3, MarkerReplacementBlock.of(Blocks.GRAVEL))
                                .build())
                        .add(Tags.Biomes.IS_FOREST, MarkerReplacementRandom.builder()
                                .entry(1, MarkerReplacementLootContainer.suspiciousGravel(LootTablesAS.DIG_SITE_ARCHAEOLOGY))
                                .entry(2, MarkerReplacementBlock.of(Blocks.GRAVEL))
                                .entry(1, MarkerReplacementBlock.of(Blocks.DIRT))
                                .build())
                        .add(Tags.Biomes.IS_SAVANNA, MarkerReplacementRandom.builder()
                                .entry(1, MarkerReplacementLootContainer.suspiciousSand(LootTablesAS.DIG_SITE_ARCHAEOLOGY))
                                .entry(2, MarkerReplacementBlock.of(Blocks.SAND))
                                .entry(1, MarkerReplacementBlock.of(Blocks.DIRT))
                                .build())
                        .add(Tags.Biomes.IS_DESERT, MarkerReplacementRandom.builder()
                                .entry(1, MarkerReplacementLootContainer.suspiciousSand(LootTablesAS.DIG_SITE_ARCHAEOLOGY))
                                .entry(3, MarkerReplacementBlock.of(Blocks.SAND))
                                .build())
                        .build());
    });
    public static final StructureDefinition MOON_DIAL = definition("moon_dial", builder -> {
        builder.biomes(TagsAS.Biomes.MOON_DIAL_BIOMES)
                .spread(20, 16, 0xE7678316)
                .terrainAdjustment(TerrainAdjustment.BEARD_THIN)
                .rotation(Rotation.NONE);
    });
    public static final StructureDefinition COLUMN = definition("column", builder -> {
        builder.biomes(TagsAS.Biomes.COLUMN_BIOMES)
                .spread(24, 12, 0xE940CBA2)
                .heightmap(Heightmap.Types.OCEAN_FLOOR_WG)
                .terrainAdjustment(TerrainAdjustment.BEARD_THIN)
                .verticalOffset(-1)
                .marker("chest_bricks", MarkerReplacementRandom.builder()
                        .entry(1, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST))
                        .entry(3, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST_SMALL))
                        .entry(12, MarkerReplacementBlock.of(BlocksAS.MARBLE_BRICKS))
                        .build())
                .processors(ctx -> List.of(
                        FlareLightExtinguishProcessor.getInstance(),
                        FlareLightColorizationProcessor.getInstance()
                ));
    });
    public static final StructureDefinition ROTUNDA = definition("rotunda", builder -> {
        builder.biomes(TagsAS.Biomes.ROTUNDA_BIOMES)
                .structure("rotunda_1").structure("rotunda_2")
                .template("rotunda_1").template("rotunda_2")
                .spread(40, 32, 0x841D9E14)
                .spreadType(RandomSpreadType.TRIANGULAR)
                .terrainAdjustment(TerrainAdjustment.BEARD_BOX)
                .boundingBoxExpansion(1)
                .marker("chest", MarkerReplacementRandom.builder()
                        .entry(1, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST))
                        .entry(4, MarkerReplacementLootContainer.chest(LootTablesAS.SHRINE_CHEST_SMALL))
                        .entry(4, MarkerReplacementNothing.INSTANCE)
                        .build());
    });

    private static StructureDefinition definition(String name, Consumer<StructureDefinition.Builder> builderConsumer) {
        StructureDefinition.Builder builder = StructureDefinition.builder(name);
        builderConsumer.accept(builder);
        StructureDefinition definition = builder.build();
        ALL.add(definition);
        return definition;
    }

    public static void generateStructures(BootstrapContext<Structure> context) {
        ALL.forEach(definition -> definition.registerStructure(context));
    }

    public static void generateStructureSets(BootstrapContext<StructureSet> context) {
        ALL.forEach(definition -> definition.registerStructureSet(context));
    }

    public static void generateStructurePools(BootstrapContext<StructureTemplatePool> context) {
        ALL.forEach(definition -> definition.registerTemplatePool(context));
    }

    public static void generateStructureProcessorLists(BootstrapContext<StructureProcessorList> context) {
        ALL.forEach(definition -> definition.registerProcessorList(context));
    }
}
