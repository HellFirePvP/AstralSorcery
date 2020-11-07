/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.config.FeaturePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import java.util.Objects;
import java.util.Random;
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

    private final FeaturePlacementConfig config;

    public SimpleCountRangePlacement(FeaturePlacementConfig config) {
        super(FeaturePlacementConfig.CODEC);
        this.config = config;
    }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper world, Random random, FeaturePlacementConfig cfgIn, BlockPos pos) {
        if (!this.config.canGenerateAtAll() || random.nextInt(Math.max(this.config.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }
        return IntStream.range(0, this.config.getGenerationAmount()).mapToObj((count) -> {
            BlockPos at = pos.add(random.nextInt(16), this.config.getRandomY(random), random.nextInt(16));
            if (!this.config.canPlace(world.field_242889_a, at, random)) {
                return null;
            }
            return at;
        }).filter(Objects::nonNull);
    }
}
