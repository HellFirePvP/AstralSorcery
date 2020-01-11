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
import net.minecraft.block.Blocks;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatternRitualPedestal
 * Created by HellFirePvP
 * Date: 09.07.2019 / 19:38
 */
public class PatternRitualPedestal extends PatternBlockArray {

    public PatternRitualPedestal() {
        super(StructureTypesAS.PTYPE_RITUAL_PEDESTAL.getRegistryName());

        makeStructure();
    }

    private void makeStructure() {
        BlockState air = Blocks.AIR.getDefaultState();

        addBlockCube(air, -2, 0, -2, 2, 2, 2);
        addBlockCube(air, -3, 0, -1, 3, 2, 1);
        addBlockCube(air, -1, 0, -3, 1, 2, 3);

        addBlock(BlocksAS.RITUAL_PEDESTAL.getDefaultState(), 0, 0, 0);

        BlockState chiseled = BlocksAS.MARBLE_CHISELED.getDefaultState();
        BlockState bricks = BlocksAS.MARBLE_BRICKS.getDefaultState();
        BlockState raw = BlocksAS.MARBLE_RAW.getDefaultState();
        BlockState arch = BlocksAS.MARBLE_ARCH.getDefaultState();

        addBlock(chiseled, 0, -1, 0);

        addBlock(bricks,  0, -1,  1);
        addBlock(bricks,  0, -1,  2);
        addBlock(bricks,  0, -1,  3);
        addBlock(bricks,  1, -1,  3);
        addBlock(bricks, -1, -1,  3);
        addBlock(bricks,  0, -1, -1);
        addBlock(bricks,  0, -1, -2);
        addBlock(bricks,  0, -1, -3);
        addBlock(bricks,  1, -1, -3);
        addBlock(bricks, -1, -1, -3);
        addBlock(bricks,  1, -1,  0);
        addBlock(bricks,  2, -1,  0);
        addBlock(bricks,  3, -1,  0);
        addBlock(bricks,  3, -1,  1);
        addBlock(bricks,  3, -1, -1);
        addBlock(bricks, -1, -1,  0);
        addBlock(bricks, -2, -1,  0);
        addBlock(bricks, -3, -1,  0);
        addBlock(bricks, -3, -1,  1);
        addBlock(bricks, -3, -1, -1);
        addBlock(bricks,  2, -1,  2);
        addBlock(bricks, -2, -1,  2);
        addBlock(bricks,  2, -1, -2);
        addBlock(bricks, -2, -1, -2);

        addBlock(raw,  1, -1,  1);
        addBlock(raw,  1, -1,  2);
        addBlock(raw,  2, -1,  1);

        addBlock(raw, -1, -1,  1);
        addBlock(raw, -1, -1,  2);
        addBlock(raw, -2, -1,  1);

        addBlock(raw,  1, -1, -1);
        addBlock(raw,  1, -1, -2);
        addBlock(raw,  2, -1, -1);

        addBlock(raw, -1, -1, -1);
        addBlock(raw, -1, -1, -2);
        addBlock(raw, -2, -1, -1);

        addBlock(arch,  0, -1,  4);
        addBlock(arch,  1, -1,  4);
        addBlock(arch, -1, -1,  4);

        addBlock(arch,  0, -1, -4);
        addBlock(arch,  1, -1, -4);
        addBlock(arch, -1, -1, -4);

        addBlock(arch,  4, -1,  0);
        addBlock(arch,  4, -1,  1);
        addBlock(arch,  4, -1, -1);

        addBlock(arch, -4, -1,  0);
        addBlock(arch, -4, -1,  1);
        addBlock(arch, -4, -1, -1);

        addBlock(arch,  3, -1,  2);
        addBlock(arch,  3, -1, -2);
        addBlock(arch, -3, -1,  2);
        addBlock(arch, -3, -1, -2);

        addBlock(arch,  2, -1,  3);
        addBlock(arch, -2, -1,  3);
        addBlock(arch,  2, -1, -3);
        addBlock(arch, -2, -1, -3);
    }

}
