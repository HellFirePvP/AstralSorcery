/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.world.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RenderVectorUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RenderVectorUtil {

    public static Vector3 interpolatePosition(Entity e, float partialTicks) {
        return new Vector3(
                interpolate(e.xo, e.getX(), partialTicks),
                interpolate(e.yo, e.getY(), partialTicks),
                interpolate(e.zo, e.getZ(), partialTicks)
        );
    }

    public static Vector3 interpolate(Vector3 oldV, Vector3 newV, float partialTicks) {
        return new Vector3(
                interpolate(oldV.getX(), newV.getX(), partialTicks),
                interpolate(oldV.getY(), newV.getY(), partialTicks),
                interpolate(oldV.getZ(), newV.getZ(), partialTicks)
        );
    }

    public static double interpolate(double oldP, double newP, float partialTicks) {
        if (oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }

    public static float interpolate(float oldP, float newP, float partialTicks) {
        if (oldP == newP) return oldP;
        return oldP + ((newP - oldP) * partialTicks);
    }

    public static float interpolateRotation(float prevRotation, float nextRotation, float partialTick) {
        float rot = nextRotation - prevRotation;
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        return prevRotation + partialTick * rot;
    }
}
