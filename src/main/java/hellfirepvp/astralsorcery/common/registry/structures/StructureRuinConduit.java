/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarbleSlab;
import hellfirepvp.astralsorcery.common.block.BlockMarbleStairs;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TileStructController;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Random;

import static hellfirepvp.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureRuinConduit
 * Created by HellFirePvP
 * Date: 17.08.2018 / 21:41
 */
public class StructureRuinConduit extends StructureBlockArray {

    public StructureRuinConduit() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mrw = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mch = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mru = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);

        IBlockState stairsNorth = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(BlockMarbleStairs.FACING, EnumFacing.NORTH);
        IBlockState stairsEast  = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(BlockMarbleStairs.FACING, EnumFacing.EAST);
        IBlockState stairsSouth = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(BlockMarbleStairs.FACING, EnumFacing.SOUTH);
        IBlockState stairsWest  = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(BlockMarbleStairs.FACING, EnumFacing.WEST);

        IBlockState slabBottom  = BlocksAS.blockMarbleSlab.getDefaultState().withProperty(BlockMarbleSlab.HALF, BlockMarbleSlab.EnumBlockHalf.BOTTOM);
        IBlockState slabTop     = BlocksAS.blockMarbleSlab.getDefaultState().withProperty(BlockMarbleSlab.HALF, BlockMarbleSlab.EnumBlockHalf.TOP);


        addBlock(-1, 0, -1, mrw);
        addBlock( 1, 0, -1, mrw);
        addBlock( 1, 0,  0, mrw);
        addBlock( 0, 0,  0, mrw);

        addBlock( 1, 1, -1, mrw);
        addBlock(-1, 1,  0, stairsEast);
        addBlock( 0, 1, -1, stairsSouth);
        addBlock( 1, 1,  0, slabBottom);
        addBlock( 0, 1,  1, slabBottom);
        addBlock( 0, 1,  0, mch);


        addBlock( 0, 4,  0, mru);
        addBlock(-1, 4,  0, slabTop);
        addBlock( 0, 4, -1, slabTop);
        addBlock( 1, 4,  0, stairsWest.withProperty(BlockMarbleStairs.HALF, BlockMarbleStairs.EnumHalf.TOP));
        addBlock( 0, 4,  1, stairsNorth.withProperty(BlockMarbleStairs.HALF, BlockMarbleStairs.EnumHalf.TOP));

        addBlock( 0, 5,  1, slabBottom);
        addBlock( 1, 5,  0, stairsEast);
        addBlock( 0, 5, -1, stairsNorth);

        addBlock( 0, 6, 0, BlocksAS.blockPortalNode.getDefaultState());
        addTileCallback(new BlockPos(0, 6, 0), new TileEntityCallback() {
            @Override
            public boolean isApplicable(TileEntity te) {
                return te != null && te instanceof TileStructController;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if(te instanceof TileStructController) {
                    ((TileStructController) te).setType(TileStructController.StructType.CONDUIT);
                }
            }
        });
    }

    @Override
    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center) {
        Map<BlockPos, IBlockState> placed = super.placeInWorld(world, center);

        IBlockState stairs = getRandomStairs(world.rand, BlockMarbleStairs.EnumHalf.BOTTOM);
        world.setBlockState(center.add(0, 2, 0), stairs);
        placed.put(center.add(0, 2, 0), stairs);

        stairs = getRandomStairs(world.rand, BlockMarbleStairs.EnumHalf.TOP);
        world.setBlockState(center.add(0, 3, 0), stairs);
        placed.put(center.add(0, 3, 0), stairs);
        return placed;
    }

    @Override
    public Map<BlockPos, IBlockState> placeInWorld(World world, BlockPos center, PastPlaceProcessor processor) {
        Map<BlockPos, IBlockState> placed = super.placeInWorld(world, center, processor);

        IBlockState stairs = getRandomStairs(world.rand, BlockMarbleStairs.EnumHalf.BOTTOM);
        world.setBlockState(center.add(0, 2, 0), stairs);
        placed.put(center.add(0, 2, 0), stairs);

        stairs = getRandomStairs(world.rand, BlockMarbleStairs.EnumHalf.TOP);
        world.setBlockState(center.add(0, 3, 0), stairs);
        placed.put(center.add(0, 3, 0), stairs);
        return placed;
    }

    private IBlockState getRandomStairs(Random rand, BlockMarbleStairs.EnumHalf half) {
        return BlocksAS.blockMarbleStairs.getDefaultState()
                .withProperty(BlockMarbleStairs.FACING,
                        EnumFacing.HORIZONTALS[(rand.nextInt(EnumFacing.HORIZONTALS.length))])
                .withProperty(BlockMarbleStairs.HALF, half);
    }

}
