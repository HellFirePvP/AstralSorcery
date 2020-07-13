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
import hellfirepvp.observerlib.api.block.MatchableState;
import hellfirepvp.observerlib.api.block.SimpleMatchableBlock;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.BlockState;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The copillarete source code for this mod can be found on github.
 * Class: PatternAltarAttunement
 * Created by HellFirePvP
 * Date: 11.05.2020 / 18:06
 */
public class PatternAltarAttunement extends PatternBlockArray {
    
    public PatternAltarAttunement() {
        super(StructureTypesAS.PTYPE_ALTAR_ATTUNEMENT.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState bricks = BlocksAS.MARBLE_BRICKS.getDefaultState();
        BlockState arch = BlocksAS.MARBLE_ARCH.getDefaultState();
        BlockState sootyRaw = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlockCube(sootyRaw,-3, -1, -3, 3, -1, 3);
        addBlock(BlocksAS.ALTAR_ATTUNEMENT.getDefaultState(), 0, 0, 0);

        for (int i = -3; i <= 3; i++) {
            addBlock(arch,  4, -1,  i);
            addBlock(arch, -4, -1,  i);
            addBlock(arch,  i, -1,  4);
            addBlock(arch,  i, -1, -4);
            addBlock(bricks,  3, -1,  i);
            addBlock(bricks, -3, -1,  i);
            addBlock(bricks,  i, -1,  3);
            addBlock(bricks,  i, -1, -3);
        }
        addBlock(chiseled,  3, -1,  3);
        addBlock(chiseled,  3, -1, -3);
        addBlock(chiseled, -3, -1,  3);
        addBlock(chiseled, -3, -1, -3);

        addBlock(bricks,  2, -1,  0);
        addBlock(bricks, -2, -1,  0);
        addBlock(bricks,  0, -1,  2);
        addBlock(bricks,  0, -1, -2);

        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM),  3, 0,  3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM),  3, 0, -3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), -3, 0,  3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), -3, 0, -3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP),  3, 1,  3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP),  3, 1, -3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), -3, 1,  3);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), -3, 1, -3);

        addBlock(chiseled, 3, 2,  3);
        addBlock(chiseled, 3, 2, -3);
        addBlock(chiseled,-3, 2,  3);
        addBlock(chiseled,-3, 2, -3);
    }

    private MatchableState getPillarState(BlockMarblePillar.PillarType type) {
        return new SimpleMatchableBlock(BlocksAS.MARBLE_PILLAR) {
            @Nonnull
            @Override
            public BlockState getDescriptiveState(long tick) {
                return BlocksAS.MARBLE_PILLAR.getDefaultState().with(BlockMarblePillar.PILLAR_TYPE, type);
            }
        };
    }
}
