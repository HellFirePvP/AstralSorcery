/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.structure;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.StructureTypesAS;
import hellfirepvp.observerlib.api.util.PatternBlockArray;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternEnhancedCollectorCrystal
 * Created by HellFirePvP
 * Date: 10.08.2019 / 23:13
 */
public class PatternEnhancedCollectorCrystal extends PatternBlockArray {

    public PatternEnhancedCollectorCrystal() {
        super(StructureTypesAS.PTYPE_ENHANCED_COLLECTOR_CRYSTAL.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState raw = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState engraved = BlocksAS.MARBLE_ENGRAVED.getDefaultState();
        BlockState pillar = BlocksAS.MARBLE_PILLAR.getDefaultState();

        //TODO liquid starlight
        //TODO collector crystal

        addBlockCube(raw, -1, -5, -1, 1, -5, 1);
        addBlock(chiseled, 0, -2, 0);
        addBlock(pillar, 0, -3, 0);
        addBlock(engraved, 0, -4, 0);

        addBlock(chiseled, -2, -4, -2);
        addBlock(chiseled, -2, -4,  2);
        addBlock(chiseled,  2, -4,  2);
        addBlock(chiseled,  2, -4, -2);
        addBlock(engraved, -2, -3, -2);
        addBlock(engraved, -2, -3,  2);
        addBlock(engraved,  2, -3,  2);
        addBlock(engraved,  2, -3, -2);

        addBlock(runed, -2, -4, -1);
        addBlock(runed, -2, -4,  0);
        addBlock(runed, -2, -4,  1);
        addBlock(runed,  2, -4, -1);
        addBlock(runed,  2, -4,  0);
        addBlock(runed,  2, -4,  1);
        addBlock(runed, -1, -4, -2);
        addBlock(runed,  0, -4, -2);
        addBlock(runed,  1, -4, -2);
        addBlock(runed, -1, -4,  2);
        addBlock(runed,  0, -4,  2);
        addBlock(runed,  1, -4,  2);
    }

}
