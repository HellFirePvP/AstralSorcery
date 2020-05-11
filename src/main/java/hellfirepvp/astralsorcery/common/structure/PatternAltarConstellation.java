/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The copillarete source code for this mod can be found on github.
 * Class: PatternAltarConstellation
 * Created by HellFirePvP
 * Date: 11.05.2020 / 18:12
 */
public class PatternAltarConstellation extends PatternBlockArray {

    public PatternAltarConstellation() {
        super(StructureTypesAS.PTYPE_ALTAR_CONSTELLATION.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState raw = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState bricks = BlocksAS.MARBLE_BRICKS.getDefaultState();
        Block pillar = BlocksAS.MARBLE_PILLAR;
        BlockState sootyRaw = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlockCube(bricks, -4, -1, -4,  4, -1,  4);
        addBlockCube(sootyRaw, -3, -1, -3,  3, -1,  3);
        addBlock(BlocksAS.ALTAR_CONSTELLATION.getDefaultState(), 0, 0, 0);

        addBlock(raw, -4, -1, -4);
        addBlock(raw, -4, -1, -3);
        addBlock(raw, -3, -1, -4);
        addBlock(raw,  4, -1, -4);
        addBlock(raw,  4, -1, -3);
        addBlock(raw,  3, -1, -4);
        addBlock(raw, -4, -1,  4);
        addBlock(raw, -4, -1,  3);
        addBlock(raw, -3, -1,  4);
        addBlock(raw,  4, -1,  4);
        addBlock(raw,  4, -1,  3);
        addBlock(raw,  3, -1,  4);
        addBlock(bricks, -5, -1, -5);
        addBlock(bricks, -5, -1, -4);
        addBlock(bricks, -5, -1, -3);
        addBlock(bricks, -4, -1, -5);
        addBlock(bricks, -3, -1, -5);
        addBlock(bricks,  5, -1, -5);
        addBlock(bricks,  5, -1, -4);
        addBlock(bricks,  5, -1, -3);
        addBlock(bricks,  4, -1, -5);
        addBlock(bricks,  3, -1, -5);
        addBlock(bricks, -5, -1,  5);
        addBlock(bricks, -5, -1,  4);
        addBlock(bricks, -5, -1,  3);
        addBlock(bricks, -4, -1,  5);
        addBlock(bricks, -3, -1,  5);
        addBlock(bricks,  5, -1,  5);
        addBlock(bricks,  5, -1,  4);
        addBlock(bricks,  5, -1,  3);
        addBlock(bricks,  4, -1,  5);
        addBlock(bricks,  3, -1,  5);
        addBlock(runed, -4, 0, -4);
        addBlock(runed, -4, 0,  4);
        addBlock(runed,  4, 0, -4);
        addBlock(runed,  4, 0,  4);
        addBlock(pillar, -4, 1, -4);
        addBlock(pillar, -4, 1,  4);
        addBlock(pillar,  4, 1, -4);
        addBlock(pillar,  4, 1,  4);
        addBlock(pillar, -4, 2, -4);
        addBlock(pillar, -4, 2,  4);
        addBlock(pillar,  4, 2, -4);
        addBlock(pillar,  4, 2,  4);
        addBlock(chiseled, -4, 3, -4);
        addBlock(chiseled, -4, 3,  4);
        addBlock(chiseled,  4, 3, -4);
        addBlock(chiseled,  4, 3,  4);
    }
}
