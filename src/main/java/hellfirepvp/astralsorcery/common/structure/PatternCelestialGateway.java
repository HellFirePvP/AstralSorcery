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
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternCelestialGateway
 * Created by HellFirePvP
 * Date: 10.09.2020 / 17:24
 */
public class PatternCelestialGateway extends PatternBlockArray {

    public PatternCelestialGateway() {
        super(StructureTypesAS.PTYPE_CELESTIAL_GATEWAY.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState arch = BlocksAS.MARBLE_ARCH.getDefaultState();
        BlockState runed = BlocksAS.MARBLE_RUNED.getDefaultState();
        BlockState engraved = BlocksAS.MARBLE_ENGRAVED.getDefaultState();
        BlockState sooty = BlocksAS.BLACK_MARBLE_RAW.getDefaultState();

        addBlock(BlocksAS.GATEWAY, BlockPos.ZERO);

        addBlockCube(arch, -3, -1, -3, 3, -1, 3);
        addBlockCube(sooty, -2, -1, -2, 2, -1, 2);

        addBlock(runed, -3, -1, -3);
        addBlock(runed,  3, -1, -3);
        addBlock(runed,  3, -1,  3);
        addBlock(runed, -3, -1,  3);

        addBlock(engraved, -3, 0, -3);
        addBlock(engraved,  3, 0, -3);
        addBlock(engraved,  3, 0,  3);
        addBlock(engraved, -3, 0,  3);
    }
}
