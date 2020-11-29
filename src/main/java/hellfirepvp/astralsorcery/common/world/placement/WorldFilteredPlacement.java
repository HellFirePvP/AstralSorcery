/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import hellfirepvp.astralsorcery.common.world.placement.config.WorldFilterConfig;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.Placement;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldFilteredPlacement
 * Created by HellFirePvP
 * Date: 20.11.2020 / 15:52
 */
public class WorldFilteredPlacement extends Placement<WorldFilterConfig> {

    public WorldFilteredPlacement() {
        super(WorldFilterConfig.CODEC);
    }

    public ConfiguredPlacement<WorldFilterConfig> inWorlds(boolean ignoreFilter, List<RegistryKey<World>> worlds) {
        return inWorlds(() -> ignoreFilter, () -> worlds);
    }

    public ConfiguredPlacement<WorldFilterConfig> inWorlds(Supplier<Boolean> ignoreFilter, Supplier<List<RegistryKey<World>>> worlds) {
        return this.configure(new WorldFilterConfig(ignoreFilter, worlds));
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, WorldFilterConfig config, BlockPos pos) {
        if (config.generatesIn(helper.field_242889_a)) {
            return Stream.of(pos);
        }
        return Stream.empty();
    }
}
