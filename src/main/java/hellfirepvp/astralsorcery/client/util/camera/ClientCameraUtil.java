package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientCameraUtil
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:24
 */
public class ClientCameraUtil {

    public static void positionCamera(PlayerEntity renderView, float pTicks, double x, double y, double z, double prevX, double prevY, double prevZ, double yaw, double yawPrev, double pitch, double pitchPrev) {
        TileEntityRendererDispatcher.staticPlayerX = x;
        TileEntityRendererDispatcher.staticPlayerY = y;
        TileEntityRendererDispatcher.staticPlayerZ = z;

        float iYaw = MathHelper.lerp(pTicks, (float) yawPrev, (float) yaw);
        float iPitch = MathHelper.lerp(pTicks, (float) pitchPrev, (float) pitch);

        Minecraft mc = Minecraft.getInstance();
        Entity rv = mc.getRenderViewEntity();
        if (rv == null || !rv.equals(renderView)) {
            mc.setRenderViewEntity(renderView);
            rv = renderView;
        }
        PlayerEntity render = (PlayerEntity) rv;

        render.posX = x;
        render.posY = y;
        render.posZ = z;
        render.prevPosX = prevX;
        render.prevPosY = prevY;
        render.prevPosZ = prevZ;
        render.lastTickPosX = prevX;
        render.lastTickPosY = prevY;
        render.lastTickPosZ = prevZ;

        render.rotationYaw =         iYaw;
        render.prevRotationYaw =     iYaw;
        render.rotationYawHead =     iYaw;
        render.prevRotationYawHead = iYaw;
        render.cameraYaw =           iYaw;
        render.prevCameraYaw =       iYaw;
        render.rotationPitch =       iPitch;
        render.prevRotationPitch =   iPitch;

        render = Minecraft.getInstance().player;

        render.posX = x;
        render.posY = y;
        render.posZ = z;
        render.prevPosX = prevX;
        render.prevPosY = prevY;
        render.prevPosZ = prevZ;
        render.lastTickPosX = prevX;
        render.lastTickPosY = prevY;
        render.lastTickPosZ = prevZ;

        render.rotationYaw =         iYaw;
        render.prevRotationYaw =     iYaw;
        render.rotationYawHead =     iYaw;
        render.prevRotationYawHead = iYaw;
        render.cameraYaw =           iYaw;
        render.prevCameraYaw =       iYaw;
        render.rotationPitch =       iPitch;
        render.prevRotationPitch =   iPitch;
    }

    public static void resetCamera() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            PlayerEntity player = mc.player;
            mc.setRenderViewEntity(player);
            double x = player.posX;
            double y = player.posY;
            double z = player.posZ;
            EntityRendererManager rm = mc.getRenderManager();
            rm.setRenderPosition(x, y, z);

            TileEntityRendererDispatcher.staticPlayerX = x;
            TileEntityRendererDispatcher.staticPlayerY = y;
            TileEntityRendererDispatcher.staticPlayerZ = z;

            if (mc.currentScreen != null) {
                mc.displayGuiScreen(null);
            }
        }
    }
}
