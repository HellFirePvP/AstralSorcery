/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CameraUtil {

    public static void positionCamera(LivingEntity cameraEntity, float pTicks, double x, double y, double z, double prevX, double prevY, double prevZ, double yaw, double yawPrev, double pitch, double pitchPrev) {
        double dYaw = Mth.positiveModulo(yaw - yawPrev, 360d);
        // Use the smaller arc
        if (dYaw > 180) {
            dYaw -= 360;
        }
        yawPrev = yaw - dYaw;
        float iYaw = Mth.lerp(pTicks, (float) yawPrev, (float) yaw);
        float iPitch = Mth.lerp(pTicks, (float) pitchPrev, (float) pitch);

        Minecraft mc = Minecraft.getInstance();
        Entity renderView = mc.getCameraEntity();
        if (renderView == null || !renderView.equals(cameraEntity)) {
            mc.setCameraEntity(cameraEntity);
            renderView = cameraEntity;
        }

        LivingEntity camera = (LivingEntity) renderView;

        camera.setPosRaw(x, y, z);
        camera.xo = prevX;
        camera.yo = prevY;
        camera.zo = prevZ;
        camera.xOld = prevX;
        camera.yOld = prevY;
        camera.zOld = prevZ;

        camera.setYRot(iYaw);
        camera.yRotO = iYaw;
        camera.setXRot(iPitch);
        camera.xRotO = iPitch;
        camera.yHeadRot  = iYaw;
        camera.yHeadRotO = iYaw;
        camera.yBodyRot  = iYaw;
        camera.yBodyRotO = iYaw;
    }

    public static void resetCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Player player = mc.player;
            mc.setCameraEntity(player);

            if (mc.screen != null) mc.setScreen(null);
        }
    }
}
