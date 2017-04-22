/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.BlockFluidBase;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockCrystalEnhancement
 * Created by HellFirePvP
 * Date: 22.04.2017 / 11:23
 */
public class MultiblockCrystalEnhancement extends PatternBlockArray {

    public MultiblockCrystalEnhancement() {
        load();
    }

    private void load() {
        IBlockState mru = BlockMarble.MarbleBlockType.RUNED.asBlock();
        IBlockState mpl = BlockMarble.MarbleBlockType.PILLAR.asBlock();
        IBlockState mch = BlockMarble.MarbleBlockType.CHISELED.asBlock();
        IBlockState mgr = BlockMarble.MarbleBlockType.ENGRAVED.asBlock();
        IBlockState mrw = BlockMarble.MarbleBlockType.RAW.asBlock();

        addBlockCube(mrw, -1, -5, -1, 1, -5, 1);
        for (BlockPos offset : TileCollectorCrystal.offsetsLiquidStarlight) {
            addBlock(offset, BlocksAS.blockLiquidStarlight.getDefaultState(),
                    (world, pos, state) -> state.getBlock().equals(BlocksAS.blockLiquidStarlight) && state.getValue(BlockFluidBase.LEVEL) == 0);
        }
        addBlockCube(Blocks.AIR.getDefaultState(), 1, 1, 1, -1, -1, -1);
        addBlock(0, 0, 0, BlocksAS.celestialCollectorCrystal.getDefaultState());

        addBlock(0, -2, 0, mch);
        addBlock(0, -3, 0, mpl);
        addBlock(0, -4, 0, mpl);

        addBlock(-2, -4, -2, mch);
        addBlock(-2, -4,  2, mch);
        addBlock( 2, -4,  2, mch);
        addBlock( 2, -4, -2, mch);
        addBlock(-2, -3, -2, mgr);
        addBlock(-2, -3,  2, mgr);
        addBlock( 2, -3,  2, mgr);
        addBlock( 2, -3, -2, mgr);

        addBlock(-2, -4, -1, mru);
        addBlock(-2, -4,  0, mru);
        addBlock(-2, -4,  1, mru);
        addBlock( 2, -4, -1, mru);
        addBlock( 2, -4,  0, mru);
        addBlock( 2, -4,  1, mru);
        addBlock(-1, -4, -2, mru);
        addBlock( 0, -4, -2, mru);
        addBlock( 1, -4, -2, mru);
        addBlock(-1, -4,  2, mru);
        addBlock( 0, -4,  2, mru);
        addBlock( 1, -4,  2, mru);
    }

}
