/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureFinder
 * Created by HellFirePvP
 * Date: 02.06.2019 / 11:07
 */
public class StructureFinder {

    private StructureFinder() {}

    @Nullable
    public static BlockPos tryFindClosestStructure(ServerWorld world, BlockPos playerPos, Structure<?> structure, int searchRadius) {
        ChunkGenerator<?> gen = world.getChunkProvider().getChunkGenerator();
        try {
            return structure.findNearest(world, gen, playerPos, searchRadius, true);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    public static BlockPos tryFindClosestBiomeType(ServerWorld world, BlockPos playerPos, BiomeDictionary.Type biomeType, int searchRadius) {
        List<Biome> fitting = Lists.newArrayList(BiomeDictionary.getBiomes(biomeType));
        if (fitting.isEmpty()) {
            return null;
        }
        BiomeProvider gen = world.getChunkProvider().getChunkGenerator().getBiomeProvider();
        for (int reach = 64; reach < searchRadius; reach += 128) {
            BlockPos closest = gen.func_225531_a_(playerPos.getX(), playerPos.getY(), playerPos.getZ(), reach, fitting, new Random(world.getSeed()));
            if (closest != null) {
                return closest;
            }
        }
        return null;
    }

}
