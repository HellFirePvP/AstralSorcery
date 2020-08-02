package hellfirepvp.astralsorcery.common.util.block;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleBlockPredicate
 * Created by HellFirePvP
 * Date: 02.08.2020 / 09:32
 */
public class SimpleBlockPredicate implements BlockPredicate, Predicate<BlockState> {

    private final Either<List<BlockState>, Block> validMatch;

    public SimpleBlockPredicate(Block block) {
        this.validMatch = Either.right(block);
    }

    public SimpleBlockPredicate(BlockState... states) {
        this.validMatch = Either.left(Arrays.asList(states));
    }

    public List<String> getAsConfigList() {
        List<String> out = new ArrayList<>();
        this.validMatch
                .ifLeft(states -> states.stream().map(BlockStateHelper::serialize).forEach(out::add))
                .ifRight(block -> out.add(BlockStateHelper.serialize(block)));
        return out;
    }

    @Nullable
    public static SimpleBlockPredicate fromConfig(String serialized) {
        if (BlockStateHelper.isMissingStateInformation(serialized)) {
            Block b = BlockStateHelper.deserializeBlock(serialized);
            if (b != Blocks.AIR) {
                return new SimpleBlockPredicate(b);
            }
        } else {
            BlockState state = BlockStateHelper.deserialize(serialized);
            if (state.getBlock() != Blocks.AIR) {
                return new SimpleBlockPredicate(state);
            }
        }
        return null;
    }

    @Override
    public boolean test(BlockState state) {
        Either<Boolean, Boolean> matchResult = this.validMatch.mapBoth(
                states -> states.contains(state),
                block -> state.getBlock().equals(block));
        return matchResult.left().orElse(matchResult.right().orElse(false));
    }

    @Override
    public boolean test(World world, BlockPos pos, BlockState state) {
        return this.test(state);
    }
}
