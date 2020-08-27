/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.world.config.StructurePlacementConfig;
import hellfirepvp.astralsorcery.common.world.placement.StructurePlacement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfiguredStructureStart
 * Created by HellFirePvP
 * Date: 07.05.2020 / 16:16
 */
public abstract class ConfiguredStructureStart<FC extends IFeatureConfig> extends StructureStart<FC> {

    private StructurePlacement<?> placementStrategy;

    public ConfiguredStructureStart(Structure<FC> structure, int chunkX, int chunkZ, MutableBoundingBox structureBox, int referenceIn, long seed) {
        super(structure, chunkX, chunkZ, structureBox, referenceIn, seed);
    }

    public ConfiguredStructureStart<FC> setPlacementStrategy(StructurePlacement<?> placementStrategy) {
        this.placementStrategy = placementStrategy;
        return this;
    }

    public static interface IConfiguredStartFactory {

        ConfiguredStructureStart<NoFeatureConfig> create(Structure<NoFeatureConfig> structure, int chunkX, int chunkZ, MutableBoundingBox structureBox, int referenceIn, long seed);

    }
}
