/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.world.config.ReplacingFeaturePlacementConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RiverbedPlacement
 * Created by HellFirePvP
 * Date: 25.07.2019 / 07:55
 */
public class RiverbedPlacement extends Placement<ReplacingFeaturePlacementConfig> {

    private final ReplacingFeaturePlacementConfig config;

    public RiverbedPlacement(ReplacingFeaturePlacementConfig config) {
        super(ReplacingFeaturePlacementConfig.CODEC);
        this.config = config;
    }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper world, Random random, ReplacingFeaturePlacementConfig cfgIn, BlockPos pos) {
        if (!this.config.canGenerateAtAll() || random.nextInt(Math.max(this.config.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }

        List<BlockPos> result = new ArrayList<>();
        for (int i = 0; i < this.config.getGenerationAmount(); i++) {
            BlockPos at = pos.add(random.nextInt(16), this.config.getRandomY(random), random.nextInt(16));

            if (!this.config.canPlace(world.field_242889_a, at, random)) {
                continue;
            }

            boolean foundWater = false;
            for (int yy = 0; yy < 3; yy++) {
                BlockPos check = at.offset(Direction.UP, yy);
                BlockState bs = world.func_242894_a(check);
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
