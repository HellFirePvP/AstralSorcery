/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.config.json.data.KnownTreeRegistry;
import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TreeDiscoverer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TreeDiscoverer {

    public static Optional<DiscoveredTree> findTree(BlockGetter level, BlockPos start, int xzLimit, boolean checkCorners) {
        int xzLimitSq = xzLimit == -1 ? -1 : xzLimit * xzLimit;
        List<DiscoveredTree> trees = findTreeAt(level, start, xzLimitSq, checkCorners);
        if (trees.isEmpty()) return Optional.empty();
        return trees.stream().max(Comparator.comparing(tree -> tree.getContents().getContents().size()));
    }

    private static List<DiscoveredTree> findTreeAt(BlockGetter level, BlockPos start, int xzLimitSq, boolean checkCorners) {
        Deque<MatchingTreePos> positions = new ArrayDeque<>();

        BlockState startState = level.getBlockState(start);
        if (startState.isAir()) return List.of();
        List<DiscoveredTree> trees = KnownTreeRegistry.getInstance().getMatchingTreesByLog(startState)
                .stream().map(DiscoveredTree::new).toList();
        if (trees.isEmpty()) return trees;

        MatchingTreePos startPos = new MatchingTreePos(start);
        startPos.trees.addAll(trees);
        positions.push(startPos);

        while (!positions.isEmpty()) {
            MatchingTreePos treePos = positions.pop();
            BlockPos pos = treePos.pos;
            BlockState state = level.getBlockState(pos);
            if (state.isAir()) continue;

            List<DiscoveredTree> matching = treePos.trees.stream()
                    .filter(tree -> {
                        if (tree.potentialTree.isLog(state)) {
                            tree.contents.addBlock(state, pos);
                            return true;
                        } else if (tree.potentialTree.isLeaf(state)) {
                            tree.contents.addBlock(state, pos);
                            tree.hasLeaf = true;
                            return true;
                        }
                        return false;
                    }).toList();
            if (matching.isEmpty()) continue;

            if (checkCorners) {
                for (int xx = -1; xx <= 1; xx++) {
                    for (int yy = -1; yy <= 1; yy++) {
                        for (int zz = -1; zz <= 1; zz++) {
                            if (xx == 0 && yy == 0 && zz == 0) continue;

                            BlockPos nextPos = pos.offset(xx, yy, zz);
                            if (xzLimitSq != -1 && flatDistance(start, nextPos) > xzLimitSq) continue;

                            MatchingTreePos nextTreePos = new MatchingTreePos(nextPos);
                            matching.stream()
                                    .filter(tree -> !tree.contents.hasBlockAt(nextPos))
                                    .forEach(nextTreePos.trees::add);
                            if (nextTreePos.trees.isEmpty()) continue;

                            positions.push(nextTreePos);
                        }
                    }
                }
            } else {
                for (Direction offset : Direction.values()) {
                    BlockPos nextPos = pos.relative(offset);
                    if (xzLimitSq != -1 && flatDistance(start, nextPos) > xzLimitSq) continue;

                    MatchingTreePos nextTreePos = new MatchingTreePos(nextPos);
                    matching.stream()
                            .filter(tree -> !tree.contents.hasBlockAt(nextPos))
                            .forEach(nextTreePos.trees::add);
                    if (nextTreePos.trees.isEmpty()) continue;

                    positions.push(nextTreePos);
                }
            }
        }

        return trees.stream().filter(tree -> tree.hasLeaf).toList();
    }

    private static double flatDistance(BlockPos from, BlockPos to) {
        int dx = from.getX() - to.getX();
        int dz = from.getZ() - to.getZ();
        return dx * dx + dz * dz;
    }

    private static class MatchingTreePos {

        private final BlockPos pos;
        private final List<DiscoveredTree> trees = new ArrayList<>();

        private MatchingTreePos(BlockPos pos) {
            this.pos = pos;
        }
    }

    public static class DiscoveredTree {

        private final KnownTreeRegistry.TreeEntry potentialTree;
        private final BlockArray contents = new BlockArray();
        private boolean hasLeaf = false;

        private DiscoveredTree(KnownTreeRegistry.TreeEntry potentialTree) {
            this.potentialTree = potentialTree;
        }

        public KnownTreeRegistry.TreeEntry getPotentialTree() {
            return this.potentialTree;
        }

        public BlockArray getContents() {
            return this.contents;
        }
    }
}
