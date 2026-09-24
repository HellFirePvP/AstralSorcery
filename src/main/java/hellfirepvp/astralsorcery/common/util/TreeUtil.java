/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AzaleaBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.BlockSnapshot;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TreeUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TreeUtil {

    public static Optional<Tree> wrapTree(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof SaplingBlock saplingBlock) {
            return Optional.of(new TreeGrowerTree(pos, saplingBlock.treeGrower));
        }
        if (state.getBlock() instanceof AzaleaBlock) {
            return Optional.of(new TreeGrowerTree(pos, TreeGrower.AZALEA));
        }

        return Optional.empty();
    }

    public interface Tree {

        BlockPos getPos();

        void growTree(ServerLevel level, RandomSource rand);

        List<BlockSnapshot> simulateGrowTree(ServerLevel level, RandomSource rand);

    }

    public static abstract class BasicTree implements Tree {

        private final BlockPos pos;

        public BasicTree(BlockPos pos) {
            this.pos = pos;
        }

        public BlockPos getPos() {
            return this.pos;
        }
    }

    public static class TreeGrowerTree extends BasicTree {

        private final TreeGrower grower;

        public TreeGrowerTree(BlockPos pos, TreeGrower grower) {
            super(pos);
            this.grower = grower;
        }

        @Override
        public void growTree(ServerLevel level, RandomSource rand) {
            this.grower.growTree(level, level.getChunkSource().getGenerator(), this.getPos(),
                    level.getBlockState(this.getPos()), rand);
        }

        @Override
        public List<BlockSnapshot> simulateGrowTree(ServerLevel level, RandomSource rand) {
            return BlockUtil.captureLevelChanges(level, () -> {
                this.grower.growTree(level, level.getChunkSource().getGenerator(), this.getPos(),
                        level.getBlockState(this.getPos()), rand);
                return BlockUtil.ChangeResult.revert();
            }).capturedChanges();
        }
    }
}
