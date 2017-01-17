/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.ChunkVersionBuffer;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeAquamarine;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeMarble;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeRockCrystals;
import hellfirepvp.astralsorcery.common.world.structure.StructureAncientShrine;
import hellfirepvp.astralsorcery.common.world.structure.StructureDesertShrine;
import hellfirepvp.astralsorcery.common.world.structure.StructureSmallShrine;
import hellfirepvp.astralsorcery.common.world.structure.WorldGenAttributeStructure;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralWorldGenerator
 * Created by HellFirePvP
 * Date: 07.05.2016 / 19:21
 */
public class AstralWorldGenerator implements IWorldGenerator {

    public static final int CURRENT_WORLD_GENERATOR_VERSION = 0;

    private List<WorldGenAttributeStructure> structures = new LinkedList<>();

    private List<WorldGenAttribute> worldGenAttributes = new LinkedList<>();

    public ChunkVersionBuffer getVersionBuffer(World world) {
        return WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.CHUNK_VERSIONING);
    }

    public void pushConfigEntries() {
        structures.add(new StructureAncientShrine());
        structures.add(new StructureDesertShrine());
        structures.add(new StructureSmallShrine());
    }

    public AstralWorldGenerator setupAttributes() {
        if(Config.spawnRockCrystalOres) {
            worldGenAttributes.add(new GenAttributeRockCrystals());
        }
        if(Config.marbleAmount > 0) {
            worldGenAttributes.add(new GenAttributeMarble());
        }
        if(Config.aquamarineAmount > 0) {
            worldGenAttributes.add(new GenAttributeAquamarine());
        }
        worldGenAttributes.addAll(structures);
        return this;
    }

    public void handleRetroGen(World world, ChunkPos pos, Integer lastKnownChunkVersion) {
        getVersionBuffer(world).markChunkGeneration(pos);

        generateWithLastKnownVersion(pos.chunkXPos, pos.chunkZPos, world, lastKnownChunkVersion);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(world.getWorldType().equals(WorldType.FLAT)) return;

        if(Config.enableChunkVersioning) {
            getVersionBuffer(world).markChunkGeneration(new ChunkPos(chunkX, chunkZ));
        }
        generateWithLastKnownVersion(chunkX, chunkZ, world, 0);

        /*for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                BlockPos pos = new BlockPos((chunkX * 16) + xx, 0, (chunkZ * 16) + zz);
                float distr = SkyNoiseCalculator.getSkyNoiseDistribution(world, pos);
                int y = (int) (35 + distr * 40);
                world.setBlockState(new BlockPos(pos.getX(), y, pos.getZ()), Blocks.GLASS.getDefaultState(), 2);
            }
        }*/
    }

    private void generateWithLastKnownVersion(int chunkX, int chunkZ, World world, int lastKnownChunkVersion) {
        long worldSeed = world.getSeed();
        Random rand = new Random(worldSeed);
        long xSeed = rand.nextLong() >> 2 + 1L;
        long zSeed = rand.nextLong() >> 2 + 1L;
        long chunkSeed = (xSeed * chunkX + zSeed * chunkZ) ^ worldSeed;

        for (WorldGenAttribute attribute : worldGenAttributes) {
            if(attribute.attributeVersion >= lastKnownChunkVersion) {
                rand.setSeed(chunkSeed);
                attribute.generate(rand, chunkX, chunkZ, world);
            }
        }
    }

}
