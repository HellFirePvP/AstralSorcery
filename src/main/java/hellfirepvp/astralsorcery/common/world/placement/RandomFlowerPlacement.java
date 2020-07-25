/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.placement;

import com.mojang.datafixers.Dynamic;
import hellfirepvp.astralsorcery.common.world.config.FeaturePlacementConfig;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.Placement;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RandomFlowerPlacement
 * Created by HellFirePvP
 * Date: 24.07.2019 / 20:01
 */
public class RandomFlowerPlacement extends Placement<FeaturePlacementConfig> {

    public RandomFlowerPlacement(Function<Dynamic<?>, ? extends FeaturePlacementConfig> cfgSupplier) {
        super(cfgSupplier);
    }

    public RandomFlowerPlacement(FeaturePlacementConfig config) {
        super(dyn -> config);
    }

    @Override
    public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, FeaturePlacementConfig configIn, BlockPos pos) {
        if (random.nextInt(Math.max(configIn.getGenerationChance(), 1)) != 0) {
            return Stream.empty();
        }
        Set<BlockPos> result = new HashSet<>();
        for (int i = 0; i < configIn.getGenerationAmount(); i++) {
            if (!configIn.generatesInBiome(worldIn.getBiome(pos))) {
                return Stream.empty();
            }

            BlockPos at = pos.add(random.nextInt(16), 0, random.nextInt(16));
            at = worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, at);
            if (configIn.canPlace(worldIn, generatorIn.getBiomeProvider(), at, random)) {
                result.add(at);
            }
        }
        return result.stream();
    }
}
