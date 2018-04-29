/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureFinder
 * Created by HellFirePvP
 * Date: 25.02.2018 / 15:29
 */
public class StructureFinder {

    private StructureFinder() {}

    public static final String STRUCT_VILLAGE = "Village";
    public static final String STRUCT_STRONGHOLD = "Stronghold";
    public static final String STRUCT_MASNION = "Mansion";
    public static final String STRUCT_MONUMENT = "Monument";
    public static final String STRUCT_MINESHAFT = "Mineshaft";
    public static final String STRUCT_TEMPLE = "Temple";
    public static final String STRUCT_ENDCITY = "EndCity";
    public static final String STRUCT_FORTRESS = "Fortress";

    @Nullable
    public static BlockPos tryFindClosestAstralSorceryStructure(WorldServer world, BlockPos playerPos, StructureGenBuffer.StructureType searchKey) {
        StructureGenBuffer buffer = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_GEN);
        return buffer.getClosest(searchKey, playerPos);
    }

    @Nullable
    public static BlockPos tryFindClosestVanillaStructure(WorldServer world, BlockPos playerPos, String searchKey) {
        IChunkGenerator gen = world.getChunkProvider().chunkGenerator;
        if(gen == null) return null;
        try {
            return gen.getNearestStructurePos(world, searchKey, playerPos, true);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Nullable
    public static BlockPos tryFindClosestBiomeType(WorldServer world, BlockPos playerPos, BiomeDictionary.Type biomeType) {
        List<Biome> fitting = Lists.newArrayList(BiomeDictionary.getBiomes(biomeType));
        if(fitting.isEmpty()) {
            return null;
        }
        BiomeProvider gen = world.getBiomeProvider();
        for (int reach = 64; reach < 4160; reach += 128) {
            BlockPos closest = gen.findBiomePosition(playerPos.getX(), playerPos.getZ(), reach, fitting, new Random(world.getSeed()));
            if(closest != null) {
                return closest;
            }
        }
        return null;
    }

}
