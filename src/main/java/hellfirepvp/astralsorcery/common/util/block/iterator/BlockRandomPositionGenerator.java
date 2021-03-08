/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block.iterator;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRandomPositionGenerator
 * Created by HellFirePvP
 * Date: 24.11.2019 / 10:06
 */
public class BlockRandomPositionGenerator extends BlockPositionGenerator {

    @Override
    protected BlockPos genNext(Vector3 offset, double radius) {
        if (radius <= 0) {
            return offset.toBlockPos();
        }
        return offset.clone().add(Vector3.random().multiply(radius)).toBlockPos();
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {}

    @Override
    public void readFromNBT(CompoundNBT nbt) {}
}
