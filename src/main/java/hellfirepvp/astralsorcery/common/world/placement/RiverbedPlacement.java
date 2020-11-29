/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RiverbedPlacement
 * Created by HellFirePvP
 * Date: 20.11.2020 / 17:10
 */
public class RiverbedPlacement extends Placement<NoPlacementConfig> {

    public RiverbedPlacement() {
        super(NoPlacementConfig.CODEC);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, NoPlacementConfig config, BlockPos pos) {
        int x = rand.nextInt(16) + pos.getX();
        int z = rand.nextInt(16) + pos.getZ();
        int y = helper.func_242893_a(Heightmap.Type.OCEAN_FLOOR_WG, x, z);
        if (y <= 0) {
            return Stream.of();
        }

        BlockPos floor = new BlockPos(x, y - 4, z);

        boolean foundWater = false;
        for (int yy = 0; yy < 5; yy++) {
            BlockPos check = floor.offset(Direction.UP, yy);
            BlockState state = helper.func_242894_a(check);
            Block block = state.getBlock();
            Fluid f;
            if ((f = MiscUtils.tryGetFuild(state)) != null && f.isIn(FluidTags.WATER) || block.isIn(BlockTags.ICE)) {
                foundWater = true;
                floor = check.down();
                break;
            }
        }
        if (foundWater && BlockTags.SAND.contains(helper.func_242894_a(floor).getBlock())) {
            return Stream.of(floor);
        }
        return Stream.of();
    }
}
