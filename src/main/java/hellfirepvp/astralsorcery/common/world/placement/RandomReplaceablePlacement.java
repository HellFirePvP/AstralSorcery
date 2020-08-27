/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.config.ReplacingFeaturePlacementConfig;
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
 * Class: RandomReplaceablePlacement
 * Created by HellFirePvP
 * Date: 24.07.2019 / 22:32
 */
public class RandomReplaceablePlacement extends Placement<ReplacingFeaturePlacementConfig> {

    private final ReplacingFeaturePlacementConfig config;

    public RandomReplaceablePlacement(ReplacingFeaturePlacementConfig config) {
        super(ReplacingFeaturePlacementConfig.CODEC);
        this.config = config;
    }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper world, Random random, ReplacingFeaturePlacementConfig config, BlockPos pos) {
        if (!this.config.canGenerateAtAll() || random.nextInt(Math.max(this.config.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }

        List<BlockPos> result = new ArrayList<>();

        BlockPos at = pos.add(random.nextInt(16), 0, random.nextInt(16));
        at = new BlockPos(at.getX(), this.config.getRandomY(random), at.getZ());

        if (this.config.canPlace(world.field_242889_a, at, random)) {
            result.add(at);
        }

        int amt = this.config.getGenerationAmount();
        while (amt > 0) {
            amt--;

            BlockPos offset = at.add(-1 + random.nextInt(3),
                    -1 + random.nextInt(3),
                    -1 + random.nextInt(3));
            if (!offset.equals(at) && this.config.canPlace(world.field_242889_a, offset, random)) {
                result.add(offset);
            }
        }

        return result.stream();
    }
}
