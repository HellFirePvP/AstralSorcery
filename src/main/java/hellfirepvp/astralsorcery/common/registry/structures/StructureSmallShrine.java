/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.LootTableUtil;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallShrine
 * Created by HellFirePvP
 * Date: 07.01.2017 / 16:41
 */
public class StructureSmallShrine extends StructureBlockArray {

    public StructureSmallShrine() {
        load();
    }

    private void load() {
        Block m = BlocksAS.blockMarble;
        IBlockState mRaw = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mBrick = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);
        IBlockState mChisel = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mPillar = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState air = Blocks.AIR.getDefaultState();

        addBlockCube(mRaw,   -4, 0, -4, 4, 0, 4);
        addBlockCube(air,    -4, 1, -4, 4, 5, 4);
        addBlockCube(mBrick, -3, 1, -3, 3, 1, 3);
        addBlockCube(air,    -1, 1, -1, 1, 1, 1);

        addBlock(0, 1, 0, mPillar);
        addBlock(0, 2, 0, mPillar);
        addBlock(0, 3, 0, Blocks.SEA_LANTERN.getDefaultState());
        addBlock(0, 4, 0, Blocks.WATER.getDefaultState());

        addBlock( 2, 2,  2, mPillar);
        addBlock( 2, 3,  2, mPillar);
        addBlock( 2, 4,  2, mPillar);
        addBlock( 2, 5,  2, mChisel);

        addBlock( 2, 2, -2, mPillar);
        addBlock( 2, 3, -2, mPillar);
        addBlock( 2, 4, -2, mPillar);
        addBlock( 2, 5, -2, mChisel);

        addBlock(-2, 2,  2, mPillar);
        addBlock(-2, 3,  2, mPillar);
        addBlock(-2, 4,  2, mPillar);
        addBlock(-2, 5,  2, mChisel);

        addBlock(-2, 2, -2, mPillar);
        addBlock(-2, 3, -2, mPillar);
        addBlock(-2, 4, -2, mPillar);
        addBlock(-2, 5, -2, mChisel);

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

        addBlock( 2, 1, -2, Blocks.CHEST.getDefaultState());
        addTileCallback(new BlockPos(2, 1, -2), lootCallback);

    }

}
