/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriPredicate;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockFinder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlockFinder {

    public static List<BlockPos> findNearbyBlocks(Level level, BlockPos center, int range, Predicate<BlockState> stateFilter) {
        return findNearbyBlocks(level, center, range, (lvl, pos, state) -> stateFilter.test(state));
    }

    public static List<BlockPos> findNearbyBlocks(Level level, BlockPos center, int range, BlockStatePredicate stateFilter) {
        Vec3i radiusVec = new Vec3i(range, range, range);
        List<BlockPos> result = new ArrayList<>();
        BlockPos.betweenClosed(center.subtract(radiusVec), center.offset(radiusVec)).forEach(offset -> {
            BlockState state = level.getBlockState(offset);
            if (stateFilter.test(level, offset, state)) {
                result.add(offset.immutable());
            }
        });
        return result;
    }

    public static List<BlockPos> findConnectedBlocksWithSameState(Level level, BlockPos origin, boolean onlyExposed, int cubeSize, int limit, boolean searchCorners) {
        return ChunkUtil.executeWithChunk(level, origin, () -> {
            BlockState state = level.getBlockState(origin);
            return findConnectedBlocksWithSameState(level, origin, onlyExposed, cubeSize, limit, searchCorners, (lvl, pos, st) -> st.equals(state));
        }, new ArrayList<>());
    }

    public static List<BlockPos> findConnectedBlocksWithSameState(Level level, BlockPos origin, boolean onlyExposed, int cubeSize, int limit, boolean searchCorners, BlockStatePredicate stateFilter) {
        List<BlockPos> result = new ArrayList<>();
        result.add(origin);

        Set<BlockPos> visited = new HashSet<>();
        visited.add(origin);

        Deque<BlockPos> searchNext = new ArrayDeque<>();
        searchNext.add(origin);

        while (!searchNext.isEmpty()) {
            Deque<BlockPos> currentSearch = searchNext;
            searchNext = new ArrayDeque<>();

            for (BlockPos offsetPos : currentSearch) {
                if (searchCorners) {
                    searchCornerNeighbors(level, origin, offsetPos, stateFilter, onlyExposed, cubeSize, limit,
                            visited, result, searchNext);
                } else {
                    searchFaceNeighbors(level, origin, offsetPos, stateFilter, onlyExposed, cubeSize, limit,
                            visited, result, searchNext);
                }
            }
        }

        return result;
    }

    private static void searchFaceNeighbors(Level level, BlockPos origin, BlockPos current,
                                             BlockStatePredicate match, boolean onlyExposed, int cubeSize, int limit,
                                             Set<BlockPos> visited, List<BlockPos> result, Deque<BlockPos> searchNext) {
        for (Direction face : Direction.values()) {
            BlockPos neighbor = current.relative(face);
            tryAddPosition(level, origin, neighbor, match, onlyExposed, cubeSize, limit,
                    visited, result, searchNext);
        }
    }

    private static void searchCornerNeighbors(Level level, BlockPos origin, BlockPos current,
                                               BlockStatePredicate match, boolean onlyExposed, int cubeSize, int limit,
                                               Set<BlockPos> visited, List<BlockPos> result, Deque<BlockPos> searchNext) {
        for (int xx = -1; xx <= 1; xx++) {
            for (int yy = -1; yy <= 1; yy++) {
                for (int zz = -1; zz <= 1; zz++) {
                    BlockPos neighbor = current.offset(xx, yy, zz);
                    tryAddPosition(level, origin, neighbor, match, onlyExposed, cubeSize, limit,
                            visited, result, searchNext);
                }
            }
        }
    }

    private static void tryAddPosition(Level level, BlockPos origin, BlockPos pos,
                                        BlockStatePredicate match, boolean onlyExposed, int cubeSize, int limit,
                                        Set<BlockPos> visited, List<BlockPos> result, Deque<BlockPos> searchNext) {
        if (!visited.add(pos)) return;
        if (cubeSize > 0 && getCubeDistance(pos, origin) > cubeSize) return;
        if (limit != -1 && result.size() >= limit) return;
        if (onlyExposed && !isExposedToAir(level, pos)) return;

        ChunkUtil.executeWithChunk(level, pos, () -> {
            BlockState state = level.getBlockState(pos);
            if (match.test(level, pos, state)) {
                result.add(pos);
                searchNext.add(pos);
            }
        });
    }

    private static int getCubeDistance(BlockPos p1, BlockPos p2) {
        return (int) Mth.absMax(Mth.absMax(p1.getX() - p2.getX(), p1.getY() - p2.getY()), p1.getZ() - p2.getZ());
    }

    private static boolean isExposedToAir(Level world, BlockPos pos) {
        for (Direction face : Direction.values()) {
            BlockPos offset = pos.relative(face);
            if (ChunkUtil.executeWithChunk(world, offset, () -> BlockUtil.isReplaceable(world, offset), false)) {
                return true;
            }
        }
        return false;
    }

    public interface BlockStatePredicate extends TriPredicate<Level, BlockPos, BlockState> {}
}
