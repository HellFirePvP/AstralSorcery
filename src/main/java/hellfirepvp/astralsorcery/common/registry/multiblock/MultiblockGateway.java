/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockGateway
 * Created by HellFirePvP
 * Date: 17.04.2017 / 10:50
 */
public class MultiblockGateway extends PatternBlockArray {

    public MultiblockGateway() {
        load();
    }

    private void load() {
        IBlockState mar = BlockMarble.MarbleBlockType.ARCH.asBlock();
        IBlockState mch = BlockMarble.MarbleBlockType.CHISELED.asBlock();
        IBlockState mru = BlockMarble.MarbleBlockType.RUNED.asBlock();
        IBlockState mpl = BlockMarble.MarbleBlockType.PILLAR.asBlock();
        IBlockState sooty = BlockBlackMarble.BlackMarbleBlockType.RAW.asBlock();

        addBlockCube(mar, -3, 0, -3, 3, 0, 3);
        addBlockCube(sooty, -2, 0, -2, 2, 0, 2);
        addBlock(0, 0, 0, BlocksAS.celestialGateway.getDefaultState());

        addBlock(-3, 0, -3, mru);
        addBlock( 3, 0, -3, mru);
        addBlock( 3, 0,  3, mru);
        addBlock(-3, 0,  3, mru);

        addBlock(-3, 1, -3, mpl);
        addBlock( 3, 1, -3, mpl);
        addBlock( 3, 1,  3, mpl);
        addBlock(-3, 1,  3, mpl);

        addBlock(-3, 2, -3, mpl);
        addBlock( 3, 2, -3, mpl);
        addBlock( 3, 2,  3, mpl);
        addBlock(-3, 2,  3, mpl);

        addBlock(-3, 3, -3, mch);
        addBlock( 3, 3, -3, mch);
        addBlock( 3, 3,  3, mch);
        addBlock(-3, 3,  3, mch);
    }

}
