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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternInfuser
 * Created by HellFirePvP
 * Date: 09.11.2019 / 20:12
 */
public class PatternInfuser extends PatternBlockArray {

    public PatternInfuser() {
        super(StructureTypesAS.PTYPE_INFUSER.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        addBlock(BlocksAS.INFUSER.getDefaultState(), 0, 0, 0);

        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState raw = BlocksAS.MARBLE_RAW.getDefaultState();
        Block pillar = BlocksAS.MARBLE_PILLAR;

        addBlock(Blocks.LAPIS_BLOCK.getDefaultState(), 0, -1, 0);

        for (int i = -2; i <= 2; i++) {
            addBlock(raw,     i, -2, -2);
            addBlock(raw,     i, -2,  2);
            addBlock(raw, -2, -2,     i);
            addBlock(raw,  2, -2,     i);
        }

        for (int i = -1; i <= 1; i++) {
            addBlock(runed,     i, -1, -1);
            addBlock(runed,     i, -1,  1);
            addBlock(runed, -1, -1,     i);
            addBlock(runed,  1, -1,     i);

            addBlock(runed,     i, -1, -3);
            addBlock(runed,     i, -1,  3);
            addBlock(runed,  3, -1,     i);
            addBlock(runed, -3, -1,     i);
        }

        addBlock(chiseled, -2, -1, -2);
        addBlock(chiseled,  2, -1, -2);
        addBlock(chiseled, -2, -1,  2);
        addBlock(chiseled,  2, -1,  2);
        addBlock(getPillarState(BlockMarblePillar.PillarType.MIDDLE), -2,  0, -2);
        addBlock(getPillarState(BlockMarblePillar.PillarType.MIDDLE),  2,  0, -2);
        addBlock(getPillarState(BlockMarblePillar.PillarType.MIDDLE), -2,  0,  2);
        addBlock(getPillarState(BlockMarblePillar.PillarType.MIDDLE),  2,  0,  2);
        addBlock(chiseled, -2,  1, -2);
        addBlock(chiseled,  2,  1, -2);
        addBlock(chiseled, -2,  1,  2);
        addBlock(chiseled,  2,  1,  2);
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
