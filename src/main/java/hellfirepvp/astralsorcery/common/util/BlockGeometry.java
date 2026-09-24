/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockGeometry
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BlockGeometry {

    private BlockGeometry() {}

    public static List<BlockPos> getPlane(Direction direction, int radius) {
        return getPlane(direction.getAxis(), radius);
    }

    public static List<BlockPos> getPlane(Direction.Axis axis, int radius) {
        List<BlockPos> out = new ArrayList<>();
        int xRadius = axis == Direction.Axis.X ? 0 : radius;
        int yRadius = axis == Direction.Axis.Y ? 0 : radius;
        int zRadius = axis == Direction.Axis.Z ? 0 : radius;
        for (int xx = -xRadius; xx <= xRadius; xx++) {
            for (int yy = -yRadius; yy <= yRadius; yy++) {
                for (int zz = -zRadius; zz <= zRadius; zz++) {
                    out.add(new BlockPos(xx, yy, zz));
                }
            }
        }
        return out;
    }

    public static List<BlockPos> getSphere(double radius) {
        List<BlockPos> out = new ArrayList<>();
        Vector3 center = new Vector3(0.5, 0.5, 0.5);
        double radiusSq = radius * radius;

        int bound = Mth.ceil(radius);
        int lower = Mth.floor(-radius);
        for (int y = lower; y <= bound; y++) {
            for (int x = lower; x <= bound; x++) {
                for (int z = lower; z <= bound; z++) {
                    Vector3 pos = new Vector3(x + 0.5, y + 0.5, z + 0.5);
                    if (pos.distanceSquared(center) <= radiusSq) {
                        out.add(pos.toBlockPos());
                    }
                }
            }
        }
        return out;
    }

    public static List<BlockPos> getHollowSphere(double outerRadius, double innerRadius) {
        List<BlockPos> out = new ArrayList<>();
        Vector3 center = new Vector3(0.5, 0.5, 0.5);
        double outerSq = outerRadius * outerRadius;
        double innerSq = innerRadius * innerRadius;

        int bound = Mth.ceil(outerRadius);
        int lower = Mth.floor(-outerRadius);
        for (int x = lower; x <= bound; x++) {
            for (int y = lower; y <= bound; y++) {
                for (int z = lower; z <= bound; z++) {
                    Vector3 pos = new Vector3(x + 0.5, y + 0.5, z + 0.5);
                    double distSq = pos.distanceSquared(center);
                    if (distSq > innerSq && distSq <= outerSq) {
                        out.add(pos.toBlockPos());
                    }
                }
            }
        }
        return out;
    }
}
