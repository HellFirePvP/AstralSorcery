/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.marble.BlockMarblePillar;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
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

        addBlock(Blocks.SEA_LANTERN.getDefaultState(),  0, 0,  0);
        addBlock(mChisel,  1, 0,  0);
        addBlock(mChisel, -1, 0,  0);
        addBlock(mChisel,  0, 0,  1);
        addBlock(mChisel,  0, 0, -1);

        addBlock(Blocks.WATER.getDefaultState(), -5, -2,  0);
        addBlock(Blocks.WATER.getDefaultState(),  5, -2,  0);
        addBlock(Blocks.WATER.getDefaultState(),  0, -2, -5);
        addBlock(Blocks.WATER.getDefaultState(),  0, -2,  5);

        addBlock(air,  2, -6,  0);
        addBlock(air,  3, -6,  0);
        addBlock(air,  4, -6,  0);
        addBlock(air, -2, -6,  0);
        addBlock(air, -3, -6,  0);
        addBlock(air, -4, -6,  0);
        addBlock(air,  0, -6,  2);
        addBlock(air,  0, -6,  3);
        addBlock(air,  0, -6,  4);
        addBlock(air,  0, -6, -2);
        addBlock(air,  0, -6, -3);
        addBlock(air,  0, -6, -4);

        addBlock(mArch,  5, -6,  0);
        addBlock(mArch, -5, -6,  0);
        addBlock(mArch,  0, -6,  5);
        addBlock(mArch,  0, -6, -5);
        addBlock(mArch,  2, -6,  1);
        addBlock(mArch,  3, -6,  1);
        addBlock(mArch,  4, -6,  1);
        addBlock(mArch, -2, -6,  1);
        addBlock(mArch, -3, -6,  1);
        addBlock(mArch, -4, -6,  1);
        addBlock(mArch,  2, -6, -1);
        addBlock(mArch,  3, -6, -1);
        addBlock(mArch,  4, -6, -1);
        addBlock(mArch, -2, -6, -1);
        addBlock(mArch, -3, -6, -1);
        addBlock(mArch, -4, -6, -1);
        addBlock(mArch,  1, -6,  2);
        addBlock(mArch,  1, -6,  3);
        addBlock(mArch,  1, -6,  4);
        addBlock(mArch,  1, -6, -2);
        addBlock(mArch,  1, -6, -3);
        addBlock(mArch,  1, -6, -4);
        addBlock(mArch, -1, -6,  2);
        addBlock(mArch, -1, -6,  3);
        addBlock(mArch, -1, -6,  4);
        addBlock(mArch, -1, -6, -2);
        addBlock(mArch, -1, -6, -3);
        addBlock(mArch, -1, -6, -4);

        addBlock(mRuned, -3, -5, -3);
        addBlock(mPillarDown, -3, -4, -3);
        addBlock(mPillar, -3, -3, -3);
        addBlock(mPillarUp, -3, -2, -3);
        addBlock(mEngraved, -3, -1, -3);
        addBlock(mRuned, -3, -5,  3);
        addBlock(mPillarDown, -3, -4,  3);
        addBlock(mPillar, -3, -3,  3);
        addBlock(mPillarUp, -3, -2,  3);
        addBlock(mEngraved, -3, -1,  3);
        addBlock(mRuned,  3, -5, -3);
        addBlock(mPillarDown,  3, -4, -3);
        addBlock(mPillar,  3, -3, -3);
        addBlock(mPillarUp,  3, -2, -3);
        addBlock(mEngraved,  3, -1, -3);
        addBlock(mRuned,  3, -5,  3);
        addBlock(mPillarDown,  3, -4,  3);
        addBlock(mPillar,  3, -3,  3);
        addBlock(mPillarUp,  3, -2,  3);
        addBlock(mEngraved,  3, -1,  3);

        addBlock(mPillarDown, -5, -5, -3);
        addBlock(mPillar, -5, -4, -3);
        addBlock(mPillar, -5, -3, -3);
        addBlock(mPillarUp, -5, -2, -3);
        addBlock(mChisel, -5, -1, -3);
        addBlock(mPillarDown, -3, -5, -5);
        addBlock(mPillar, -3, -4, -5);
        addBlock(mPillar, -3, -3, -5);
        addBlock(mPillarUp, -3, -2, -5);
        addBlock(mChisel, -3, -1, -5);
        addBlock(mPillarDown,  5, -5, -3);
        addBlock(mPillar,  5, -4, -3);
        addBlock(mPillar,  5, -3, -3);
        addBlock(mPillarUp,  5, -2, -3);
        addBlock(mChisel,  5, -1, -3);
        addBlock(mPillarDown,  3, -5, -5);
        addBlock(mPillar,  3, -4, -5);
        addBlock(mPillar,  3, -3, -5);
        addBlock(mPillarUp,  3, -2, -5);
        addBlock(mChisel,  3, -1, -5);
        addBlock(mPillarDown, -5, -5,  3);
        addBlock(mPillar, -5, -4,  3);
        addBlock(mPillar, -5, -3,  3);
        addBlock(mPillarUp, -5, -2,  3);
        addBlock(mChisel, -5, -1,  3);
        addBlock(mPillarDown, -3, -5,  5);
        addBlock(mPillar, -3, -4,  5);
        addBlock(mPillar, -3, -3,  5);
        addBlock(mPillarUp, -3, -2,  5);
        addBlock(mChisel, -3, -1,  5);
        addBlock(mPillarDown,  5, -5,  3);
        addBlock(mPillar,  5, -4,  3);
        addBlock(mPillar,  5, -3,  3);
        addBlock(mPillarUp,  5, -2,  3);
        addBlock(mChisel,  5, -1,  3);
        addBlock(mPillarDown,  3, -5,  5);
        addBlock(mPillar,  3, -4,  5);
        addBlock(mPillar,  3, -3,  5);
        addBlock(mPillarUp,  3, -2,  5);
        addBlock(mChisel,  3, -1,  5);

        addBlock(mBrick, -7, 2, -7);
        addBlock(mPillarDown, -7, 3, -7);
        addBlock(mPillarUp, -7, 4, -7);
        addBlock(mChisel, -7, 5, -7);
        addBlock(mBrick,  7, 2, -7);
        addBlock(mPillarDown,  7, 3, -7);
        addBlock(mPillarUp,  7, 4, -7);
        addBlock(mChisel,  7, 5, -7);
        addBlock(mBrick, -7, 2,  7);
        addBlock(mPillarDown, -7, 3,  7);
        addBlock(mPillarUp, -7, 4,  7);
        addBlock(mChisel, -7, 5,  7);
        addBlock(mBrick,  7, 2,  7);
        addBlock(mPillarDown,  7, 3,  7);
        addBlock(mPillarUp,  7, 4,  7);
        addBlock(mChisel,  7, 5,  7);

        addBlock(mBrick,  5, 2,  0);
        addBlock(mPillarDown,  5, 3,  0);
        addBlock(mPillar,  5, 4,  0);
        addBlock(mPillarUp,  5, 5,  0);
        addBlock(mChisel,  5, 6,  0);
        addBlock(mPillar,  5, 7,  0);
        addBlock(mChisel,  5, 8,  0);
        addBlock(mBrick, -5, 2,  0);
        addBlock(mPillarDown, -5, 3,  0);
        addBlock(mPillar, -5, 4,  0);
        addBlock(mPillarUp, -5, 5,  0);
        addBlock(mChisel, -5, 6,  0);
        addBlock(mPillar, -5, 7,  0);
        addBlock(mChisel, -5, 8,  0);
        addBlock(mBrick,  0, 2,  5);
        addBlock(mPillarDown,  0, 3,  5);
        addBlock(mPillar,  0, 4,  5);
        addBlock(mPillarUp,  0, 5,  5);
        addBlock(mChisel,  0, 6,  5);
        addBlock(mPillar,  0, 7,  5);
        addBlock(mChisel,  0, 8,  5);
        addBlock(mBrick,  0, 2, -5);
        addBlock(mPillarDown,  0, 3, -5);
        addBlock(mPillar,  0, 4, -5);
        addBlock(mPillarUp,  0, 5, -5);
        addBlock(mChisel,  0, 6, -5);
        addBlock(mPillar,  0, 7, -5);
        addBlock(mChisel,  0, 8, -5);

        addBlock(mRuned,  5, 2,  5);
        addBlock(mPillarDown,  5, 3,  5);
        addBlock(mPillar,  5, 4,  5);
        addBlock(mPillarUp,  5, 5,  5);
        addBlock(Blocks.SEA_LANTERN.getDefaultState(),  5, 6,  5);
        addBlock(mRuned, -5, 2,  5);
        addBlock(mPillarDown, -5, 3,  5);
        addBlock(mPillar, -5, 4,  5);
        addBlock(mPillarUp, -5, 5,  5);
        addBlock(Blocks.SEA_LANTERN.getDefaultState(), -5, 6,  5);
        addBlock(mRuned,  5, 2, -5);
        addBlock(mPillarDown,  5, 3, -5);
        addBlock(mPillar,  5, 4, -5);
        addBlock(mPillarUp,  5, 5, -5);
        addBlock(Blocks.SEA_LANTERN.getDefaultState(),  5, 6, -5);
        addBlock(mRuned, -5, 2, -5);
        addBlock(mPillarDown, -5, 3, -5);
        addBlock(mPillar, -5, 4, -5);
        addBlock(mPillarUp, -5, 5, -5);
        addBlock(Blocks.SEA_LANTERN.getDefaultState(), -5, 6, -5);

        addBlock(mArch,  5, 6,  4);
        addBlock(mArch,  5, 6,  3);
        addBlock(mArch,  5, 6,  2);
        addBlock(mArch,  5, 6,  1);
        addBlock(mArch,  5, 6, -1);
        addBlock(mArch,  5, 6, -2);
        addBlock(mArch,  5, 6, -3);
        addBlock(mArch,  5, 6, -4);
        addBlock(mArch, -5, 6,  4);
        addBlock(mArch, -5, 6,  3);
        addBlock(mArch, -5, 6,  2);
        addBlock(mArch, -5, 6,  1);
        addBlock(mArch, -5, 6, -1);
        addBlock(mArch, -5, 6, -2);
        addBlock(mArch, -5, 6, -3);
        addBlock(mArch, -5, 6, -4);
        addBlock(mArch,  4, 6,  5);
        addBlock(mArch,  3, 6,  5);
        addBlock(mArch,  2, 6,  5);
        addBlock(mArch,  1, 6,  5);
        addBlock(mArch, -1, 6,  5);
        addBlock(mArch, -2, 6,  5);
        addBlock(mArch, -3, 6,  5);
        addBlock(mArch, -4, 6,  5);
        addBlock(mArch,  4, 6, -5);
        addBlock(mArch,  3, 6, -5);
        addBlock(mArch,  2, 6, -5);
        addBlock(mArch,  1, 6, -5);
        addBlock(mArch, -1, 6, -5);
        addBlock(mArch, -2, 6, -5);
        addBlock(mArch, -3, 6, -5);
        addBlock(mArch, -4, 6, -5);

        addBlock(mRaw,  4, 1,  4);
        addBlock(mRaw,  3, 1,  4);
        addBlock(mRaw,  4, 1,  3);
        addBlock(mRaw, -4, 1,  4);
        addBlock(mRaw, -3, 1,  4);
        addBlock(mRaw, -4, 1,  3);
        addBlock(mRaw,  4, 1, -4);
        addBlock(mRaw,  3, 1, -4);
        addBlock(mRaw,  4, 1, -3);
        addBlock(mRaw, -4, 1, -4);
        addBlock(mRaw, -3, 1, -4);
        addBlock(mRaw, -4, 1, -3);

        addBlock(mBrick,  4, 6,  4);
        addBlock(mBrick,  4, 7,  4);
        addBlock(mBrick,  3, 7,  3);
        addBlock(mBrick,  3, 8,  3);
        addBlock(mBrick, -4, 6,  4);
        addBlock(mBrick, -4, 7,  4);
        addBlock(mBrick, -3, 7,  3);
        addBlock(mBrick, -3, 8,  3);
        addBlock(mBrick,  4, 6, -4);
        addBlock(mBrick,  4, 7, -4);
        addBlock(mBrick,  3, 7, -3);
        addBlock(mBrick,  3, 8, -3);
        addBlock(mBrick, -4, 6, -4);
        addBlock(mBrick, -4, 7, -4);
        addBlock(mBrick, -3, 7, -3);
        addBlock(mBrick, -3, 8, -3);

        addBlock(mBrick,  2, 8,  3);
        addBlock(mBrick,  2, 9,  3);
        addBlock(mBrick,  3, 8,  2);
        addBlock(mBrick,  3, 9,  2);
        addBlock(mRuned,  2, 9,  2);
        addBlock(mBrick, -2, 8,  3);
        addBlock(mBrick, -2, 9,  3);
        addBlock(mBrick, -3, 8,  2);
        addBlock(mBrick, -3, 9,  2);
        addBlock(mRuned, -2, 9,  2);
        addBlock(mBrick,  2, 8, -3);
        addBlock(mBrick,  2, 9, -3);
        addBlock(mBrick,  3, 8, -2);
        addBlock(mBrick,  3, 9, -2);
        addBlock(mRuned,  2, 9, -2);
        addBlock(mBrick, -2, 8, -3);
        addBlock(mBrick, -2, 9, -3);
        addBlock(mBrick, -3, 8, -2);
        addBlock(mBrick, -3, 9, -2);
        addBlock(mRuned, -2, 9, -2);

        addBlock(mBrick,  1, 9,  3);
        addBlock(mBrick,  0, 9,  3);
        addBlock(mBrick, -1, 9,  3);
        addBlock(mBrick,  1, 9, -3);
        addBlock(mBrick,  0, 9, -3);
        addBlock(mBrick, -1, 9, -3);
        addBlock(mBrick,  3, 9,  1);
        addBlock(mBrick,  3, 9,  0);
        addBlock(mBrick,  3, 9, -1);
        addBlock(mBrick, -3, 9,  1);
        addBlock(mBrick, -3, 9,  0);
        addBlock(mBrick, -3, 9, -1);

        addBlock(air,  2, 10,  2);
        addBlock(air, -2, 10,  2);
        addBlock(air,  2, 10, -2);
        addBlock(air, -2, 10, -2);

        addBlock(mPillar.with(BlockMarblePillar.WATERLOGGED, true), 0, 1,  0);
        addBlock(mPillar.with(BlockMarblePillar.WATERLOGGED, true), 0, 2,  0);
        addBlock(mPillar.with(BlockMarblePillar.WATERLOGGED, true), 0, 3,  0);
        addBlock(mChisel,  0, 4,  0);
        addBlock(Blocks.WATER.getDefaultState(),  0, 5,  0);
    }

}
