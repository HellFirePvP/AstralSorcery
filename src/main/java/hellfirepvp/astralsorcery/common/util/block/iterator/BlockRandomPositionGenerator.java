/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block.iterator;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockRandomPositionGenerator
 * Created by HellFirePvP
 * Date: 24.11.2019 / 10:06
 */
public class BlockRandomPositionGenerator extends BlockPositionGenerator {

    private static final Random rand = new Random();

    @Override
    protected BlockPos genNext(BlockPos offset, double radius) {
        int iRadius = MathHelper.ceil(radius);
        if (iRadius <= 0) {
            return offset;
        }
        int rX = -iRadius + rand.nextInt(2 * iRadius - 1);
        int rY = -iRadius + rand.nextInt(2 * iRadius - 1);
        int rZ = -iRadius + rand.nextInt(2 * iRadius - 1);
        return offset.add(rX, rY, rZ);
    }

    @Override
    public void writeToNBT(CompoundNBT nbt) {}

    @Override
    public void readFromNBT(CompoundNBT nbt) {}
}
