/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
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

    public static final Codec<StructurePlacementConfig> CODEC = RecordCodecBuilder.create((codecBuilder) -> codecBuilder.group(
            Codec.STRING.fieldOf("featureName").forGetter(cfg -> cfg.featureName),
            Codec.INT.fieldOf("structureSize").forGetter(cfg -> cfg.defaultStructureSize),
            Codec.BOOL.fieldOf("whitelistBiomeSpecification").forGetter(cfg -> cfg.defaultWhitelistBiomeSpecification),
            Codec.BOOL.fieldOf("whitelistDimensionTypeSpecification").forGetter(cfg -> cfg.defaultWhitelistDimensionSpecification),
            Codec.STRING.listOf().fieldOf("applicableBiomeCategories").forGetter(cfg -> cfg.defaultApplicableBiomeCategories),
            ResourceLocation.RESOURCE_LOCATION_CODEC.listOf().fieldOf("applicableDimensionTypes").forGetter(cfg -> cfg.defaultApplicableDimensionTypes),
            Codec.INT.fieldOf("minY").forGetter(cfg -> cfg.defaultMinY),
            Codec.INT.fieldOf("maxY").forGetter(cfg -> cfg.defaultMaxY),
            Codec.INT.fieldOf("generationChance").forGetter(cfg -> cfg.defaultGenerationChance),
            Codec.INT.fieldOf("structureSpacing").forGetter(cfg -> cfg.defaultStructureSpacing),
            Codec.INT.fieldOf("structureSeparation").forGetter(cfg -> cfg.defaultStructureSeparation))
                .apply(codecBuilder, StructurePlacementConfig::new));

    private final int defaultStructureSize;
    private final int defaultStructureSpacing;
    private final int defaultStructureSeparation;

    private ForgeConfigSpec.IntValue configStructureSize;
    private ForgeConfigSpec.IntValue configStructureSpacing;
    private ForgeConfigSpec.IntValue configStructureSeparation;

    public StructurePlacementConfig(String featureName, int structureSize,
                                    boolean defaultWhitelistBiomeSpecification,
                                    boolean defaultWhitelistDimensionSpecification,
                                    List<String> applicableBiomeCategories,
                                    List<ResourceLocation> applicableDimensionTypes,
                                    int minY, int maxY, int generationChance,
                                    int defaultStructureSpacing, int defaultStructureSeparation) {
        super(featureName, defaultWhitelistBiomeSpecification, defaultWhitelistDimensionSpecification,
                applicableBiomeCategories, applicableDimensionTypes,
                minY, maxY, generationChance, 1);
        this.defaultStructureSize = structureSize;
        this.defaultStructureSpacing = defaultStructureSpacing;
        this.defaultStructureSeparation = defaultStructureSeparation;
    }

    public int getStructureSize() {
        return this.configStructureSize.get();
    }

    public int getStructureSpacing() {
        return this.configStructureSpacing.get();
    }

    public int getStructureSeparation() {
        return this.configStructureSeparation.get();
    }

    public int getFeatureSalt() {
        return 0x5815931A ^ (this.featureName.hashCode() * 31);
    }

    public StructureSeparationSettings makeSpacingSettings() {
        return new DynamicStructureSpacing(this::getStructureSpacing, this::getStructureSeparation, this::getFeatureSalt);
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.configStructureSize = cfgBuilder
                .comment("Set this to the estimated structure size to be generated. Should match the structure's bigger width/length.")
                .translation(translationKey("structuresize"))
                .defineInRange("structureSize", this.defaultStructureSize, 1, 10_000);
        this.configStructureSpacing = cfgBuilder
                .comment("Defines the average structure distance between two structures of this type")
                .translation(translationKey("structurespacing"))
                .defineInRange("structurespacing", this.defaultStructureSpacing, 1, 150);
        this.configStructureSeparation = cfgBuilder
                .comment("Defines the average structure separation/position-shift between two structures of this type")
                .translation(translationKey("structureseparation"))
                .defineInRange("structureseparation", this.defaultStructureSeparation, 1, 100);
    }
}
