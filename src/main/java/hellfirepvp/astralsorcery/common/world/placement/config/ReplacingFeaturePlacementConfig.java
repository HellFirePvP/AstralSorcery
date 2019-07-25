/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement.config;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.util.block.BlockStateHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ReplacingFeaturePlacementConfig
 * Created by HellFirePvP
 * Date: 24.07.2019 / 22:13
 */
public class ReplacingFeaturePlacementConfig extends FeaturePlacementConfig {

    private final List<BlockState> defaultReplaceableBlockstates;

    private ForgeConfigSpec.ConfigValue<List<String>> configReplaceableBlockstates;
    private List<BlockState> replaceableBlockStates = null;

    public ReplacingFeaturePlacementConfig(String featureName, boolean defaultWhitelistBiomeSpecification, boolean defaultWhitelistDimensionSpecification,
                                           List<BiomeDictionary.Type> defaultApplicableBiomeTypes, List<Integer> defaultApplicableDimensions,
                                           int defaultMinY, int defaultMaxY, int defaultGenerationChance, int generationAmount,
                                           List<BlockState> defaultReplaceableBlockstates) {
        super(featureName, defaultWhitelistBiomeSpecification, defaultWhitelistDimensionSpecification,
                defaultApplicableBiomeTypes, defaultApplicableDimensions,
                defaultMinY, defaultMaxY, defaultGenerationChance, generationAmount);
        this.defaultReplaceableBlockstates = defaultReplaceableBlockstates;
    }

    public List<BlockState> getReplaceableBlockStates() {
        if (this.replaceableBlockStates == null) {
            this.replaceableBlockStates = convertBlockStates();
        }
        return this.replaceableBlockStates;
    }

    @Override
    public boolean canPlace(IWorld iWorld, BlockPos pos, Random rand) {
        if (!super.canPlace(iWorld, pos, rand)) {
            return false;
        }
        BlockState atState = iWorld.getBlockState(pos);
        return this.getReplaceableBlockStates().contains(atState);
    }

    private List<BlockState> convertBlockStates() {
        List<BlockState> states = Lists.newArrayList();
        for (String str : this.configReplaceableBlockstates.get()) {
            BlockState state = BlockStateHelper.deserialize(str);
            if (!states.contains(state)) {
                states.add(state);
            }
        }
        return states;
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        super.createEntries(cfgBuilder);

        this.configReplaceableBlockstates = cfgBuilder
                .comment("List all blockstates here that this feature should be able to replace with its own blocks")
                .translation("config.world.generation.replaceable")
                .define("replaceable", this.defaultReplaceableBlockstates.stream()
                        .map(BlockStateHelper::serialize)
                        .collect(Collectors.toList()));
    }
}
