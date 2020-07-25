/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.ore;

import hellfirepvp.astralsorcery.common.block.base.template.BlockRockTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRockCrystalOre
 * Created by HellFirePvP
 * Date: 21.07.2019 / 08:12
 */
public class BlockRockCrystalOre extends BlockRockTemplate {

    @Override
    public int getHarvestLevel(BlockState state) {
        return 2;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        return fortune * MathHelper.nextInt(RANDOM, 8, 14);
    }
}
