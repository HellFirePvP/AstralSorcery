/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure.feature;

import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.placement.StructurePlacement;
import hellfirepvp.astralsorcery.common.world.structure.ConfiguredStructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.ScatteredStructure;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfiguredStructureFeature
 * Created by HellFirePvP
 * Date: 07.05.2020 / 08:18
 */
public abstract class ConfiguredStructureFeature extends ScatteredStructure<NoFeatureConfig> {

    private final StructurePlacementConfig placementConfig;
    private final StructurePlacement<?> placement;

    public ConfiguredStructureFeature(StructurePlacementConfig placementConfig) {
        super(NoFeatureConfig::deserialize);
        this.placementConfig = placementConfig;
        this.placement = this.createPlacement(placementConfig);
    }

    protected StructurePlacement<?> createPlacement(StructurePlacementConfig placementConfig) {
        return new StructurePlacement<>(dyn -> placementConfig);
    }

    protected IStartFactory configuredStart(ConfiguredStructureStart.IConfiguredStartFactory factory) {
        return (structure, chunkX, chunkZ, structureBoundingBox, reference, seed) -> {
            ConfiguredStructureStart start = factory.create(structure, chunkX, chunkZ, structureBoundingBox, reference, seed);
            start.setPlacementStrategy(ConfiguredStructureFeature.this.placement);
            return start;
        };
    }

    @Override
    protected int getSeedModifier() {
        return 0x5815931A ^ (this.getStructureName().hashCode() * 31);
    }

    @Override
    protected int getBiomeFeatureSeparation(ChunkGenerator<?> chunkGenerator) {
        return this.getPlacementConfig().getStructureSeparation();
    }

    @Override
    protected int getBiomeFeatureDistance(ChunkGenerator<?> chunkGenerator) {
        return this.getPlacementConfig().getStructureDistance();
    }

    @Override
    public boolean canBeGenerated(BiomeManager biomeManagerIn, ChunkGenerator<?> chunkGenerator, Random rand, int chunkX, int chunkZ, Biome biomeIn) {
        ChunkPos chunkpos = this.getStartPositionForPosition(chunkGenerator, rand, chunkX, chunkZ, 0, 0);
        if (chunkX == chunkpos.x && chunkZ == chunkpos.z) {
            BlockPos at = new BlockPos(chunkX * 16 + 9, 0, chunkZ * 16 + 9);
            int yLevel = chunkGenerator.func_222531_c(at.getX(), at.getZ(), Heightmap.Type.OCEAN_FLOOR_WG);
            at = new BlockPos(at.getX(), yLevel, at.getZ());
            Biome biome = biomeManagerIn.getBiome(at);

            return chunkGenerator.hasStructure(biome, this) &&
                    this.getPlacementConfig().canPlace(chunkGenerator.world, chunkGenerator.getBiomeProvider(), at, rand);
        }

        return false;
    }

    public StructurePlacementConfig getPlacementConfig() {
        return this.placementConfig;
    }

    @Override
    public int getSize() {
        return this.getPlacementConfig().getStructureSize();
    }
}
