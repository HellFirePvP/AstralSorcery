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
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RandomFlowerPlacement
 * Created by HellFirePvP
 * Date: 24.07.2019 / 20:01
 */
public class RandomFlowerPlacement extends Placement<FeaturePlacementConfig> {

    private final FeaturePlacementConfig config;

    public RandomFlowerPlacement(FeaturePlacementConfig config) {
        super(FeaturePlacementConfig.CODEC);
        this.config = config;
    }

    @Override
    public Stream<BlockPos> func_241857_a(WorldDecoratingHelper decoratingHelper, Random random, FeaturePlacementConfig config, BlockPos pos) {
        if (!this.config.canGenerateAtAll() || random.nextInt(Math.max(this.config.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }
        Set<BlockPos> result = new HashSet<>();
        for (int i = 0; i < this.config.getGenerationAmount(); i++) {
            BlockPos at = pos.add(random.nextInt(16), 0, random.nextInt(16));
            int y = decoratingHelper.func_242893_a(Heightmap.Type.WORLD_SURFACE_WG, at.getX(), at.getZ());
            at = new BlockPos(at.getX(), y, at.getZ());
            if (this.config.canPlace(decoratingHelper.field_242889_a, at, random)) {
                result.add(at);
            }
        }
        return result.stream();
    }
}
