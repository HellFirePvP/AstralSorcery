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
 * Class: PatternAltarT3
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatternAltarT3 extends PatternAltarT2Expanded implements PillarStructure {

    public PatternAltarT3() {
        this.addBlock(BlocksAS.ALTAR_LUMINANCE.get().defaultBlockState(), 0, 0, 0);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  5, 0,  5);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -5, 0,  5);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(),  5, 0, -5);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get().defaultBlockState(), -5, 0, -5);

        this.addPillar(BlocksAS.MARBLE_PILLAR,  5, 1,  5, 3);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(),  5, 4,  5);
        this.addPillar(BlocksAS.MARBLE_PILLAR, -5, 1,  5, 3);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(), -5, 4,  5);
        this.addPillar(BlocksAS.MARBLE_PILLAR, -5, 1, -5, 3);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(), -5, 4, -5);
        this.addPillar(BlocksAS.MARBLE_PILLAR,  5, 1, -5, 3);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(),  5, 4, -5);

        this.addPillar(BlocksAS.MARBLE_PILLAR,  5, 1,  0, 5);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(),  5, 6,  0);
        this.addPillar(BlocksAS.MARBLE_PILLAR, -5, 1,  0, 5);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(), -5, 6,  0);
        this.addPillar(BlocksAS.MARBLE_PILLAR,  0, 1,  5, 5);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(),  0, 6,  5);
        this.addPillar(BlocksAS.MARBLE_PILLAR,  0, 1, -5, 5);
        this.addBlock(BlocksAS.MARBLE_ENGRAVED.get().defaultBlockState(),  0, 6, -5);

        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 6,  1);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 6, -1);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 6,  2);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 6, -2);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 6,  1);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 6, -1);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 6,  2);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 6, -2);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  1, 6,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -1, 6,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  2, 6,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -2, 6,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  1, 6, -5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -1, 6, -5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  2, 6, -5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -2, 6, -5);

        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 5,  3);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 5, -3);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 5,  4);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  5, 5, -4);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 5,  3);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 5, -3);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 5,  4);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -5, 5, -4);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  3, 5,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -3, 5,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  4, 5,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -4, 5,  5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  3, 5, -5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -3, 5, -5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  4, 5, -5);
        this.addBlock(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -4, 5, -5);
    }
}
