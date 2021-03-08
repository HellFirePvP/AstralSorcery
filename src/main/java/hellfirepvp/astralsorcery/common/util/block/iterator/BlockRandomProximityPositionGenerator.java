/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block.iterator;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
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
    protected BlockPos genNext(Vector3 offset, double radius) {
        BlockPos next1 = super.genNext(offset, radius);
        BlockPos next2 = super.genNext(offset, radius);
        return offset.distance(next1) < offset.distance(next2) ? next1 : next2;
    }
}
