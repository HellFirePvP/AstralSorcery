/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.multiblock;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.block.BlockBlackMarble;
import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.BlockStateCheck;
import hellfirepvp.astralsorcery.common.structure.array.PatternBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

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
        super(new ResourceLocation(AstralSorcery.MODID, "pattern_fountain"));
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

        for (int yy = -2; yy <= 2; yy++) {
            for (int xx = -3; xx <= 3; xx++) {
                for (int zz = -3; zz <= 3; zz++) {
                    if(Math.abs(xx) == 3 && Math.abs(zz) == 3) continue; //corners

                    if(xx == 0 && zz == 0) {
                        if (yy == -2) {
                            addAir(xx, yy, zz);
                        }
                    } else {
                        addAir(xx, yy, zz);
                    }
                }
            }
        }
    }

}
