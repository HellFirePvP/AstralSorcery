/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.marker;

import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.CrystalPropertiesAS;
import hellfirepvp.astralsorcery.common.lib.LootAS;
import hellfirepvp.astralsorcery.common.tile.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MarkerManagerAS
 * Created by HellFirePvP
 * Date: 18.11.2020 / 20:48
 */
public class MarkerManagerAS {

    public static void handleMarker(String marker, BlockPos pos, IWorld genWorld, Random rand, MutableBoundingBox box) {
        switch (marker) {
            case "brick_shrine_chest":
                if (rand.nextBoolean()) {
                    makeChest(genWorld, pos, LootAS.SHRINE_CHEST, rand, box);
                } else {
                    genWorld.setBlockState(pos, BlocksAS.MARBLE_BRICKS.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
                }
                break;
            case "shrine_chest":
                if (rand.nextBoolean()) {
                    makeChest(genWorld, pos, LootAS.SHRINE_CHEST, rand, box);
                } else {
                    genWorld.setBlockState(pos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
                }
                break;
            case "random_top_block":
                if (rand.nextFloat() < 0.7F) {
                    genWorld.setBlockState(pos, genWorld.getBiome(pos).getGenerationSettings().getSurfaceBuilderConfig().getTop(), Constants.BlockFlags.BLOCK_UPDATE);
                } else {
                    genWorld.setBlockState(pos, Blocks.AIR.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);
                }
                break;
            case "crystal":
                makeCollectorCrystal(genWorld, pos, rand, box);
                break;
        }
    }

    private static void makeCollectorCrystal(IWorld world, BlockPos pos, Random rand, MutableBoundingBox box) {
        if (box.isVecInside(pos) && world.getBlockState(pos).getBlock() != BlocksAS.ROCK_COLLECTOR_CRYSTAL) {
            world.setBlockState(pos, BlocksAS.ROCK_COLLECTOR_CRYSTAL.getDefaultState(), Constants.BlockFlags.BLOCK_UPDATE);

            TileCollectorCrystal tcc = MiscUtils.getTileAt(world, pos, TileCollectorCrystal.class, true);
            if (tcc != null) {
                IMajorConstellation cst = MiscUtils.getRandomEntry(ConstellationRegistry.getMajorConstellations(), rand);
                tcc.setAttributes(CrystalPropertiesAS.WORLDGEN_SHRINE_COLLECTOR_ATTRIBUTES);
                tcc.setAttunedConstellation(cst);
            }
        }
    }

    private static void makeChest(IWorld world, BlockPos pos, ResourceLocation tableName, Random rand, MutableBoundingBox box) {
        if (box.isVecInside(pos) && world.getBlockState(pos).getBlock() != Blocks.CHEST) {
            BlockState chest = StructurePiece.correctFacing(world, pos, Blocks.CHEST.getDefaultState());

            world.setBlockState(pos, chest, Constants.BlockFlags.BLOCK_UPDATE);
            // Static setLootTable used instead of manual tile fetch -> member setLootTable to provide compatibility with Lootr.
            LockableLootTileEntity.setLootTable(world, rand, pos, tableName);
        }
    }
}
