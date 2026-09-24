/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal;

import hellfirepvp.astralsorcery.common.focal.observer.FocusCrystalFilamentObserver;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalPlacementHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalPlacementHelper {

    private FocusCrystalPlacementHelper() {}

    public static List<BlockPos> collectFocusCrystalFilaments(LevelAccessor level, BlockPos center) {
        int radius = FocusCrystalFilamentObserver.OBSERVED_AREA_RADIUS;
        return collectFocusCrystalFilaments(level, center,
                AABB.ofSize(Vec3.atLowerCornerOf(center), radius * 2, radius * 2, radius * 2));
    }

    public static List<BlockPos> collectFocusCrystalFilaments(LevelAccessor level, BlockPos center, AABB scanBox) {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.betweenClosedStream(scanBox).forEach(pos -> {
            if (level.getBlockState(pos).is(BlocksAS.STELLAR_FILAMENT)) {
                positions.add(pos.immutable());
            }
        });
        return positions;
    }

    public static boolean hasOppositeButNoAxisSymmetry(Level level, BlockPos center, Set<BlockPos> positions) {
        if (!fulfillsGeneralLayerRules(level, center, positions)) return false;
        Set<BlockPos> normalize = positions.stream()
                .map(pos -> new BlockPos(pos.getX() - center.getX(), 0, pos.getZ() - center.getZ()))
                .collect(Collectors.toSet());

        for (BlockPos off : normalize) {
            int dx = off.getX();
            int dz = off.getZ();

            BlockPos r90  = new BlockPos(-dz, 0, dx);
            BlockPos r180 = new BlockPos(-dx, 0, -dz);
            BlockPos r270 = new BlockPos(dz, 0, -dx);

            if (normalize.contains(r90) || !normalize.contains(r180) || normalize.contains(r270)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasOneAxisSymmetryButNotOther(Level level, BlockPos center, Set<BlockPos> positions) {
        if (!fulfillsGeneralLayerRules(level, center, positions)) return false;
        Set<BlockPos> normalize = positions.stream()
                .map(pos -> new BlockPos(pos.getX() - center.getX(), 0, pos.getZ() - center.getZ()))
                .collect(Collectors.toSet());

        boolean hasX = normalize.stream()
                .allMatch(off -> normalize.contains(new BlockPos(off.getX(), 0, -off.getZ())));
        boolean hasZ = normalize.stream()
                .allMatch(off -> normalize.contains(new BlockPos(-off.getX(), 0, off.getZ())));

        return hasX ^ hasZ;
    }

    public static boolean allHaveUniqueDistances(Level level, BlockPos center, Set<BlockPos> positions) {
        if (!fulfillsGeneralLayerRules(level, center, positions)) return false;
        Set<Double> dist = positions.stream()
                .map(pos -> {
                    double dx = pos.getX() - center.getX();
                    double dz = pos.getZ() - center.getZ();
                    return Math.sqrt(dx * dx + dz * dz);
                })
                .collect(Collectors.toSet());
        return dist.size() == positions.size();
    }

    public static boolean noneHaveRightAngles(Level level, BlockPos center, Set<BlockPos> positions) {
        if (!fulfillsGeneralLayerRules(level, center, positions)) return false;
        Set<Vector3> normalize = positions.stream()
                .map(pos -> new Vector3(pos.getX() - center.getX(), 0, pos.getZ() - center.getZ()))
                .collect(Collectors.toSet());

        for (Vector3 pivot : normalize) {
            for (Vector3 other1 : normalize) {
                if (other1.equals(pivot)) continue;
                for (Vector3 other2 : normalize) {
                    if (other2.equals(pivot) || other2.equals(other1)) continue;

                    Vector3 v1 = other1.copy().subtract(pivot);
                    Vector3 v2 = other2.copy().subtract(pivot);

                    if (Math.abs(v1.dot(v2)) < 1.0E-4D) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean noDotNoAxisSymmetry(Level level, BlockPos center, Set<BlockPos> positions) {
        if (!fulfillsGeneralLayerRules(level, center, positions)) return false;
        Set<BlockPos> normalize = positions.stream()
                .map(pos -> new BlockPos(pos.getX() - center.getX(), 0, pos.getZ() - center.getZ()))
                .collect(Collectors.toSet());

        for (BlockPos off : normalize) {
            int dx = off.getX();
            int dz = off.getZ();

            BlockPos r90  = new BlockPos(-dz, 0, dx);
            BlockPos r180 = new BlockPos(-dx, 0, -dz);
            BlockPos r270 = new BlockPos(dz, 0, -dx);

            if (normalize.contains(r90) || normalize.contains(r180) || normalize.contains(r270)) {
                return false;
            }
        }
        return true;
    }

    private static boolean fulfillsGeneralLayerRules(Level level, BlockPos center, Set<BlockPos> positions) {
        return hasNoPositionOnAxis(level, center, positions) &&
                hasPositionsInEveryQuadrant(level, center, positions) &&
                hasPositionsOnlyInOneLayer(level, center, positions);
    }

    private static boolean hasNoPositionOnAxis(Level level, BlockPos center, Set<BlockPos> positions) {
        for (BlockPos pos : positions) {
            if (pos.getX() == center.getX() || pos.getZ() == center.getZ()) {
                return false;
            }
        }
        return true;
    }

    private static boolean hasPositionsInEveryQuadrant(Level level, BlockPos center, Set<BlockPos> positions) {
        boolean posXPosZ = false;
        boolean posXNegZ = false;
        boolean negXPosZ = false;
        boolean negXNegZ = false;

        for (BlockPos pos : positions) {
            int dx = pos.getX() - center.getX();
            int dz = pos.getZ() - center.getZ();

            if (dx > 0 && dz > 0) {
                posXPosZ = true;
            } else if (dx > 0 && dz < 0) {
                posXNegZ = true;
            } else if (dx < 0 && dz > 0) {
                negXPosZ = true;
            } else {
                negXNegZ = true;
            }
        }

        return posXPosZ && posXNegZ && negXPosZ && negXNegZ && positions.size() == 4;
    }

    private static boolean hasPositionsOnlyInOneLayer(Level level, BlockPos center, Set<BlockPos> positions) {
        for (BlockPos pos : positions) {
            if (pos.equals(center)) return false;
            for (BlockPos otherPos : positions) {
                if (otherPos.getY() != pos.getY()) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean allPositionsFulfillGlobalRules(Level level, BlockPos center, Set<BlockPos> allPositions) {
        return mustNotStackIndividualPositions(level, center, allPositions);
    }

    private static boolean mustNotStackIndividualPositions(Level level, BlockPos center, Set<BlockPos> allPositions) {
        for (BlockPos pos : allPositions) {
            for (BlockPos other : allPositions) {
                if (other.equals(pos)) continue;
                if (pos.getX() == other.getX() && pos.getZ() == other.getZ()) {
                    return false;
                }
            }
        }
        return true;
    }
}
