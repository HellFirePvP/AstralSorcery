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
import net.minecraft.block.BlockState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternSpectralRelay
 * Created by HellFirePvP
 * Date: 16.11.2019 / 17:48
 */
public class PatternSpectralRelay extends PatternBlockArray {

    public PatternSpectralRelay() {
        super(StructureTypesAS.PTYPE_SPECTRAL_RELAY.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        addBlock(BlocksAS.SPECTRAL_RELAY.getDefaultState(), 0, 0, 0);

        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState arch = BlocksAS.MARBLE_ARCH.getDefaultState();
        BlockState sooty = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlock(sooty, 0, -1, 0);

        addBlock(chiseled, -1, -1, -1);
        addBlock(chiseled,  1, -1, -1);
        addBlock(chiseled, -1, -1,  1);
        addBlock(chiseled,  1, -1,  1);

        addBlock(arch, -1, -1,  0);
        addBlock(arch,  1, -1,  0);
        addBlock(arch,  0, -1, -1);
        addBlock(arch,  0, -1,  1);
    }
}
