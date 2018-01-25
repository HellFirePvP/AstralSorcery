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
        IBlockState mru = BlockMarble.MarbleBlockType.RUNED.asBlock();
        IBlockState mgr = BlockMarble.MarbleBlockType.ENGRAVED.asBlock();
        IBlockState sooty = BlockBlackMarble.BlackMarbleBlockType.RAW.asBlock();

        addBlockCube(mar, -3, -1, -3, 3, -1, 3);
        addBlockCube(sooty, -2, -1, -2, 2, -1, 2);
        addBlock(0, 0, 0, BlocksAS.celestialGateway.getDefaultState());

        addBlock(-3, -1, -3, mru);
        addBlock( 3, -1, -3, mru);
        addBlock( 3, -1,  3, mru);
        addBlock(-3, -1,  3, mru);

        addBlock(-3,  0, -3, mgr);
        addBlock( 3,  0, -3, mgr);
        addBlock( 3,  0,  3, mgr);
        addBlock(-3,  0,  3, mgr);
    }

}
