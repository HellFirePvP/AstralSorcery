/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.config;

import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructurePlacementConfig
 * Created by HellFirePvP
 * Date: 23.07.2019 / 20:11
 */
public class StructurePlacementConfig extends FeaturePlacementConfig {

    private final int defaultStructureSize;
    private final int defaultStructureDistance;
    private final int defaultStructureSeparation;

    private ForgeConfigSpec.IntValue configStructureSize;
    private ForgeConfigSpec.IntValue configStructureDistance;
    private ForgeConfigSpec.IntValue configStructureSeparation;

    public StructurePlacementConfig(String featureName, int structureSize,
                                    List<BiomeDictionary.Type> applicableBiomeTypes,
                                    List<DimensionType> applicableDimensions,
                                    int minY, int maxY, int generationChance,
                                    int defaultStructureDistance, int defaultStructureSeparation) {
        super(featureName, true, true,
                applicableBiomeTypes, applicableDimensions,
                minY, maxY, generationChance, 1);
        this.defaultStructureSize = structureSize;
        this.defaultStructureDistance = defaultStructureDistance;
        this.defaultStructureSeparation = defaultStructureSeparation;
    }

    public int getStructureSize() {
        return this.configStructureSize.get();
    }

    public int getStructureDistance() {
        return this.configStructureDistance.get();
    }

    public int getStructureSeparation() {
        return this.configStructureSeparation.get();
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.configStructureSize = cfgBuilder
                .comment("Set this to the estimated structure size to be generated. Should match the structure's bigger width/length.")
                .translation(translationKey("structuresize"))
                .defineInRange("structureSize", this.defaultStructureSize, 1, 10_000);
        this.configStructureDistance = cfgBuilder
                .comment("Defines the average structure distance between two structures of this type")
                .translation(translationKey("structuredistance"))
                .defineInRange("structuredistance", this.defaultStructureDistance, 1, 150);
        this.configStructureSeparation = cfgBuilder
                .comment("Defines the average structure separation/position-shift between two structures of this type")
                .translation(translationKey("structureseparation"))
                .defineInRange("structureseparation", this.defaultStructureSeparation, 1, 100);
    }
}
