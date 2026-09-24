/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.block.PillarBlock;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.observerlib.api.util.StructureBlockArray;
import net.minecraft.world.level.block.Blocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatternInfuser
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatternInfuser extends StructureBlockArray implements PillarStructure {

    public PatternInfuser() {
        this.addBlock(BlocksAS.INFUSER.get(), 0, 0, 0);

        this.addBlock(BlocksAS.MARBLE_RAW.get(), -2, -2, -2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(), -2, -2, -1);
        this.addBlock(BlocksAS.MARBLE_RAW.get(), -2, -2,  0);
        this.addBlock(BlocksAS.MARBLE_RAW.get(), -2, -2,  1);
        this.addBlock(BlocksAS.MARBLE_RAW.get(), -2, -2,  2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  2, -2, -2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  2, -2, -1);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  2, -2,  0);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  2, -2,  1);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  2, -2,  2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(), -1, -2, -2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  0, -2, -2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  1, -2, -2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(), -1, -2,  2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  0, -2,  2);
        this.addBlock(BlocksAS.MARBLE_RAW.get(),  1, -2,  2);

        this.addBlock(Blocks.LAPIS_BLOCK.defaultBlockState(), 0, -1, 0);

        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1,  1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  0, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  0, -1,  1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1,  1);

        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1,  0);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1,  0);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1,  1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1,  1);

        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1, -3);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -1, -1,  3);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  0, -1, -3);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  0, -1,  3);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1, -3);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  1, -1,  3);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  3, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -3, -1, -1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  3, -1,  0);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -3, -1,  0);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(),  3, -1,  1);
        this.addBlock(BlocksAS.MARBLE_RUNED.get(), -3, -1,  1);

        this.addBlock(BlocksAS.MARBLE_CHISELED.get(), -2, -1, -2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(),  2, -1, -2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(), -2, -1,  2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(),  2, -1,  2);
        this.addBlock(this.getPillarState(BlocksAS.MARBLE_PILLAR.get(), PillarBlock.PillarType.MIDDLE), -2,  0, -2);
        this.addBlock(this.getPillarState(BlocksAS.MARBLE_PILLAR.get(), PillarBlock.PillarType.MIDDLE),  2,  0, -2);
        this.addBlock(this.getPillarState(BlocksAS.MARBLE_PILLAR.get(), PillarBlock.PillarType.MIDDLE), -2,  0,  2);
        this.addBlock(this.getPillarState(BlocksAS.MARBLE_PILLAR.get(), PillarBlock.PillarType.MIDDLE),  2,  0,  2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(), -2,  1, -2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(),  2,  1, -2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(), -2,  1,  2);
        this.addBlock(BlocksAS.MARBLE_CHISELED.get(),  2,  1,  2);
    }
}
