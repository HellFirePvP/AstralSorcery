/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeAquamarine;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeGlowstoneFlower;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeMarble;
import hellfirepvp.astralsorcery.common.world.attributes.GenAttributeRockCrystals;
import hellfirepvp.astralsorcery.common.world.retrogen.ChunkVersionController;
import hellfirepvp.astralsorcery.common.world.structure.StructureAncientShrine;
import hellfirepvp.astralsorcery.common.world.structure.StructureDesertShrine;
import hellfirepvp.astralsorcery.common.world.structure.StructureSmallShrine;
import hellfirepvp.astralsorcery.common.world.structure.StructureTreasureShrine;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
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

    public static final int CURRENT_WORLD_GENERATOR_VERSION = 2;

    private List<WorldGenAttributeCommon> structures = new LinkedList<>();
    private List<WorldGenAttribute> decorators = new LinkedList<>();

    private List<WorldGenAttribute> worldGenAttributes = new LinkedList<>();

    public void pushConfigEntries() {
        structures.add(new StructureAncientShrine());
        structures.add(new StructureDesertShrine());
        structures.add(new StructureSmallShrine());
        structures.add(new StructureTreasureShrine());

        decorators.add(new GenAttributeGlowstoneFlower());
    }

    public AstralWorldGenerator setupAttributes() {
        if(Config.spawnRockCrystalOres) {
            decorators.add(new GenAttributeRockCrystals());
        }
        if(Config.marbleAmount > 0) {
            decorators.add(new GenAttributeMarble());
        }
        if(Config.aquamarineAmount > 0) {
            decorators.add(new GenAttributeAquamarine());
        }

        worldGenAttributes.addAll(decorators);
        worldGenAttributes.addAll(structures);
        return this;
    }

    public void handleRetroGen(World world, ChunkPos pos, Integer lastKnownChunkVersion) {
        ChunkVersionController.instance.setGenerationVersion(pos, CURRENT_WORLD_GENERATOR_VERSION);

        generateWithLastKnownVersion(pos.x, pos.z, world, lastKnownChunkVersion);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(world.getWorldType().equals(WorldType.FLAT)) return;
        if(!Config.worldGenDimWhitelist.contains(world.provider.getDimension())) return;

        ChunkVersionController.instance.setGenerationVersion(new ChunkPos(chunkX, chunkZ), CURRENT_WORLD_GENERATOR_VERSION);
        generateWithLastKnownVersion(chunkX, chunkZ, world, -1);

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
            if(attribute.attributeVersion > lastKnownChunkVersion) {
                rand.setSeed(chunkSeed);
                attribute.generate(rand, chunkX, chunkZ, world);
            }
        }
    }

}
