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
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConfiguredStructure
 * Created by HellFirePvP
 * Date: 07.05.2020 / 08:18
 */
public abstract class ConfiguredStructure extends Structure<NoFeatureConfig> {

    private final StructurePlacementConfig placementConfig;
    private final StructurePlacement<?> placement;

    public ConfiguredStructure(StructurePlacementConfig placementConfig) {
        super(NoFeatureConfig.field_236558_a_);
        this.placementConfig = placementConfig;
        this.placement = this.createPlacement(placementConfig);
    }

    protected StructurePlacement<?> createPlacement(StructurePlacementConfig placementConfig) {
        return new StructurePlacement<>(placementConfig);
    }

    protected IStartFactory<NoFeatureConfig> configuredStart(ConfiguredStructureStart.IConfiguredStartFactory factory) {
        return (structure, chunkX, chunkZ, structureBoundingBox, reference, seed) -> {
            ConfiguredStructureStart<NoFeatureConfig> start = factory.create(structure, chunkX, chunkZ, structureBoundingBox, reference, seed);
            start.setPlacementStrategy(ConfiguredStructure.this.placement);
            return start;
        };
    }

    public StructurePlacementConfig getPlacementConfig() {
        return this.placementConfig;
    }
}
