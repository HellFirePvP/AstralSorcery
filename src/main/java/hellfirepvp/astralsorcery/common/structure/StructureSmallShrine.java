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
 * Class: StructureSmallShrine
 * Created by HellFirePvP
 * Date: 24.07.2019 / 10:07
 */
public class StructureSmallShrine extends StructureBlockArray {

    public StructureSmallShrine() {
        super(StructureTypesAS.STYPE_SMALL.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState mRaw = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState mBrick = BlocksAS.MARBLE_BRICKS.getDefaultState();
        BlockState mChisel = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState mPillar = BlocksAS.MARBLE_PILLAR.getDefaultState();
        BlockState mPillarUp   = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.TOP);
        BlockState mPillarDown = BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, BlockMarblePillar.PillarType.BOTTOM);

        addBlockCube(mRaw,   -4, 0, -4, 4, 0, 4);
        addBlockCube(Blocks.AIR.getDefaultState(), -4, 1, -4, 4, 5, 4);
        addBlockCube(mBrick, -3, 1, -3, 3, 1, 3);
        addBlockCube(Blocks.AIR.getDefaultState(), -1, 1, -1, 1, 1, 1);

        addBlock(0, 1, 0, mPillarDown.with(BlockMarblePillar.WATERLOGGED, true));
        addBlock(0, 2, 0, mPillarUp.with(BlockMarblePillar.WATERLOGGED, true));
        addBlock(0, 3, 0, Blocks.SEA_LANTERN.getDefaultState());
        addBlock(0, 4, 0, Blocks.WATER.getDefaultState());

        addBlock( 2, 2,  2, mPillarDown);
        addBlock( 2, 3,  2, mPillar);
        addBlock( 2, 4,  2, mPillarUp);
        addBlock( 2, 5,  2, mChisel);

        addBlock( 2, 2, -2, mPillarDown);
        addBlock( 2, 3, -2, mPillar);
        addBlock( 2, 4, -2, mPillarUp);
        addBlock( 2, 5, -2, mChisel);

        addBlock(-2, 2,  2, mPillarDown);
        addBlock(-2, 3,  2, mPillar);
        addBlock(-2, 4,  2, mPillarUp);
        addBlock(-2, 5,  2, mChisel);

        addBlock(-2, 2, -2, mPillarDown);
        addBlock(-2, 3, -2, mPillar);
        addBlock(-2, 4, -2, mPillarUp);
        addBlock(-2, 5, -2, mChisel);
    }

}
