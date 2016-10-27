package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.block.BlockCustomOre;
import hellfirepvp.astralsorcery.common.block.BlockCustomSandOre;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.WorldStructureEntry;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.world.structure.StructureAncientShrine;
import hellfirepvp.astralsorcery.common.world.structure.StructureDesertShrine;
import hellfirepvp.astralsorcery.common.world.structure.WorldGenAttributeStructure;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeRiver;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
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

    private WorldGenMinable marbleMineable;

    private List<WorldGenAttributeStructure> structures = new LinkedList<>();

    public void pushConfigEntries() {
        structures.add(new StructureAncientShrine());
        structures.add(new StructureDesertShrine());
    }

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

        if(world.getWorldType() != WorldType.FLAT) {
            for (WorldGenAttributeStructure struct : structures) {
                if(struct.canGenerate(chunkX, chunkZ, world, random)) {
                    BlockPos pos = struct.getGenerationPosition(chunkX, chunkZ, world, random);
                    if(struct.fulfillsSpecificConditions(pos, world, random)) {
                        struct.generate(pos, world, random);
                    }
                }
            }
        }
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
        if(Config.aquamarineAmount > 0) {
            for (int i = 0; i < Config.aquamarineAmount; i++) {
                int rX = (chunkX  * 16) + random.nextInt(16);
                int rY = 57 + random.nextInt(7);
                int rZ = (chunkZ  * 16) + random.nextInt(16);
                BlockPos pos = new BlockPos(rX, rY, rZ);
                IBlockState stateAt = world.getBlockState(pos);
                if(!stateAt.getBlock().equals(Blocks.SAND)) {
                    continue;
                }
                Biome at = world.getBiomeGenForCoords(pos);
                if(at != null) {
                    BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(at);
                    if(types != null) {
                        boolean found = false;
                        for (BiomeDictionary.Type t : types) {
                            if(t != null && t.equals(BiomeDictionary.Type.RIVER)) found = true;
                        }
                        if(!found)
                            continue;
                    } else {
                        if(!(at instanceof BiomeRiver))
                            continue;
                    }

                    /*boolean foundWater = false;
                    for (int yy = 0; yy < 2; yy++) {
                        BlockPos check = pos.offset(EnumFacing.UP, yy);
                        IBlockState bs = world.getBlockState(check);
                        Block block = bs.getBlock();
                        if(block instanceof BlockLiquid && bs.getMaterial() == Material.WATER) {
                            foundWater = true;
                            break;
                        }
                    }
                    if(!foundWater)
                        continue;*/

                    world.setBlockState(pos, BlocksAS.customSandOre.getDefaultState()
                            .withProperty(BlockCustomSandOre.ORE_TYPE, BlockCustomSandOre.OreType.AQUAMARINE));
                }
            }
        }
    }

    private void genCrystals(Random random, int chunkX, int chunkZ, World world) {
        if (Config.crystalDensity <= 0 || random.nextInt(Config.crystalDensity) == 0) {
            int xPos = chunkX * 16 + random.nextInt(16);
            int zPos = chunkZ * 16 + random.nextInt(16);
            int yPos = 2 + random.nextInt(4);
            BlockPos pos = new BlockPos(xPos, yPos, zPos);
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock().equals(Blocks.STONE)) {
                BlockStone.EnumType stoneType = state.getValue(BlockStone.VARIANT);
                if (stoneType != null && stoneType.equals(BlockStone.EnumType.STONE)) {
                    IBlockState newState = BlocksAS.customOre.getDefaultState().withProperty(BlockCustomOre.ORE_TYPE, BlockCustomOre.OreType.ROCK_CRYSTAL);
                    world.setBlockState(pos, newState);
                    RockCrystalBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.ROCK_CRYSTAL);
                    buf.addOre(pos);
                }
            }
        }
    }
}
