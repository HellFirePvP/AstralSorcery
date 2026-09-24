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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatternAttunementAltar
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatternAttunementAltar extends StructureBlockArray implements PillarStructure {

    public PatternAttunementAltar() {
        this.addBlock(BlocksAS.ATTUNEMENT_ALTAR.get(), 0, 0, 0);

        this.addBlockCube(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -7, -1, -8,  7, -1, -8);
        this.addBlockCube(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -7, -1,  8,  7, -1,  8);
        this.addBlockCube(BlocksAS.MARBLE_ARCH.get().defaultBlockState(), -8, -1, -7, -8, -1,  7);
        this.addBlockCube(BlocksAS.MARBLE_ARCH.get().defaultBlockState(),  8, -1, -7,  8, -1,  7);
        this.addBlockCube(BlocksAS.SOOTY_MARBLE_RAW.get().defaultBlockState(), -7, -1, -7, 7, -1, 7);

        this.addPillar(BlocksAS.MARBLE_PILLAR, -8, 0, -8, 3);
        this.addPillar(BlocksAS.MARBLE_PILLAR, -8, 0,  8, 3);
        this.addPillar(BlocksAS.MARBLE_PILLAR,  8, 0, -8, 3);
        this.addPillar(BlocksAS.MARBLE_PILLAR,  8, 0,  8, 3);

        this.addBlock(BlocksAS.MARBLE_CHISELED.get(), -8, 3, -8);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(), -8, 3,  8);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(),  8, 3, -8);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(),  8, 3,  8);

        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -9, -1, -9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -9, -1, -8);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -9, -1, -7);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -8, -1, -9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -7, -1, -9);

        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -9, -1,  9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -9, -1,  8);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -9, -1,  7);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -8, -1,  9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(), -7, -1,  9);

        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  9, -1, -9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  9, -1, -8);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  9, -1, -7);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  8, -1, -9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  7, -1, -9);

        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  9, -1,  9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  9, -1,  8);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  9, -1,  7);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  8, -1,  9);
        this.addBlock(BlocksAS.MARBLE_ARCH.get(),  7, -1,  9);
    }
}
