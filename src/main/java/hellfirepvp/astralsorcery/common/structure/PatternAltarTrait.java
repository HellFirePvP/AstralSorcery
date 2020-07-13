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
 * Class: PatternAltarTrait
 * Created by HellFirePvP
 * Date: 11.05.2020 / 18:15
 */
public class PatternAltarTrait extends PatternBlockArray {

    public PatternAltarTrait() {
        super(StructureTypesAS.PTYPE_ALTAR_TRAIT.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState bricks = BlocksAS.MARBLE_BRICKS.getDefaultState();

        StructureTypesAS.PTYPE_ALTAR_CONSTELLATION.getStructure().getContents().forEach((pos, state) ->
                this.addBlock(state.getDescriptiveState(0), pos.getX(), pos.getY(), pos.getZ()));

        addBlock(BlocksAS.ALTAR_RADIANCE.getDefaultState(), 0, 0, 0);

        addBlock(bricks, 4, 3, 3);
        addBlock(bricks, 4, 3, -3);
        addBlock(bricks, -4, 3, 3);
        addBlock(bricks, -4, 3, -3);
        addBlock(bricks, 3, 3, 4);
        addBlock(bricks, -3, 3, 4);
        addBlock(bricks, 3, 3, -4);
        addBlock(bricks, -3, 3,-4);
        addBlock(bricks, 3, 4, 3);
        addBlock(bricks, 3, 4, 2);
        addBlock(bricks, 3, 4, 1);
        addBlock(bricks, 3, 4, -1);
        addBlock(bricks, 3, 4, -2);
        addBlock(bricks, 3, 4, -3);
        addBlock(bricks, 2, 4, -3);
        addBlock(bricks, 1, 4, -3);
        addBlock(bricks, -1, 4, -3);
        addBlock(bricks, -2, 4, -3);
        addBlock(bricks, -3, 4, -3);
        addBlock(bricks, -3, 4, -2);
        addBlock(bricks, -3, 4, -1);
        addBlock(bricks, -3, 4, 1);
        addBlock(bricks, -3, 4, 2);
        addBlock(bricks, -3, 4, 3);
        addBlock(bricks, -2, 4, 3);
        addBlock(bricks, -1, 4, 3);
        addBlock(bricks, 1, 4, 3);
        addBlock(bricks, 2, 4, 3);
    }
}
