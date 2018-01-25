/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry.structures;

import hellfirepvp.astralsorcery.common.block.BlockMarble;
import hellfirepvp.astralsorcery.common.block.network.BlockCollectorCrystalBase;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.item.crystal.CrystalProperties;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.tile.network.TileCollectorCrystal;
import hellfirepvp.astralsorcery.common.util.LootTableUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
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
 * Class: StructureAncientShrine
 * Created by HellFirePvP
 * Date: 02.08.2016 / 10:24
 */
public class StructureAncientShrine extends StructureBlockArray {

    public StructureAncientShrine() {
        load();
    }

    private void load() {
        Block m = BlocksAS.blockMarble;
        IBlockState mRaw = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RAW);
        IBlockState mBrick = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.BRICKS);
        IBlockState mChisel = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.CHISELED);
        IBlockState mPillar = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.PILLAR);
        IBlockState mArch = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ARCH);
        IBlockState mRuned = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.RUNED);
        IBlockState mEngraved = m.getDefaultState().withProperty(BlockMarble.MARBLE_TYPE, BlockMarble.MarbleBlockType.ENGRAVED);
        IBlockState air = Blocks.AIR.getDefaultState();

        addBlockCube(mRaw, -7, 0, -7, 7, 0, 7);
        addBlockCube(air, -7, 1, -7, 7, 11, 7);

        addBlockCube(mRaw, -9, 0, -9, -5, 0, -5);
        addBlockCube(mRaw, 9, 0, 9, 5, 0, 5);
        addBlockCube(mRaw, -9, 0, 9, -5, 0, 5);
        addBlockCube(mRaw, 9, 0, -9, 5, 0, -5);
        addBlockCube(air, -9, 1, -9, -5, 6, -5);
        addBlockCube(air, 9, 1, 9, 5, 6, 5);
        addBlockCube(air, -9, 1, 9, -5, 6, 5);
        addBlockCube(air, 9, 1, -9, 5, 6, -5);

        addBlockCube(mRaw, -6, -1, -6, 6, -7, 6);
        addBlockCube(air, -4, -1, -4, 4, -5, 4);

        addBlockCube(mBrick, -6, 1, -6, 6, 1, 6);
        addBlockCube(mBrick, -8, 1, -8, -6, 1, -6);
        addBlockCube(mBrick, -8, 1, 8, -6, 1, 6);
        addBlockCube(mBrick, 8, 1, -8, 6, 1, -6);
        addBlockCube(mBrick, 8, 1, 8, 6, 1, 6);

        addBlockCube(air, -2, 1, -2, 2, 1, 2);

        addBlockCube(air, -3, 1, 1, -3, 1, -1);
        addBlockCube(air, 3, 1, 1, 3, 1, -1);
        addBlockCube(air, 1, 1, -3, -1, 1, -3);
        addBlockCube(air, 1, 1, 3, -1, 1, 3);

        addBlockCube(air, -1, -6, -1, 1, -6, 1);

        addBlockCube(mBrick, -2, 10, -2, 2, 10, 2);

        addBlock( 0, 0,  0, Blocks.SEA_LANTERN.getDefaultState());
        addBlock( 1, 0,  0, mChisel);
        addBlock(-1, 0,  0, mChisel);
        addBlock( 0, 0,  1, mChisel);
        addBlock( 0, 0, -1, mChisel);

        addBlock(-5, -2,  0, Blocks.WATER.getDefaultState());
        addBlock( 5, -2,  0, Blocks.WATER.getDefaultState());
        addBlock( 0, -2, -5, Blocks.WATER.getDefaultState());
        addBlock( 0, -2,  5, Blocks.WATER.getDefaultState());

        addBlock( 2, -6,  0, air);
        addBlock( 3, -6,  0, air);
        addBlock( 4, -6,  0, air);
        addBlock(-2, -6,  0, air);
        addBlock(-3, -6,  0, air);
        addBlock(-4, -6,  0, air);
        addBlock( 0, -6,  2, air);
        addBlock( 0, -6,  3, air);
        addBlock( 0, -6,  4, air);
        addBlock( 0, -6, -2, air);
        addBlock( 0, -6, -3, air);
        addBlock( 0, -6, -4, air);

        addBlock( 5, -6,  0, mArch);
        addBlock(-5, -6,  0, mArch);
        addBlock( 0, -6,  5, mArch);
        addBlock( 0, -6, -5, mArch);
        addBlock( 2, -6,  1, mArch);
        addBlock( 3, -6,  1, mArch);
        addBlock( 4, -6,  1, mArch);
        addBlock(-2, -6,  1, mArch);
        addBlock(-3, -6,  1, mArch);
        addBlock(-4, -6,  1, mArch);
        addBlock( 2, -6, -1, mArch);
        addBlock( 3, -6, -1, mArch);
        addBlock( 4, -6, -1, mArch);
        addBlock(-2, -6, -1, mArch);
        addBlock(-3, -6, -1, mArch);
        addBlock(-4, -6, -1, mArch);
        addBlock( 1, -6,  2, mArch);
        addBlock( 1, -6,  3, mArch);
        addBlock( 1, -6,  4, mArch);
        addBlock( 1, -6, -2, mArch);
        addBlock( 1, -6, -3, mArch);
        addBlock( 1, -6, -4, mArch);
        addBlock(-1, -6,  2, mArch);
        addBlock(-1, -6,  3, mArch);
        addBlock(-1, -6,  4, mArch);
        addBlock(-1, -6, -2, mArch);
        addBlock(-1, -6, -3, mArch);
        addBlock(-1, -6, -4, mArch);

        addBlock(-3, -5, -3, mRuned);
        addBlock(-3, -4, -3, mPillar);
        addBlock(-3, -3, -3, mPillar);
        addBlock(-3, -2, -3, mPillar);
        addBlock(-3, -1, -3, mEngraved);
        addBlock(-3, -5,  3, mRuned);
        addBlock(-3, -4,  3, mPillar);
        addBlock(-3, -3,  3, mPillar);
        addBlock(-3, -2,  3, mPillar);
        addBlock(-3, -1,  3, mEngraved);
        addBlock( 3, -5, -3, mRuned);
        addBlock( 3, -4, -3, mPillar);
        addBlock( 3, -3, -3, mPillar);
        addBlock( 3, -2, -3, mPillar);
        addBlock( 3, -1, -3, mEngraved);
        addBlock( 3, -5,  3, mRuned);
        addBlock( 3, -4,  3, mPillar);
        addBlock( 3, -3,  3, mPillar);
        addBlock( 3, -2,  3, mPillar);
        addBlock( 3, -1,  3, mEngraved);

        addBlock(-5, -5, -3, mPillar);
        addBlock(-5, -4, -3, mPillar);
        addBlock(-5, -3, -3, mPillar);
        addBlock(-5, -2, -3, mPillar);
        addBlock(-5, -1, -3, mChisel);
        addBlock(-3, -5, -5, mPillar);
        addBlock(-3, -4, -5, mPillar);
        addBlock(-3, -3, -5, mPillar);
        addBlock(-3, -2, -5, mPillar);
        addBlock(-3, -1, -5, mChisel);
        addBlock( 5, -5, -3, mPillar);
        addBlock( 5, -4, -3, mPillar);
        addBlock( 5, -3, -3, mPillar);
        addBlock( 5, -2, -3, mPillar);
        addBlock( 5, -1, -3, mChisel);
        addBlock( 3, -5, -5, mPillar);
        addBlock( 3, -4, -5, mPillar);
        addBlock( 3, -3, -5, mPillar);
        addBlock( 3, -2, -5, mPillar);
        addBlock( 3, -1, -5, mChisel);
        addBlock(-5, -5,  3, mPillar);
        addBlock(-5, -4,  3, mPillar);
        addBlock(-5, -3,  3, mPillar);
        addBlock(-5, -2,  3, mPillar);
        addBlock(-5, -1,  3, mChisel);
        addBlock(-3, -5,  5, mPillar);
        addBlock(-3, -4,  5, mPillar);
        addBlock(-3, -3,  5, mPillar);
        addBlock(-3, -2,  5, mPillar);
        addBlock(-3, -1,  5, mChisel);
        addBlock( 5, -5,  3, mPillar);
        addBlock( 5, -4,  3, mPillar);
        addBlock( 5, -3,  3, mPillar);
        addBlock( 5, -2,  3, mPillar);
        addBlock( 5, -1,  3, mChisel);
        addBlock( 3, -5,  5, mPillar);
        addBlock( 3, -4,  5, mPillar);
        addBlock( 3, -3,  5, mPillar);
        addBlock( 3, -2,  5, mPillar);
        addBlock( 3, -1,  5, mChisel);

        addBlock(-7, 2, -7, mBrick);
        addBlock(-7, 3, -7, mPillar);
        addBlock(-7, 4, -7, mPillar);
        addBlock(-7, 5, -7, mChisel);
        addBlock( 7, 2, -7, mBrick);
        addBlock( 7, 3, -7, mPillar);
        addBlock( 7, 4, -7, mPillar);
        addBlock( 7, 5, -7, mChisel);
        addBlock(-7, 2,  7, mBrick);
        addBlock(-7, 3,  7, mPillar);
        addBlock(-7, 4,  7, mPillar);
        addBlock(-7, 5,  7, mChisel);
        addBlock( 7, 2,  7, mBrick);
        addBlock( 7, 3,  7, mPillar);
        addBlock( 7, 4,  7, mPillar);
        addBlock( 7, 5,  7, mChisel);

        addBlock( 5, 2,  0, mBrick);
        addBlock( 5, 3,  0, mPillar);
        addBlock( 5, 4,  0, mPillar);
        addBlock( 5, 5,  0, mPillar);
        addBlock( 5, 6,  0, mChisel);
        addBlock( 5, 7,  0, mPillar);
        addBlock( 5, 8,  0, mChisel);
        addBlock(-5, 2,  0, mBrick);
        addBlock(-5, 3,  0, mPillar);
        addBlock(-5, 4,  0, mPillar);
        addBlock(-5, 5,  0, mPillar);
        addBlock(-5, 6,  0, mChisel);
        addBlock(-5, 7,  0, mPillar);
        addBlock(-5, 8,  0, mChisel);
        addBlock( 0, 2,  5, mBrick);
        addBlock( 0, 3,  5, mPillar);
        addBlock( 0, 4,  5, mPillar);
        addBlock( 0, 5,  5, mPillar);
        addBlock( 0, 6,  5, mChisel);
        addBlock( 0, 7,  5, mPillar);
        addBlock( 0, 8,  5, mChisel);
        addBlock( 0, 2, -5, mBrick);
        addBlock( 0, 3, -5, mPillar);
        addBlock( 0, 4, -5, mPillar);
        addBlock( 0, 5, -5, mPillar);
        addBlock( 0, 6, -5, mChisel);
        addBlock( 0, 7, -5, mPillar);
        addBlock( 0, 8, -5, mChisel);

        addBlock( 5, 2,  5, mRuned);
        addBlock( 5, 3,  5, mPillar);
        addBlock( 5, 4,  5, mPillar);
        addBlock( 5, 5,  5, mPillar);
        addBlock( 5, 6,  5, Blocks.SEA_LANTERN.getDefaultState());
        addBlock(-5, 2,  5, mRuned);
        addBlock(-5, 3,  5, mPillar);
        addBlock(-5, 4,  5, mPillar);
        addBlock(-5, 5,  5, mPillar);
        addBlock(-5, 6,  5, Blocks.SEA_LANTERN.getDefaultState());
        addBlock( 5, 2, -5, mRuned);
        addBlock( 5, 3, -5, mPillar);
        addBlock( 5, 4, -5, mPillar);
        addBlock( 5, 5, -5, mPillar);
        addBlock( 5, 6, -5, Blocks.SEA_LANTERN.getDefaultState());
        addBlock(-5, 2, -5, mRuned);
        addBlock(-5, 3, -5, mPillar);
        addBlock(-5, 4, -5, mPillar);
        addBlock(-5, 5, -5, mPillar);
        addBlock(-5, 6, -5, Blocks.SEA_LANTERN.getDefaultState());

        addBlock( 5, 6,  4, mArch);
        addBlock( 5, 6,  3, mArch);
        addBlock( 5, 6,  2, mArch);
        addBlock( 5, 6,  1, mArch);
        addBlock( 5, 6, -1, mArch);
        addBlock( 5, 6, -2, mArch);
        addBlock( 5, 6, -3, mArch);
        addBlock( 5, 6, -4, mArch);
        addBlock(-5, 6,  4, mArch);
        addBlock(-5, 6,  3, mArch);
        addBlock(-5, 6,  2, mArch);
        addBlock(-5, 6,  1, mArch);
        addBlock(-5, 6, -1, mArch);
        addBlock(-5, 6, -2, mArch);
        addBlock(-5, 6, -3, mArch);
        addBlock(-5, 6, -4, mArch);
        addBlock( 4, 6,  5, mArch);
        addBlock( 3, 6,  5, mArch);
        addBlock( 2, 6,  5, mArch);
        addBlock( 1, 6,  5, mArch);
        addBlock(-1, 6,  5, mArch);
        addBlock(-2, 6,  5, mArch);
        addBlock(-3, 6,  5, mArch);
        addBlock(-4, 6,  5, mArch);
        addBlock( 4, 6, -5, mArch);
        addBlock( 3, 6, -5, mArch);
        addBlock( 2, 6, -5, mArch);
        addBlock( 1, 6, -5, mArch);
        addBlock(-1, 6, -5, mArch);
        addBlock(-2, 6, -5, mArch);
        addBlock(-3, 6, -5, mArch);
        addBlock(-4, 6, -5, mArch);

        addBlock( 4, 1,  4, mRaw);
        addBlock( 3, 1,  4, mRaw);
        addBlock( 4, 1,  3, mRaw);
        addBlock(-4, 1,  4, mRaw);
        addBlock(-3, 1,  4, mRaw);
        addBlock(-4, 1,  3, mRaw);
        addBlock( 4, 1, -4, mRaw);
        addBlock( 3, 1, -4, mRaw);
        addBlock( 4, 1, -3, mRaw);
        addBlock(-4, 1, -4, mRaw);
        addBlock(-3, 1, -4, mRaw);
        addBlock(-4, 1, -3, mRaw);

        addBlock( 4, 6,  4, mBrick);
        addBlock( 4, 7,  4, mBrick);
        addBlock( 3, 7,  3, mBrick);
        addBlock( 3, 8,  3, mBrick);
        addBlock(-4, 6,  4, mBrick);
        addBlock(-4, 7,  4, mBrick);
        addBlock(-3, 7,  3, mBrick);
        addBlock(-3, 8,  3, mBrick);
        addBlock( 4, 6, -4, mBrick);
        addBlock( 4, 7, -4, mBrick);
        addBlock( 3, 7, -3, mBrick);
        addBlock( 3, 8, -3, mBrick);
        addBlock(-4, 6, -4, mBrick);
        addBlock(-4, 7, -4, mBrick);
        addBlock(-3, 7, -3, mBrick);
        addBlock(-3, 8, -3, mBrick);

        addBlock( 2, 8,  3, mBrick);
        addBlock( 2, 9,  3, mBrick);
        addBlock( 3, 8,  2, mBrick);
        addBlock( 3, 9,  2, mBrick);
        addBlock( 2, 9,  2, mRuned);
        addBlock(-2, 8,  3, mBrick);
        addBlock(-2, 9,  3, mBrick);
        addBlock(-3, 8,  2, mBrick);
        addBlock(-3, 9,  2, mBrick);
        addBlock(-2, 9,  2, mRuned);
        addBlock( 2, 8, -3, mBrick);
        addBlock( 2, 9, -3, mBrick);
        addBlock( 3, 8, -2, mBrick);
        addBlock( 3, 9, -2, mBrick);
        addBlock( 2, 9, -2, mRuned);
        addBlock(-2, 8, -3, mBrick);
        addBlock(-2, 9, -3, mBrick);
        addBlock(-3, 8, -2, mBrick);
        addBlock(-3, 9, -2, mBrick);
        addBlock(-2, 9, -2, mRuned);

        addBlock( 1, 9,  3, mBrick);
        addBlock( 0, 9,  3, mBrick);
        addBlock(-1, 9,  3, mBrick);
        addBlock( 1, 9, -3, mBrick);
        addBlock( 0, 9, -3, mBrick);
        addBlock(-1, 9, -3, mBrick);
        addBlock( 3, 9,  1, mBrick);
        addBlock( 3, 9,  0, mBrick);
        addBlock( 3, 9, -1, mBrick);
        addBlock(-3, 9,  1, mBrick);
        addBlock(-3, 9,  0, mBrick);
        addBlock(-3, 9, -1, mBrick);

        addBlock( 2, 10,  2, air);
        addBlock(-2, 10,  2, air);
        addBlock( 2, 10, -2, air);
        addBlock(-2, 10, -2, air);

        addBlock( 0, 1,  0, mPillar);
        addBlock( 0, 2,  0, mPillar);
        addBlock( 0, 3,  0, mPillar);
        addBlock( 0, 4,  0, mChisel);
        addBlock( 0, 5,  0, Blocks.WATER.getDefaultState());

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

        addBlock( 4, -5, -4, Blocks.CHEST.getDefaultState());
        addTileCallback(new BlockPos(4, -5, -4), lootCallback);

        addBlock( -4, -5, 4, Blocks.CHEST.getDefaultState());
        addTileCallback(new BlockPos(-4, -5, 4), lootCallback);

        addBlock(0, -3, 0, BlocksAS.collectorCrystal.getDefaultState());
        addTileCallback(new BlockPos(0, -3, 0), new TileEntityCallback() {
            @Override
            public boolean isApplicable(TileEntity te) {
                return te != null && te instanceof TileCollectorCrystal;
            }

            @Override
            public void onPlace(IBlockAccess access, BlockPos at, TileEntity te) {
                    if(te instanceof TileCollectorCrystal) {
                    ((TileCollectorCrystal) te).onPlace(
                            MiscUtils.getRandomEntry(ConstellationRegistry.getMajorConstellations(), STATIC_RAND),
                            CrystalProperties.getMaxRockProperties(), false,
                            BlockCollectorCrystalBase.CollectorCrystalType.ROCK_CRYSTAL);
                }
            }
        });
    }

}
