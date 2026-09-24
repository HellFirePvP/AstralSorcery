/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.placement;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.lib.WorldGenAS;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RiverbedPlacement
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RiverbedPlacement extends PlacementModifier {

    public static final MapCodec<RiverbedPlacement> CODEC = MapCodec.unit(RiverbedPlacement::new);

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        BlockPos genPosition = pos.below();
        if (!context.getBlockState(genPosition).is(BlockTags.SAND)) {
            return Stream.empty();
        }
        boolean hasWater = false;
        for (int i = 1; i < 8; i++) {
            BlockState above = context.getBlockState(pos.above(i));
            if (above.getFluidState().is(FluidTags.WATER) || above.is(BlockTags.ICE)) {
                hasWater = true;
            }
            if (hasWater && above.is(Blocks.AIR)) {
                return Stream.of(genPosition);
            }
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return WorldGenAS.RIVERBED_PLACEMENT.get();
    }
}
