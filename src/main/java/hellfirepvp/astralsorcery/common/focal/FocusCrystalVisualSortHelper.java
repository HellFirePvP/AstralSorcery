/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.focal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocusCrystalVisualSortHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocusCrystalVisualSortHelper {

    private FocusCrystalVisualSortHelper() {}

    public static List<BlockPos> sortIntoContinuousPolygon(Level level, BlockPos center, List<BlockPos> positions) {
        positions.sort((a, b) -> {
            double angleA = Math.atan2(a.getZ() - center.getZ(), a.getX() - center.getX());
            double angleB = Math.atan2(b.getZ() - center.getZ(), b.getX() - center.getX());
            return Double.compare(angleA, angleB);
        });
        if (!positions.isEmpty()) {
            positions.addLast(positions.getFirst());
        }
        return positions;
    }

    public static List<BlockPos> sortByClosestFirst(Level level, BlockPos center, List<BlockPos> positions) {
        positions.sort(Comparator.comparingDouble(center::distSqr));
        return positions;
    }
}
