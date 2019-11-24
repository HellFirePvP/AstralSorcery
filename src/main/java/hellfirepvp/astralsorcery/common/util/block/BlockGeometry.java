/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGeometry
 * Created by HellFirePvP
 * Date: 24.11.2019 / 09:54
 */
public class BlockGeometry {

    public static Set<BlockPos> getSphere(double radius) {
        Set<BlockPos> out = new HashSet<>();
        Vector3 vFrom = new Vector3(0.5, 0.5, 0.5);
        double dst = radius * radius;

        int toX = MathHelper.ceil(radius);
        int toY = MathHelper.ceil(radius);
        int toZ = MathHelper.ceil(radius);
        for (int x = MathHelper.floor(-radius); x <= toX; x++) {
            for (int y = MathHelper.floor(-radius); y <= toY; y++) {
                for (int z = MathHelper.floor(-radius); z <= toZ; z++) {
                    Vector3 result = new Vector3(x, y, z).add(0.5, 0.5, 0.5);
                    if (result.distanceSquared(vFrom) <= dst) {
                        out.add(result.toBlockPos());
                    }
                }
            }
        }
        return out;
    }

    public static Set<BlockPos> getHollowSphere(double outerRadius, double innerRadius) {
        Set<BlockPos> out = new HashSet<>();
        Vector3 vFrom = new Vector3(0.5, 0.5, 0.5);
        double dstOuter = outerRadius * outerRadius;
        double dstInner = innerRadius * innerRadius;

        int toX = MathHelper.ceil(outerRadius);
        int toY = MathHelper.ceil(outerRadius);
        int toZ = MathHelper.ceil(outerRadius);
        for (int x = MathHelper.floor(-outerRadius); x <= toX; x++) {
            for (int y = MathHelper.floor(-outerRadius); y <= toY; y++) {
                for (int z = MathHelper.floor(-outerRadius); z <= toZ; z++) {
                    Vector3 result = new Vector3(x, y, z).add(0.5, 0.5, 0.5);
                    double dst = result.distanceSquared(vFrom);
                    if (dst > dstInner && dst <= dstOuter) {
                        out.add(result.toBlockPos());
                    }
                }
            }
        }
        return out;
    }

}
