/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.world.config.ReplacingFeaturePlacementConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
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
 * Class: RiverbedPlacement
 * Created by HellFirePvP
 * Date: 25.07.2019 / 07:55
 */
public class RiverbedPlacement extends Placement<ReplacingFeaturePlacementConfig> {

    public RiverbedPlacement(Function<Dynamic<?>, ? extends ReplacingFeaturePlacementConfig> cfgSupplier) {
        super(cfgSupplier);
    }

    public RiverbedPlacement(ReplacingFeaturePlacementConfig config) {
        super(dyn -> config);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, ReplacingFeaturePlacementConfig configIn, BlockPos pos) {
        if (random.nextInt(Math.max(configIn.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }

        List<BlockPos> result = new ArrayList<>();
        for (int i = 0; i < configIn.getGenerationAmount(); i++) {
            BlockPos at = pos.add(random.nextInt(16), configIn.getRandomY(random), random.nextInt(16));

            if (!configIn.canPlace(worldIn, generatorIn.getBiomeProvider(), at, random)) {
                continue;
            }

            boolean foundWater = false;
            for (int yy = 0; yy < 3; yy++) {
                BlockPos check = at.offset(Direction.UP, yy);
                BlockState bs = worldIn.getBlockState(check);
                Block block = bs.getBlock();
                Fluid f;
                if ((f = MiscUtils.tryGetFuild(bs)) != null && f.isIn(FluidTags.WATER) || block.isIn(BlockTags.ICE)) {
                    foundWater = true;
                    at = check.down();
                    break;
                }
            }
            if (foundWater) {
                result.add(at);
            }
        }
        return result.stream();
    }
}
