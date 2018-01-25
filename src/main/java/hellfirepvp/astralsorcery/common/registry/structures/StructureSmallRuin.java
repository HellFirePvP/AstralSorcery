/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarbleStairs;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.TilePortalNode;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

import static hellfirepvp.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallRuin
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:07
 */
public class StructureSmallRuin extends StructureBlockArray {

    public StructureSmallRuin() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mrw = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mch = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mar = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mbr = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);

        IBlockState stairsSouth = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(BlockMarbleStairs.FACING, EnumFacing.SOUTH);
        IBlockState stairsNorth = BlocksAS.blockMarbleStairs.getDefaultState().withProperty(BlockMarbleStairs.FACING, EnumFacing.NORTH);

        addBlock( 0, 3,  0, BlocksAS.blockPortalNode.getDefaultState());
        addTileCallback(new BlockPos(0, 3, 0), new TileEntityCallback() {
            @Override
            public boolean isApplicable(TileEntity te) {
                return te != null && te instanceof TilePortalNode;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                if(te instanceof TilePortalNode) {
                    ((TilePortalNode) te).setWorldGen(true);
                }
            }
        });

        addBlock( 1, 0,  0, mrw);
        addBlock( 1, 0,  1, mrw);
        addBlock( 1, 0,  2, mrw);
        addBlock( 1, 0,  3, mrw);
        addBlock( 2, 0,  2, mrw);
        addBlock( 0, 0,  2, mrw);
        addBlock(-1, 0,  2, mrw);
        addBlock( 0, 0,  3, mrw);
        addBlock(-1, 0,  3, mrw);
        addBlock( 0, 0,  4, mrw);
        addBlock(-1, 0,  4, mrw);
        addBlock( 0, 0,  5, mrw);

        addBlock( 0, 0, -2, mrw);
        addBlock( 0, 0, -3, mrw);
        addBlock( 0, 0, -4, mrw);
        addBlock( 0, 0, -5, mrw);
        addBlock( 0, 0, -6, mrw);
        addBlock( 1, 0, -1, mrw);
        addBlock( 1, 0, -2, mbr);
        addBlock( 1, 0, -3, mrw);
        addBlock( 1, 0, -4, mrw);
        addBlock( 2, 0, -2, mrw);
        addBlock( 2, 0, -3, mrw);
        addBlock( -1, 0, -3, mbr);
        addBlock( -1, 0, -4, mrw);
        addBlock( -2, 0, -4, mrw);

        addBlock(0, 1,  1, stairsSouth);
        addBlock(0, 1, -1, stairsNorth);

        addBlock( 0, 1, -2, mrw);
        addBlock( 0, 1, -3, mrw);
        addBlock( 0, 1, -4, mrw);
        addBlock( 1, 1, -2, mrw);
        addBlock(-1, 1, -2, mrw);
        addBlock( 1, 1,  0, mar);
        addBlock( 1, 1, -1, mar);
        addBlock( 1, 1, -3, stairsSouth);

        addBlock(-1, 1,  1, mar);
        addBlock(-1, 1,  2, mrw);
        addBlock( 0, 1,  2, mrw);
        addBlock( 1, 1,  2, mbr);
        addBlock( 1, 1,  3, stairsNorth);
        addBlock( 0, 1,  3, mrw);

        addBlock( 0, 2,  2, mch);
        addBlock( 0, 3,  2, mrw);
        addBlock( 0, 2, -2, mrw);
        addBlock( 0, 3, -2, mch);

        addBlock( 0, 4,  2, stairsNorth);
        addBlock( 0, 4, -2, stairsSouth);
    }

}
