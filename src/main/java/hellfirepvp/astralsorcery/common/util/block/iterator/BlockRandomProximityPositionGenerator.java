/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block.iterator;

import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRandomProximityPositionGenerator
 * Created by HellFirePvP
 * Date: 01.12.2019 / 10:01
 */
public class BlockRandomProximityPositionGenerator extends BlockRandomPositionGenerator {

    @Override
    protected BlockPos genNext(BlockPos offset, double radius) {
        BlockPos next1 = super.genNext(offset, radius);
        BlockPos next2 = super.genNext(offset, radius);
        return next1.distanceSq(offset) < next2.distanceSq(offset) ? next1 : next2;
    }
}
