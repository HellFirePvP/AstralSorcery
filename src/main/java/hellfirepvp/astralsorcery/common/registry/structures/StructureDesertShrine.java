/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.LootTableUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.Random;

import static hellfirepvp.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureDesertShrine
 * Created by HellFirePvP
 * Date: 21.10.2016 / 12:17
 */
public class StructureDesertShrine extends StructureBlockArray {

    private static Random rand = new Random();

    public StructureDesertShrine() {
        load();
    }

    @Override
    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center) {
        center = center.down();
        Map<BlockPos, IBlockState> result = super.placeInWorld(world, center);
        placeSand(world, center);
        return result;
    }

    @Override
    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center, PastPlaceProcessor processor) {
        center = center.down();
        Map<BlockPos, IBlockState> result = super.placeInWorld(world, center, processor);
        placeSand(world, center);
        return result;
    }

    private void placeSand(World world, BlockPos center) {
        sandWithChanceOffset( 3, 3,  4, world, center, 0.5);
        sandWithChanceOffset( 4, 3,  3, world, center, 0.5);
        sandWithChanceOffset( 3, 3,  2, world, center, 0.5);
        sandWithChanceOffset( 2, 3,  3, world, center, 0.5);
        sandWithChanceOffset(-3, 3,  4, world, center, 0.5);
        sandWithChanceOffset(-4, 3,  3, world, center, 0.5);
        sandWithChanceOffset(-3, 3,  2, world, center, 0.5);
        sandWithChanceOffset(-2, 3,  3, world, center, 0.5);
        sandWithChanceOffset( 3, 3, -4, world, center, 0.5);
        sandWithChanceOffset( 4, 3, -3, world, center, 0.5);
        sandWithChanceOffset( 3, 3, -2, world, center, 0.5);
        sandWithChanceOffset( 2, 3, -3, world, center, 0.5);
        sandWithChanceOffset(-3, 3, -4, world, center, 0.5);
        sandWithChanceOffset(-4, 3, -3, world, center, 0.5);
        sandWithChanceOffset(-3, 3, -2, world, center, 0.5);
        sandWithChanceOffset(-2, 3, -3, world, center, 0.5);

        sandWithChanceOffset( 1, 2,  1, world, center, 0.5);
        sandWithChanceOffset(-1, 2,  1, world, center, 0.5);
        sandWithChanceOffset( 1, 2, -1, world, center, 0.5);
        sandWithChanceOffset(-1, 2, -1, world, center, 0.5);

        sandWithChanceOffset( 0, 3,  1, world, center, 0.4);
        sandWithChanceOffset( 1, 3,  0, world, center, 0.4);
        sandWithChanceOffset( 0, 3, -1, world, center, 0.4);
        sandWithChanceOffset(-1, 3,  0, world, center, 0.4);
        sandWithChanceOffset( 0, 2,  2, world, center, 0.4);
        sandWithChanceOffset( 2, 2,  0, world, center, 0.4);
        sandWithChanceOffset( 0, 2, -2, world, center, 0.4);
        sandWithChanceOffset(-2, 2,  0, world, center, 0.4);

        sandWithChanceOffset( 2, 2,  2, world, center, 0.3);
        sandWithChanceOffset( 4, 2,  2, world, center, 0.3);
        sandWithChanceOffset( 2, 2,  4, world, center, 0.3);
        sandWithChanceOffset( 4, 2,  4, world, center, 0.3);
        sandWithChanceOffset( 2, 2, -2, world, center, 0.3);
        sandWithChanceOffset( 4, 2, -2, world, center, 0.3);
        sandWithChanceOffset( 2, 2, -4, world, center, 0.3);
        sandWithChanceOffset( 4, 2, -4, world, center, 0.3);
        sandWithChanceOffset(-2, 2,  2, world, center, 0.3);
        sandWithChanceOffset(-4, 2,  2, world, center, 0.3);
        sandWithChanceOffset(-2, 2,  4, world, center, 0.3);
        sandWithChanceOffset(-4, 2,  4, world, center, 0.3);
        sandWithChanceOffset(-2, 2, -2, world, center, 0.3);
        sandWithChanceOffset(-4, 2, -2, world, center, 0.3);
        sandWithChanceOffset(-2, 2, -4, world, center, 0.3);
        sandWithChanceOffset(-4, 2, -4, world, center, 0.3);
    }

    private void sandWithChanceOffset(int x, int y, int z, World world, BlockPos center, double chance) {
        if(rand.nextFloat() <= chance) {
            world.setBlockState(center.add(x, y, z), Blocks.SAND.getDefaultState());
        }
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;

        IBlockState mch = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mbr = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);
        IBlockState mrw = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mar = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mpl = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState air = Blocks.AIR.getDefaultState();
        IBlockState sand = Blocks.SAND.getDefaultState();

        addBlockCube(mrw, -4, 0, -3, 4, -7, 3);
        addBlockCube(mrw, -3, 0, -4, 3, -7, 4);

        addBlockCube(mar, -4, 0, -3, -4, 0,  3);
        addBlockCube(mar,  4, 0, -3,  4, 0,  3);
        addBlockCube(mar, -3, 0, -4,  3, 0, -4);
        addBlockCube(mar, -3, 0,  4,  3, 0,  4);

        addBlockCube(mbr, -3, 0, -3,  3, 0,  3);
        addBlockCube(mrw, -2, 0, -2,  2, 0,  2);

        addBlockCube(air, -2, -2, -2,  2, -5,  2);

        addBlockCube(mpl, -3, -2, -1, -3, -5, -1);
        addBlockCube(mpl, -3, -2,  1, -3, -5,  1);
        addBlockCube(mpl,  3, -2, -1,  3, -5, -1);
        addBlockCube(mpl,  3, -2,  1,  3, -5,  1);
        addBlockCube(mpl, -1, -2, -3, -1, -5, -3);
        addBlockCube(mpl,  1, -2, -3,  1, -5, -3);
        addBlockCube(mpl, -1, -2,  3, -1, -5,  3);
        addBlockCube(mpl,  1, -2,  3,  1, -5,  3);

        addBlockCube(mar, -2, -6, -2, 2, -6, 2);
        addBlockCube(air, -2, -6,  0, 2, -6, 0);
        addBlockCube(air,  0, -6, -2, 0, -6, 2);
        addBlock(-2, -6, -2, mrw);
        addBlock(-2, -6, 2, mrw);
        addBlock(2, -6, -2, mrw);
        addBlock( 2, -6,  2, mrw);

        addBlock(-3, -3,  0, Blocks.WATER.getDefaultState());
        addBlock( 3, -3,  0, Blocks.WATER.getDefaultState());
        addBlock( 0, -3, -3, Blocks.WATER.getDefaultState());
        addBlock( 0, -3,  3, Blocks.WATER.getDefaultState());

        addBlockCube(sand, -4, 1, -4,  4, 1,  4);

        addBlock(0, 1, 0, mpl);
        addBlock(0, 2, 0, mch);

        addBlock( 3, 1,  3, mpl);
        addBlock( 3, 2,  3, mpl);
        addBlock( 3, 3,  3, mch);

        addBlock(-3, 1,  3, mpl);
        addBlock(-3, 2,  3, mpl);
        addBlock(-3, 3,  3, mch);

        addBlock(-3, 1, -3, mpl);
        addBlock(-3, 2, -3, mpl);
        addBlock(-3, 3, -3, mch);

        addBlock( 3, 1, -3, mpl);
        addBlock( 3, 2, -3, mpl);
        addBlock( 3, 3, -3, mch);

        addBlock( 3, 4,  3, sand);
        addBlock(-3, 4,  3, sand);
        addBlock( 3, 4, -3, sand);
        addBlock(-3, 4, -3, sand);
        addBlock( 0, 3,  0, sand);

        addBlock( 1, 2,  0, sand);
        addBlock(-1, 2,  0, sand);
        addBlock( 0, 2,  1, sand);
        addBlock( 0, 2, -1, sand);


        addBlock(-3, 2, -4, sand);
        addBlock(-4, 2, -3, sand);
        addBlock(-3, 2, -2, sand);
        addBlock(-2, 2, -3, sand);

        addBlock(-3, 2,  4, sand);
        addBlock(-4, 2,  3, sand);
        addBlock(-3, 2,  2, sand);
        addBlock(-2, 2,  3, sand);

        addBlock( 3, 2, -4, sand);
        addBlock( 4, 2, -3, sand);
        addBlock( 3, 2, -2, sand);
        addBlock( 2, 2, -3, sand);

        addBlock( 3, 2,  4, sand);
        addBlock( 4, 2,  3, sand);
        addBlock( 3, 2,  2, sand);
        addBlock( 2, 2,  3, sand);

        TileEntityCallback lootCallback = new TileEntityCallback() {
            @Override
            public boolean isApplicable(TileEntity te) {
                return te != null && te instanceof TileEntityChest;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if(te instanceof TileEntityChest) {
                    ((TileEntityChest) te).setLootTable(LootTableUtil.LOOT_TABLE_SHRINE, STATIC_RAND.nextLong());
                }
            }
        };

        addBlock(2, -5, -2, Blocks.CHEST.getDefaultState());
        addTileCallback(new BlockPos(2, -5, -2), lootCallback);

        addBlock(0, -3, 0, BlocksAS.collectorCrystal.getDefaultState());
        addTileCallback(new BlockPos(0, -3, 0), new TileEntityCallback() {
            @Override
            public boolean isApplicable(TileEntity te) {
                return te != null && te instanceof TileCollectorCrystal;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if (te instanceof TileCollectorCrystal) {
                    ((TileCollectorCrystal) te).onPlace(
                            MiscUtils.getRandomEntry(ConstellationRegistry.getWeakConstellations(), STATIC_RAND),
                            CrystalProperties.createStructural(), false,
                            BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL);
                }
            }
        });
    }

}
