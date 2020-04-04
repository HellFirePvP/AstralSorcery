/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.observerlib.api.util.BlockArray;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Stack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TreeDiscoverer
 * Created by HellFirePvP
 * Date: 03.04.2020 / 18:38
 */
public class TreeDiscoverer {

    @Nonnull
    public static BlockArray findTreeAt(World world, BlockPos at, boolean checkCorners) {
        return findTreeAt(world, at, checkCorners, -1);
    }

    @Nonnull
    public static BlockArray findTreeAt(World world, BlockPos at, boolean checkCorners, int xzLimit) {
        int xzLimitSq = xzLimit == -1 ? -1 : xzLimit * xzLimit;
        BlockArray out = new BlockArray();
        findTree(world, at, xzLimitSq, checkCorners, out);
        return out;
    }

    private static void findTree(World world, BlockPos at, int xzLimitSq, boolean checkCorners, BlockArray out) {
        //The gist: we start at a LOG and eventually might find a LEAF
        //If we don't find a log instantly, stop. If we find something different than what we found before, stop.
        //Match against blocks, not tags, not specific states.
        TreeMatch foundTreeType = new TreeMatch();

        Stack<BlockPos> discoverPositions = new Stack<>();
        discoverPositions.push(at);
        while (!discoverPositions.isEmpty()) {
            BlockPos offset = discoverPositions.pop();
            if (world.isAirBlock(offset)) {
                continue;
            }

            BlockState foundState = world.getBlockState(offset);
            Block foundBlock = foundState.getBlock();
            if (foundTreeType.matchLog == null) {
                //Try find log
                if (!BlockTags.LOGS.contains(foundBlock)) {
                    return; //Couldn't find a log first thing. this is probably not a tree.
                }
                foundTreeType.matchLog = BlockPredicates.isBlock(foundBlock);
            } else if (foundTreeType.matchLeaf == null) {
                //Test if this is a leaf here
                if (BlockTags.LEAVES.contains(foundBlock)) {
                    foundTreeType.matchLeaf = BlockPredicates.isBlock(foundBlock);
                }
            }

            boolean successful = false;
            if (foundTreeType.matchLog.test(world, offset, foundState)) {
                successful = true;
            } else if (foundTreeType.matchLeaf != null && foundTreeType.matchLeaf.test(world, offset, foundState)) {
                successful = true;
            }

            if (successful) {
                out.addBlock(foundState, offset.getX(), offset.getY(), offset.getZ());

                if (checkCorners) {
                    for (int xx = -1; xx <= 1; xx++) {
                        for (int yy = -1; yy <= 1; yy++) {
                            for (int zz = -1; zz <= 1; zz++) {
                                BlockPos newPos = offset.add(xx, yy, zz);
                                if((xzLimitSq == -1 || flatDistanceSq(newPos, at) <= xzLimitSq) && !out.hasBlockAt(newPos)) {
                                    discoverPositions.push(newPos);
                                }
                            }
                        }
                    }
                } else {
                    for (Direction dir : Direction.values()) {
                        BlockPos newPos = offset.offset(dir);
                        if((xzLimitSq == -1 || flatDistanceSq(newPos, at) <= xzLimitSq) && !out.hasBlockAt(newPos)) {
                            discoverPositions.push(newPos);
                        }
                    }
                }
            }
        }
    }

    private static double flatDistanceSq(Vec3i from, Vec3i to) {
        double xDiff = (double) from.getX() - to.getX();
        double zDiff = (double) from.getZ() - to.getZ();
        return xDiff * xDiff + zDiff * zDiff;
    }

    private static class TreeMatch {

        private BlockPredicate matchLog;
        private BlockPredicate matchLeaf;

    }
}
