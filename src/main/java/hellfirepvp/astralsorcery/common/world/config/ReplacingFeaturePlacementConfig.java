/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.config;

import hellfirepvp.astralsorcery.common.data.config.base.ConfiguredBlockStateList;
import hellfirepvp.astralsorcery.common.util.block.BlockStateList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.BiomeDictionary;
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

    private final BlockStateList defaultReplaceableBlockstates;
    private ConfiguredBlockStateList configReplaceableBlockstates;

    public ReplacingFeaturePlacementConfig(String featureName, boolean defaultWhitelistBiomeSpecification, boolean defaultWhitelistDimensionSpecification,
                                           List<BiomeDictionary.Type> defaultApplicableBiomeTypes, List<DimensionType> defaultApplicableDimensions,
                                           int defaultMinY, int defaultMaxY, int defaultGenerationChance, int generationAmount,
                                           BlockStateList defaultReplaceableBlockstates) {
        super(featureName, defaultWhitelistBiomeSpecification, defaultWhitelistDimensionSpecification,
                defaultApplicableBiomeTypes, defaultApplicableDimensions,
                defaultMinY, defaultMaxY, defaultGenerationChance, generationAmount);
        this.defaultReplaceableBlockstates = defaultReplaceableBlockstates;
    }

    @Override
    public boolean canPlace(IWorld iWorld, BiomeProvider biomeProvider, BlockPos pos, Random rand) {
        if (!super.canPlace(iWorld, biomeProvider, pos, rand)) {
            return false;
        }
        return this.configReplaceableBlockstates.test(iWorld.getBlockState(pos));
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.configReplaceableBlockstates = this.defaultReplaceableBlockstates.getAsConfig(
                cfgBuilder, "replaceable", translationKey("replaceable"),
                "List all blockstates here that this feature should be able to replace with its own blocks.");
    }
}
