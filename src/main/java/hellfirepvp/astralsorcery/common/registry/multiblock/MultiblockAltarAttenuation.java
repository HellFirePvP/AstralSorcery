package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockAltar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.struct.PatternBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import static hellfirepvp.astralsorcery.common.block.BlockMarble.MARBLE_TYPE;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MultiblockAltarAttenuation
 * Created by HellFirePvP
 * Date: 17.10.2016 / 11:46
 */
public class MultiblockAltarAttenuation extends PatternBlockArray {

    public MultiblockAltarAttenuation() {
        load();
    }

    private void load() {
        Block marble = BlocksAS.blockMarble;
        IBlockState mch = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mbr = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);
        IBlockState mrw = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mar = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mpl = marble.getDefaultState().withProperty(MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);

        addBlockCube(Blocks.AIR.getDefaultState(), -3,  0, -3, 3,  2, 3);
        addBlockCube(mrw,                          -3, -1, -3, 3, -1, 3);

        addBlock(0, 0, 0, BlocksAS.blockAltar.getDefaultState().withProperty(BlockAltar.ALTAR_TYPE, BlockAltar.AltarType.ALTAR_2));

        for (int i = -3; i <= 3; i++) {
            addBlock( 4, -1,  i, mar);
            addBlock(-4, -1,  i, mar);
            addBlock( i, -1,  4, mar);
            addBlock( i, -1, -4, mar);
            addBlock( 3, -1,  i, mbr);
            addBlock(-3, -1,  i, mbr);
            addBlock( i, -1,  3, mbr);
            addBlock( i, -1, -3, mbr);
        }
        addBlock( 3, -1,  3, mch);
        addBlock( 3, -1, -3, mch);
        addBlock(-3, -1,  3, mch);
        addBlock(-3, -1, -3, mch);

        addBlock( 2, -1,  0, mbr);
        addBlock(-2, -1,  0, mbr);
        addBlock( 0, -1,  2, mbr);
        addBlock( 0, -1, -2, mbr);

        for (int i = 0; i < 2; i++) {
            addBlock( 3, i,  3, mpl);
            addBlock( 3, i, -3, mpl);
            addBlock(-3, i,  3, mpl);
            addBlock(-3, i, -3, mpl);
        }
        addBlock( 3, 2,  3, mch);
        addBlock( 3, 2, -3, mch);
        addBlock(-3, 2,  3, mch);
        addBlock(-3, 2, -3, mch);
    }

}
