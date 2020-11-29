/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.placement.config.ChanceConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.SimplePlacement;

import java.util.Random;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ChancePlacement
 * Created by HellFirePvP
 * Date: 19.11.2020 / 22:45
 */
public class ChancePlacement extends SimplePlacement<ChanceConfig> {

    public ChancePlacement() {
        super(ChanceConfig.CODEC);
    }

    public ConfiguredPlacement<ChanceConfig> withChance(float chance) {
        return this.configure(new ChanceConfig(chance));
    }

    @Override
    protected Stream<BlockPos> getPositions(Random random, ChanceConfig config, BlockPos pos) {
        return config.test(random) ? Stream.of(pos) : Stream.empty();
    }
}
