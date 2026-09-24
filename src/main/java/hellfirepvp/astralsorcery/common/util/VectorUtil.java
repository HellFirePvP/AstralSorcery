/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: VectorUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class VectorUtil {

    public static Vector3 withRandomOffset(Vector3 target, RandomSource rand) {
        return withRandomOffset(target, rand, 1F);
    }

    public static Vector3 withRandomOffset(Vector3 target, RandomSource rand, float multiplier) {
        return target.copy().add(
                rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1),
                rand.nextFloat() * multiplier * (rand.nextBoolean() ? 1 : -1)
        );
    }

    public static List<Vector3> getCircleOffsets(Vector3 center, Vector3.RotAxis axis, float radius, int count) {
        return getCircleOffsets(center, axis.getVector(), radius, count);
    }

    public static List<Vector3> getCircleOffsets(Vector3 center, Vector3 axis, float radius, int count) {
        List<Vector3> offsets = new ArrayList<>();
        Vector3 circleVec = axis.copy().perpendicular().normalize().multiply(radius);
        float degStep = 360F / count;
        for (int step = 0; step < count; step++) {
            double degree = step * degStep;
            offsets.add(circleVec.copy().rotate(Math.toRadians(degree), axis).add(center));
        }
        return offsets;
    }

    public static Vector3 getVortexMotion(Vector3 pos, Vector3 target, double vortexRange, double multiplier) {
        return getVortexMotion(() -> pos, target, vortexRange, multiplier);
    }

    public static Vector3 getVortexMotion(Supplier<Vector3> positionSupplier, Vector3 target, double vortexRange, double multiplier) {
        Vector3 pos = positionSupplier.get();
        double diffX = (target.getX() - pos.getX()) / vortexRange;
        double diffY = (target.getY() - pos.getY()) / vortexRange;
        double diffZ = (target.getZ() - pos.getZ()) / vortexRange;
        double dist = Math.sqrt(diffX * diffX + diffY * diffY + diffZ * diffZ);
        if (1.0D - dist > 0.0D) {
            double dstFactorSq = (1.0D - dist) * (1.0D - dist);
            Vector3 toAdd = new Vector3();
            toAdd.setX(diffX / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setY(diffY / dist * dstFactorSq * 0.15D * multiplier);
            toAdd.setZ(diffZ / dist * dstFactorSq * 0.15D * multiplier);
            return toAdd;
        }
        return new Vector3();
    }

    public static List<Vector3> iteratePoints(Vector3 from, Vector3 to, float stepLength) {
        List<Vector3> path = new ArrayList<>();

        int stepAmount = (int) Math.ceil(from.distance(to) / stepLength);
        Vector3 stepVec = to.copy().subtract(from).multiply(1.0 / stepAmount);
        for (int i = 0; i <= stepAmount; i++) {
            Vector3 point = from.copy().add(stepVec.copy().multiply(i));
            path.add(point);
        }

        return path;
    }

    public static Vector3 limitVelocityToMinecraftLimit(Vector3 velocity) {
        double maxDir = Math.max(Math.abs(velocity.getX()), Math.max(Math.abs(velocity.getY()), Math.abs(velocity.getZ())));
        if (maxDir <= 3.9) { //ClientboundSetEntityMotionPacket 3.9 * 8000 short value limit
            return velocity;
        }
        return velocity.multiply(3.9 / maxDir);
    }
}
