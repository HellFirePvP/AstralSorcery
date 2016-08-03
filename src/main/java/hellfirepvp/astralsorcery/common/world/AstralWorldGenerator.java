package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AstralWorldGenerator
 * Created by HellFirePvP
 * Date: 07.05.2016 / 19:21
 */
public class AstralWorldGenerator implements IWorldGenerator {

    private WorldGenMinable marbleMineable;

    public AstralWorldGenerator init() {
        if(Config.marbleAmount > 0) {
            marbleMineable = new WorldGenMinable(
                    BlocksAS.blockMarble.getDefaultState().
                            withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW),
                    Config.marbleVeinSize);
        }
        return this;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 0) return;

        genResources(random, chunkX, chunkZ, world);
        if(Config.generateShrines && random.nextInt(Config.shrineGenerationChance) == 0) {
            genShrine(random, chunkX, chunkZ, world);
        }
    }

    private void genShrine(Random random, int chunkX, int chunkZ, World world) {
        int rX = (chunkX  * 16) + random.nextInt(16);
        int rZ = (chunkZ  * 16) + random.nextInt(16);
        int rY = world.getTopSolidOrLiquidBlock(new BlockPos(rX, 0, rZ)).getY();
        BlockPos central = new BlockPos(rX, rY, rZ);
        if(!isMountainBiome(world, central)) return;
        if(!canSpawnShrineCorner(world, central.add(0, 0, 7))) return;
        if(!canSpawnShrineCorner(world, central.add(0, 0, -7))) return;
        if(!canSpawnShrineCorner(world, central.add( 7, 0, -7))) return;
        if(!canSpawnShrineCorner(world, central.add(-7, 0, -7))) return;
        MultiBlockArrays.ancientShrine.placeInWorld(world, central);
    }

    private boolean canSpawnShrineCorner(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos).getY();
        return Math.abs(dY - pos.getY()) <= 3 && isMountainBiome(world, pos);
    }

    private boolean isMountainBiome(World world, BlockPos pos) {
        Biome b = world.getBiomeGenForCoords(pos);
        BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(b);
        if(types == null || types.length == 0) return false;
        boolean mountain = false;
        for (BiomeDictionary.Type t : types) {
            if(t.equals(BiomeDictionary.Type.MOUNTAIN)) mountain = true;
        }
        return mountain;
    }

    private void genResources(Random random, int chunkX, int chunkZ, World world) {
        genCrystals(random, chunkX, chunkZ, world);
        if(marbleMineable != null) {
            for (int i = 0; i < Config.marbleAmount; i++) {
                int rX = (chunkX  * 16) + random.nextInt(16);
                int rY = 50 + random.nextInt(10);
                int rZ = (chunkZ  * 16) + random.nextInt(16);
                BlockPos pos = new BlockPos(rX, rY, rZ);
                marbleMineable.generate(world, random, pos);
            }
        }
    }

    private void genCrystals(Random random, int chunkX, int chunkZ, World world) {
        if (Config.crystalDensity <= 0 || random.nextInt(Config.crystalDensity) == 0) {
            int xPos = (chunkX << 4) + random.nextInt(16);
            int zPos = (chunkZ << 4) + random.nextInt(16);
            int yPos = 2 + random.nextInt(4);
            BlockPos pos = new BlockPos(xPos, yPos, zPos);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock().equals(Blocks.STONE)) {
                BlockStone.EnumType stoneType = state.getValue(BlockStone.VARIANT);
                if (stoneType != null && stoneType.equals(BlockStone.EnumType.STONE)) {
                    IBlockState newState = BlocksAS.customOre.getDefaultState().withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.ROCK_CRYSTAL);
                    world.setBlockState(pos, newState);
                    RockCrystalBuffer buf = WorldCacheManager.getData(world, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
                    buf.addOre(pos);
                }
            }
        }
    }

}
