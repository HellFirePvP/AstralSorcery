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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockPositionGenerator
 * Created by HellFirePvP
 * Date: 24.11.2019 / 09:09
 */
public abstract class BlockPositionGenerator {

    private BiPredicate<BlockPos, Double> posFilter = (pos, radius) -> true;

    public final BlockPositionGenerator andFilter(Predicate<BlockPos> filter) {
        return this.andFilter((pos, radius) -> filter.test(pos));
    }

    public final BlockPositionGenerator andFilter(BiPredicate<BlockPos, Double> filter) {
        this.posFilter = this.posFilter.and(filter);
        return this;
    }

    public final BlockPositionGenerator copyFilterFrom(BlockPositionGenerator other) {
        other.andFilter(this.posFilter);
        return this;
    }

    public final BlockPos generateNextPosition(BlockPos offset, double selectionRadius) {
        BlockPos next;
        do {
            next = genNext(offset, selectionRadius);
        } while (!posFilter.test(next, selectionRadius));
        return next;
    }

    protected abstract BlockPos genNext(BlockPos offset, double radius);

    public abstract void writeToNBT(CompoundNBT nbt);

    public abstract void readFromNBT(CompoundNBT nbt);

}
