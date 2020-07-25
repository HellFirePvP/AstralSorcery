/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.world.config.ReplacingFeaturePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.placement.Placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RandomReplaceablePlacement
 * Created by HellFirePvP
 * Date: 24.07.2019 / 22:32
 */
public class RandomReplaceablePlacement extends Placement<ReplacingFeaturePlacementConfig> {

    public RandomReplaceablePlacement(Function<Dynamic<?>, ? extends ReplacingFeaturePlacementConfig> cfgSupplier) {
        super(cfgSupplier);
    }

    public RandomReplaceablePlacement(ReplacingFeaturePlacementConfig config) {
        super(dyn -> config);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, ReplacingFeaturePlacementConfig configIn, BlockPos pos) {
        if (!configIn.generatesInBiome(worldIn.getBiome(pos))) {
            return Stream.empty();
        }
        if (random.nextInt(Math.max(configIn.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }

        List<BlockPos> result = new ArrayList<>();

        BlockPos at = pos.add(random.nextInt(16), 0, random.nextInt(16));
        at = new BlockPos(at.getX(), configIn.getRandomY(random), at.getZ());

        if (configIn.canPlace(worldIn, generatorIn.getBiomeProvider(), at, random)) {
            result.add(at);
        }

        int amt = configIn.getGenerationAmount();
        while (amt > 0) {
            amt--;

            BlockPos offset = at.add(-1 + random.nextInt(3),
                    -1 + random.nextInt(3),
                    -1 + random.nextInt(3));
            if (!offset.equals(at) &&
                    worldIn.chunkExists(offset.getX() >> 4, offset.getZ() >> 4) &&
                    configIn.canPlace(worldIn, generatorIn.getBiomeProvider(), offset, random)) {
                result.add(offset);
            }
        }

        return result.stream();
    }
}
