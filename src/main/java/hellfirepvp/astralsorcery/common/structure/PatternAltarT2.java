/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import net.minecraft.world.level.block.Blocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatternAltarT2
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatternAltarT2 extends StructureBlockArray {

    public PatternAltarT2() {
        this.addBlock(BlocksAS.ALTAR_RESONANCE.get().defaultBlockState(), 0, 0, 0);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -2, -1, -2, 2, -1, 2);

        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -3, -1, -2, -3, -1,  2);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(),  3, -1, -2,  3, -1,  2);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -2, -1, -3,  2, -1, -3);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -2, -1,  3,  2, -1,  3);

        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -2, 0,  0);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -2, 0, -2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  0, 0, -2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  2, 0, -2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  2, 0,  0);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  2, 0,  2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(),  0, 0,  2);
        this.addBlock(BlocksAS.FOCUS_RELAY.get().defaultBlockState(), -2, 0,  2);

    }

}
