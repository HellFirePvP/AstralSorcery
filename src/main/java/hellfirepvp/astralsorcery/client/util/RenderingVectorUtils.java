/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import hellfirepvp.astralsorcery.client.util.draw.RenderInfo;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderingVectorUtils
 * Created by HellFirePvP
 * Date: 27.05.2019 / 22:27
 */
public class RenderingVectorUtils {

    public static void removeStandardTranslationFromTESRMatrix(float partialTicks) {
        Vector3 v = getStandardTranslationRemovalVector(partialTicks);
        RenderSystem.translated(-v.getX(), -v.getY(), -v.getZ());
    }

    public static Vector3 getStandardTranslationRemovalVector(float partialTicks) {
        Vec3d view = RenderInfo.getInstance().getARI().getProjectedView();
        return new Vector3(view);
    }

    public static Vector3 interpolatePosition(Entity e, float partialTicks) {
        return new Vector3(
                interpolate(e.prevPosX, e.getPosX(), partialTicks),
                interpolate(e.prevPosY, e.getPosY(), partialTicks),
                interpolate(e.prevPosZ, e.getPosZ(), partialTicks)
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
        while (rot >= 180.0F) {
            rot -= 360.0F;
        }
        return prevRotation + partialTick * rot;
    }

}
