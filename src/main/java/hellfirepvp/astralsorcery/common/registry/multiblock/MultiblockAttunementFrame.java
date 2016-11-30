package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAttunementFrame
 * Created by HellFirePvP
 * Date: 28.11.2016 / 10:36
 */
public class MultiblockAttunementFrame extends PatternBlockArray {

    public MultiblockAttunementFrame() {
        load();
    }

    private void load() {
        IBlockState mar = BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mbl = BlocksAS.blockBlackMarble.getDefaultState().withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.RAW);

        addBlock(0, 0, 0, BlocksAS.attunementAltar.getDefaultState());

        addBlockCube(mar, -7, -2, -8,  7, -2, -8);
        addBlockCube(mar, -7, -2,  8,  7, -2,  8);
        addBlockCube(mar, -8, -2, -7, -8, -2,  7);
        addBlockCube(mar,  8, -2, -7,  8, -2,  7);

        addBlockCube(mbl, -7, -2, -7,  7, -2,  7);

        pillarAt(-8, -1, -8);
        pillarAt(-8, -1,  8);
        pillarAt( 8, -1, -8);
        pillarAt( 8, -1,  8);

        addBlock(-9, -2, -9, mar);
        addBlock(-9, -2, -8, mar);
        addBlock(-9, -2, -7, mar);
        addBlock(-8, -2, -9, mar);
        addBlock(-7, -2, -9, mar);

        addBlock(-9, -2,  9, mar);
        addBlock(-9, -2,  8, mar);
        addBlock(-9, -2,  7, mar);
        addBlock(-8, -2,  9, mar);
        addBlock(-7, -2,  9, mar);

        addBlock( 9, -2, -9, mar);
        addBlock( 9, -2, -8, mar);
        addBlock( 9, -2, -7, mar);
        addBlock( 8, -2, -9, mar);
        addBlock( 7, -2, -9, mar);

        addBlock( 9, -2,  9, mar);
        addBlock( 9, -2,  8, mar);
        addBlock( 9, -2,  7, mar);
        addBlock( 8, -2,  9, mar);
        addBlock( 7, -2,  9, mar);
    }

    private void pillarAt(int x, int y, int z) {
        IBlockState mru = BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState mpl = BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mch = BlocksAS.blockMarble.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);

        addBlock(x, y,     z, mru);
        addBlock(x, y + 1, z, mpl);
        addBlock(x, y + 2, z, mpl);
        addBlock(x, y + 3, z, mpl);
        addBlock(x, y + 4, z, mch);
    }

}
