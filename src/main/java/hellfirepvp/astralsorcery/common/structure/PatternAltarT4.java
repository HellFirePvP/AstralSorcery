/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatternAltarT4
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatternAltarT4 extends PatternAltarT3 {

    public PatternAltarT4() {
        this.addBlock(BlocksAS.ALTAR_RADIANCE.get().defaultBlockState(), 0, 0, 0);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -6, 0, -6, -6, 0,  6);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(),  6, 0, -6,  6, 0,  6);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -6, 0, -6,  6, 0, -6);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -6, 0,  6,  6, 0,  6);

        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -7, 0, -7, -7, 0,  7);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(),  7, 0, -7,  7, 0,  7);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -7, 0, -7,  7, 0, -7);
        this.addBlockCube(BlocksAS.MARBLE_RUNED.get().defaultBlockState(), -7, 0,  7,  7, 0,  7);

        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  7, 0,  7);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -7, 0,  7);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  7, 0, -7);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -7, 0, -7);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  7, 0,  0);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -7, 0,  0);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  0, 0,  7);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  0, 0, -7);

        this.addPillar(BlocksAS.MARBLE_PILLAR,  7, 1,  7, 2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  7, 3,  7);
        this.addPillar(BlocksAS.MARBLE_PILLAR, -7, 1,  7, 2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -7, 3,  7);
        this.addPillar(BlocksAS.MARBLE_PILLAR, -7, 1, -7, 2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -7, 3, -7);
        this.addPillar(BlocksAS.MARBLE_PILLAR,  7, 1, -7, 2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  7, 3, -7);
    }
}
