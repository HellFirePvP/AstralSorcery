/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import static hellfirepvp.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockFountain
 * Created by HellFirePvP
 * Date: 31.10.2017 / 15:36
 */
public class MultiblockFountain extends PatternBlockArray {

    public MultiblockFountain() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;

        IBlockState mpl = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mru = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState msr = BlocksAS.blockBlackMarble.getDefaultState()
                .withProperty(BlockBlackMarble.BLACK_MARBLE_TYPE, BlockBlackMarble.BlackMarbleBlockType.RAW);

        addBlock(0, 0, 0, BlocksAS.blockBore.getDefaultState(), new BlockStateCheck.Block(BlocksAS.blockBore));

        addBlock( 4,  0,  0, msr);
        addBlock(-4,  0,  0, msr);
        addBlock( 0,  0,  4, msr);
        addBlock( 0,  0, -4, msr);

        addBlock( 4,  1,  0, mpl);
        addBlock( 4,  2,  0, mpl);
        addBlock( 4, -1,  0, mpl);
        addBlock( 4, -2,  0, mpl);

        addBlock(-4,  1,  0, mpl);
        addBlock(-4,  2,  0, mpl);
        addBlock(-4, -1,  0, mpl);
        addBlock(-4, -2,  0, mpl);

        addBlock( 0,  1,  4, mpl);
        addBlock( 0,  2,  4, mpl);
        addBlock( 0, -1,  4, mpl);
        addBlock( 0, -2,  4, mpl);

        addBlock( 0,  1, -4, mpl);
        addBlock( 0,  2, -4, mpl);
        addBlock( 0, -1, -4, mpl);
        addBlock( 0, -2, -4, mpl);

        addBlock( 4,  0,  1, mru);
        addBlock( 4,  0,  2, mru);
        addBlock( 4,  0, -1, mru);
        addBlock( 4,  0, -2, mru);

        addBlock(-4,  0,  1, mru);
        addBlock(-4,  0,  2, mru);
        addBlock(-4,  0, -1, mru);
        addBlock(-4,  0, -2, mru);

        addBlock( 1,  0,  4, mru);
        addBlock( 2,  0,  4, mru);
        addBlock(-1,  0,  4, mru);
        addBlock(-2,  0,  4, mru);

        addBlock( 1,  0, -4, mru);
        addBlock( 2,  0, -4, mru);
        addBlock(-1,  0, -4, mru);
        addBlock(-2,  0, -4, mru);

        addBlock( 3,  0,  3, mru);
        addBlock( 3,  0, -3, mru);
        addBlock(-3,  0, -3, mru);
        addBlock(-3,  0,  3, mru);
    }

}
