/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.world.level.block.Blocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatternAltarT2Expanded
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatternAltarT2Expanded extends PatternAltarT2 {

    public PatternAltarT2Expanded() {
        this.addBlockCube(BlocksAS.MARBLE_SLAB.get().defaultBlockState(), -3, 0, -2, -3, 0,  2);
        this.addBlockCube(BlocksAS.MARBLE_SLAB.get().defaultBlockState(),  3, 0, -2,  3, 0,  2);
        this.addBlockCube(BlocksAS.MARBLE_SLAB.get().defaultBlockState(), -2, 0, -3,  2, 0, -3);
        this.addBlockCube(BlocksAS.MARBLE_SLAB.get().defaultBlockState(), -2, 0,  3,  2, 0,  3);

        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -3, 0, -3);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  3, 0, -3);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -3, 0,  3);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  3, 0,  3);

        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -4, 0, -4, -4, 0,  4);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(),  4, 0, -4,  4, 0,  4);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -4, 0, -4,  4, 0, -4);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -4, 0,  4,  4, 0,  4);

        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -5, 0, -4, -5, 0,  4);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(),  5, 0, -4,  5, 0,  4);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -4, 0, -5,  4, 0, -5);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -4, 0,  5,  4, 0,  5);

        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  5, 0,  0);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -5, 0,  0);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  0, 0,  5);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  0, 0, -5);

        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -4, 1,  0);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -4, 1, -2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -4, 1, -4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -2, 1, -4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  0, 1, -4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  2, 1, -4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  4, 1, -4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  4, 1, -2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  4, 1,  0);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  4, 1,  2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  4, 1,  4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  2, 1,  4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  0, 1,  4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -2, 1,  4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -4, 1,  4);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -4, 1,  2);
    }
}
