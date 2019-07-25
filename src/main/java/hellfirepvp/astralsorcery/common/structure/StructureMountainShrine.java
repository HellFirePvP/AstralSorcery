/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureMountainShrine
 * Created by HellFirePvP
 * Date: 22.07.2019 / 08:22
 */
public class StructureMountainShrine extends StructureBlockArray {

    public StructureMountainShrine() {
        super(StructureTypesAS.STYPE_MOUNTAIN.getRegistryName());

        makeStructure();
    }

    // TODO stuff
    private void makeStructure() {
        BlockState air         = Blocks.AIR.getDefaultState();
        BlockState mRaw        = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState mBrick      = BlocksAS.MARBLE_BRICKS.getDefaultState();
        BlockState mChisel     = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState mArch       = BlocksAS.MARBLE_ARCH.getDefaultState();
        BlockState mRuned      = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState mEngraved   = BlocksAS.MARBLE_ENGRAVED.getDefaultState();
        BlockState mPillar     = BlocksAS.MARBLE_PILLAR.getDefaultState();
        BlockState mPillarUp   = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.TOP);
        BlockState mPillarDown = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.BOTTOM);

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
        addBlock(-3, -4, -3, mPillarDown);
        addBlock(-3, -3, -3, mPillar);
        addBlock(-3, -2, -3, mPillarUp);
        addBlock(-3, -1, -3, mEngraved);
        addBlock(-3, -5,  3, mRuned);
        addBlock(-3, -4,  3, mPillarDown);
        addBlock(-3, -3,  3, mPillar);
        addBlock(-3, -2,  3, mPillarUp);
        addBlock(-3, -1,  3, mEngraved);
        addBlock( 3, -5, -3, mRuned);
        addBlock( 3, -4, -3, mPillarDown);
        addBlock( 3, -3, -3, mPillar);
        addBlock( 3, -2, -3, mPillarUp);
        addBlock( 3, -1, -3, mEngraved);
        addBlock( 3, -5,  3, mRuned);
        addBlock( 3, -4,  3, mPillarDown);
        addBlock( 3, -3,  3, mPillar);
        addBlock( 3, -2,  3, mPillarUp);
        addBlock( 3, -1,  3, mEngraved);

        addBlock(-5, -5, -3, mPillarDown);
        addBlock(-5, -4, -3, mPillar);
        addBlock(-5, -3, -3, mPillar);
        addBlock(-5, -2, -3, mPillarUp);
        addBlock(-5, -1, -3, mChisel);
        addBlock(-3, -5, -5, mPillarDown);
        addBlock(-3, -4, -5, mPillar);
        addBlock(-3, -3, -5, mPillar);
        addBlock(-3, -2, -5, mPillarUp);
        addBlock(-3, -1, -5, mChisel);
        addBlock( 5, -5, -3, mPillarDown);
        addBlock( 5, -4, -3, mPillar);
        addBlock( 5, -3, -3, mPillar);
        addBlock( 5, -2, -3, mPillarUp);
        addBlock( 5, -1, -3, mChisel);
        addBlock( 3, -5, -5, mPillarDown);
        addBlock( 3, -4, -5, mPillar);
        addBlock( 3, -3, -5, mPillar);
        addBlock( 3, -2, -5, mPillarUp);
        addBlock( 3, -1, -5, mChisel);
        addBlock(-5, -5,  3, mPillarDown);
        addBlock(-5, -4,  3, mPillar);
        addBlock(-5, -3,  3, mPillar);
        addBlock(-5, -2,  3, mPillarUp);
        addBlock(-5, -1,  3, mChisel);
        addBlock(-3, -5,  5, mPillarDown);
        addBlock(-3, -4,  5, mPillar);
        addBlock(-3, -3,  5, mPillar);
        addBlock(-3, -2,  5, mPillarUp);
        addBlock(-3, -1,  5, mChisel);
        addBlock( 5, -5,  3, mPillarDown);
        addBlock( 5, -4,  3, mPillar);
        addBlock( 5, -3,  3, mPillar);
        addBlock( 5, -2,  3, mPillarUp);
        addBlock( 5, -1,  3, mChisel);
        addBlock( 3, -5,  5, mPillarDown);
        addBlock( 3, -4,  5, mPillar);
        addBlock( 3, -3,  5, mPillar);
        addBlock( 3, -2,  5, mPillarUp);
        addBlock( 3, -1,  5, mChisel);

        addBlock(-7, 2, -7, mBrick);
        addBlock(-7, 3, -7, mPillarDown);
        addBlock(-7, 4, -7, mPillarUp);
        addBlock(-7, 5, -7, mChisel);
        addBlock( 7, 2, -7, mBrick);
        addBlock( 7, 3, -7, mPillarDown);
        addBlock( 7, 4, -7, mPillarUp);
        addBlock( 7, 5, -7, mChisel);
        addBlock(-7, 2,  7, mBrick);
        addBlock(-7, 3,  7, mPillarDown);
        addBlock(-7, 4,  7, mPillarUp);
        addBlock(-7, 5,  7, mChisel);
        addBlock( 7, 2,  7, mBrick);
        addBlock( 7, 3,  7, mPillarDown);
        addBlock( 7, 4,  7, mPillarUp);
        addBlock( 7, 5,  7, mChisel);

        addBlock( 5, 2,  0, mBrick);
        addBlock( 5, 3,  0, mPillarDown);
        addBlock( 5, 4,  0, mPillar);
        addBlock( 5, 5,  0, mPillarUp);
        addBlock( 5, 6,  0, mChisel);
        addBlock( 5, 7,  0, mPillar);
        addBlock( 5, 8,  0, mChisel);
        addBlock(-5, 2,  0, mBrick);
        addBlock(-5, 3,  0, mPillarDown);
        addBlock(-5, 4,  0, mPillar);
        addBlock(-5, 5,  0, mPillarUp);
        addBlock(-5, 6,  0, mChisel);
        addBlock(-5, 7,  0, mPillar);
        addBlock(-5, 8,  0, mChisel);
        addBlock( 0, 2,  5, mBrick);
        addBlock( 0, 3,  5, mPillarDown);
        addBlock( 0, 4,  5, mPillar);
        addBlock( 0, 5,  5, mPillarUp);
        addBlock( 0, 6,  5, mChisel);
        addBlock( 0, 7,  5, mPillar);
        addBlock( 0, 8,  5, mChisel);
        addBlock( 0, 2, -5, mBrick);
        addBlock( 0, 3, -5, mPillarDown);
        addBlock( 0, 4, -5, mPillar);
        addBlock( 0, 5, -5, mPillarUp);
        addBlock( 0, 6, -5, mChisel);
        addBlock( 0, 7, -5, mPillar);
        addBlock( 0, 8, -5, mChisel);

        addBlock( 5, 2,  5, mRuned);
        addBlock( 5, 3,  5, mPillarDown);
        addBlock( 5, 4,  5, mPillar);
        addBlock( 5, 5,  5, mPillarUp);
        addBlock( 5, 6,  5, Blocks.SEA_LANTERN.getDefaultState());
        addBlock(-5, 2,  5, mRuned);
        addBlock(-5, 3,  5, mPillarDown);
        addBlock(-5, 4,  5, mPillar);
        addBlock(-5, 5,  5, mPillarUp);
        addBlock(-5, 6,  5, Blocks.SEA_LANTERN.getDefaultState());
        addBlock( 5, 2, -5, mRuned);
        addBlock( 5, 3, -5, mPillarDown);
        addBlock( 5, 4, -5, mPillar);
        addBlock( 5, 5, -5, mPillarUp);
        addBlock( 5, 6, -5, Blocks.SEA_LANTERN.getDefaultState());
        addBlock(-5, 2, -5, mRuned);
        addBlock(-5, 3, -5, mPillarDown);
        addBlock(-5, 4, -5, mPillar);
        addBlock(-5, 5, -5, mPillarUp);
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

        addBlock( 0, 1,  0, mPillar.with(BlockMarblePillar.WATERLOGGED, true));
        addBlock( 0, 2,  0, mPillar.with(BlockMarblePillar.WATERLOGGED, true));
        addBlock( 0, 3,  0, mPillar.with(BlockMarblePillar.WATERLOGGED, true));
        addBlock( 0, 4,  0, mChisel);
        addBlock( 0, 5,  0, Blocks.WATER.getDefaultState());
    }

}
