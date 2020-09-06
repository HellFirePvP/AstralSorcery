/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.world.config.FeaturePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleCountRangePlacement
 * Created by HellFirePvP
 * Date: 22.08.2020 / 17:31
 */
public class SimpleCountRangePlacement extends Placement<FeaturePlacementConfig> {

    public SimpleCountRangePlacement(Function<Dynamic<?>, ? extends FeaturePlacementConfig> cfgSupploer) {
        super(cfgSupploer);
    }

    public SimpleCountRangePlacement(FeaturePlacementConfig config) {
        super(dyn -> config);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, FeaturePlacementConfig configIn, BlockPos pos) {
        if (!configIn.canGenerateAtAll() || random.nextInt(Math.max(configIn.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }
        return IntStream.range(0, configIn.getGenerationAmount()).mapToObj((count) -> {
            BlockPos at = pos.add(random.nextInt(16), configIn.getRandomY(random), random.nextInt(16));
            if (!configIn.canPlace(worldIn, generatorIn.getBiomeProvider(), at, random)) {
                return null;
            }
            return at;
        }).filter(Objects::nonNull);
    }
}
