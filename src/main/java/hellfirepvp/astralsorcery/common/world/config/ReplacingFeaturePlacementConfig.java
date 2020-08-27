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
import hellfirepvp.astralsorcery.common.data.config.base.ConfiguredBlockStateList;
import hellfirepvp.astralsorcery.common.util.block.BlockStateList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReplacingFeaturePlacementConfig
 * Created by HellFirePvP
 * Date: 24.07.2019 / 22:13
 */
public class ReplacingFeaturePlacementConfig extends FeaturePlacementConfig {

    public static final Codec<ReplacingFeaturePlacementConfig> CODEC = RecordCodecBuilder.create(codecBuilder -> codecBuilder.group(
            Codec.STRING.fieldOf("featureName").forGetter(cfg -> cfg.featureName),
            Codec.BOOL.fieldOf("whitelistBiomeSpecification").forGetter(cfg -> cfg.defaultWhitelistBiomeSpecification),
            Codec.BOOL.fieldOf("whitelistDimensionTypeSpecification").forGetter(cfg -> cfg.defaultWhitelistDimensionSpecification),
            Codec.STRING.listOf().fieldOf("applicableBiomeCategories").forGetter(cfg -> cfg.defaultApplicableBiomeCategories),
            ResourceLocation.RESOURCE_LOCATION_CODEC.listOf().fieldOf("applicableDimensionTypes").forGetter(cfg -> cfg.defaultApplicableDimensionTypes),
            Codec.INT.fieldOf("minY").forGetter(cfg -> cfg.defaultMinY),
            Codec.INT.fieldOf("maxY").forGetter(cfg -> cfg.defaultMaxY),
            Codec.INT.fieldOf("generationChance").forGetter(cfg -> cfg.defaultGenerationChance),
            Codec.INT.fieldOf("generationAmount").forGetter(cfg -> cfg.defaultGenerationAmount),
            BlockStateList.CODEC.fieldOf("blockStates").forGetter(cfg -> cfg.defaultReplaceableBlockstates))
            .apply(codecBuilder, ReplacingFeaturePlacementConfig::new));

    private final BlockStateList defaultReplaceableBlockstates;
    private ConfiguredBlockStateList configReplaceableBlockstates;

    public ReplacingFeaturePlacementConfig(String featureName,
                                           boolean defaultWhitelistBiomeSpecification,
                                           boolean defaultWhitelistDimensionSpecification,
                                           List<String> defaultApplicableBiomeTypes,
                                           List<ResourceLocation> defaultApplicableDimensionTypes,
                                           int defaultMinY, int defaultMaxY, int defaultGenerationChance, int generationAmount,
                                           BlockStateList defaultReplaceableBlockstates) {
        super(featureName, defaultWhitelistBiomeSpecification, defaultWhitelistDimensionSpecification,
                defaultApplicableBiomeTypes, defaultApplicableDimensionTypes,
                defaultMinY, defaultMaxY, defaultGenerationChance, generationAmount);
        this.defaultReplaceableBlockstates = defaultReplaceableBlockstates;
    }

    @Override
    public boolean canPlace(ISeedReader world, BlockPos pos, Random rand) {
        if (!super.canPlace(world, pos, rand)) {
            return false;
        }
        return this.configReplaceableBlockstates.test(world.getBlockState(pos));
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.configReplaceableBlockstates = this.defaultReplaceableBlockstates.getAsConfig(
                cfgBuilder, "replaceable", translationKey("replaceable"),
                "List all blockstates here that this feature should be able to replace with its own blocks.");
    }
}
