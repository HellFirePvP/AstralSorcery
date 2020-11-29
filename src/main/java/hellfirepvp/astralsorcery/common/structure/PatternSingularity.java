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
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternSingularity
 * Created by HellFirePvP
 * Date: 29.10.2020 / 19:59
 */
public class PatternSingularity extends PatternBlockArray {

    public PatternSingularity() {
        super(StructureTypesAS.PTYPE_SINGULARITY.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState engraved = BlocksAS.MARBLE_ENGRAVED.getDefaultState();
        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState sooty = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlockCube(runed, -4, -4, -4,  4, -4,  4);
        addBlockCube(sooty, -3, -4, -3,  3, -4,  3);

        addBlockCube(runed, -4, -4, -4, -2, -4, -2);
        addBlockCube(runed, -4, -4,  4, -2, -4,  2);
        addBlockCube(runed,  4, -4, -4,  2, -4, -2);
        addBlockCube(runed,  4, -4,  4,  2, -4,  2);

        addBlock(sooty, -3, -5, -3);
        addBlock(sooty, -3, -5,  3);
        addBlock(sooty,  3, -5, -3);
        addBlock(sooty,  3, -5,  3);
        addBlock(BlocksAS.FLUID_LIQUID_STARLIGHT.getDefaultState(), -3, -4, -3);
        addBlock(BlocksAS.FLUID_LIQUID_STARLIGHT.getDefaultState(), -3, -4,  3);
        addBlock(BlocksAS.FLUID_LIQUID_STARLIGHT.getDefaultState(),  3, -4, -3);
        addBlock(BlocksAS.FLUID_LIQUID_STARLIGHT.getDefaultState(),  3, -4,  3);

        addBlock(engraved, -4, -4, -4);
        addBlock(engraved, -4, -4,  4);
        addBlock(engraved,  4, -4, -4);
        addBlock(engraved,  4, -4,  4);
        addBlock(engraved, -4, -4, -2);
        addBlock(engraved, -4, -4,  2);
        addBlock(engraved,  4, -4, -2);
        addBlock(engraved,  4, -4,  2);
        addBlock(engraved, -2, -4, -4);
        addBlock(engraved, -2, -4,  4);
        addBlock(engraved,  2, -4, -4);
        addBlock(engraved,  2, -4,  4);
        addBlock(engraved, -2, -4, -2);
        addBlock(engraved, -2, -4,  2);
        addBlock(engraved,  2, -4, -2);
        addBlock(engraved,  2, -4,  2);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), -4, -3, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM), -4, -3,  4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM),  4, -3, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.BOTTOM),  4, -3,  4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), -4, -2, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP), -4, -2,  4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP),  4, -2, -4);
        addBlock(getPillarState(BlockMarblePillar.PillarType.TOP),  4, -2,  4);
        addBlock(chiseled, -4, -1, -4);
        addBlock(chiseled, -4, -1,  4);
        addBlock(chiseled,  4, -1, -4);
        addBlock(chiseled,  4, -1,  4);

        addBlock(Blocks.GOLD_BLOCK, 0, -4, 0);
        addBlock(chiseled,  1, -4,  0);
        addBlock(chiseled,  0, -4,  1);
        addBlock(chiseled, -1, -4,  0);
        addBlock(chiseled,  0, -4, -1);

        addBlock(chiseled, -2, -3, -2);
        addBlock(chiseled, -2, -3,  2);
        addBlock(chiseled,  2, -3, -2);
        addBlock(chiseled,  2, -3,  2);
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
