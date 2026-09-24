/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RevertableCameraTransformer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class RevertableCameraTransformer {

    private boolean active = false;

    private boolean viewBobbing = false, hideGui = false, flying = false;
    private CameraType thirdPersonView;

    private Vector3 startPosition;
    private float startYaw, startPitch;

    public void startTransforming() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        this.viewBobbing = mc.options.bobView().get();
        this.hideGui = mc.options.hideGui;
        this.thirdPersonView = mc.options.getCameraType();
        this.flying = player.getAbilities().flying;
        this.startPosition = new Vector3(player);
        this.startYaw = player.getYRot();
        this.startPitch = player.getXRot();
        player.lerpMotion(0, 0, 0);
        player.setYRot(0);
        player.setXRot(0);
        player.setYHeadRot(0);
        player.setYBodyRot(0);
        this.active = true;
    }

    public void stopTransforming() {
        if (!this.active) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        mc.options.bobView().set(this.viewBobbing);
        mc.options.hideGui = this.hideGui;
        mc.options.setCameraType(this.thirdPersonView);
        player.getAbilities().flying = this.flying;
        player.absMoveTo(this.startPosition.getX(), this.startPosition.getY(), this.startPosition.getZ(), this.startYaw, this.startPitch);
        player.lerpMotion(0, 0, 0);
        this.active = false;
    }

    public void transformCameraView(float pTicks) {
        if (!this.active) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        mc.options.bobView().set(false);
        mc.options.hideGui = true;
        mc.options.setCameraType(CameraType.THIRD_PERSON_BACK);
        // 'mc.gameMode != null' to be sure to not set a screen during client teardown
        if (mc.screen != null && mc.gameMode != null) mc.setScreen(null);
        player.getAbilities().flying = true;
        player.lerpMotion(0, 0, 0);
    }

    public abstract void onTick();

    public abstract CameraPersistencyFunction getPersistencyFunction();
}
