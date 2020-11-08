/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.player.PlayerEntity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CameraTransformerSettingsCache
 * Created by HellFirePvP
 * Date: 02.12.2019 / 20:08
 */
public abstract class CameraTransformerSettingsCache implements ICameraTransformer {

    private boolean active = false;

    private boolean viewBobbing = false, hideGui = false, flying = false;
    private PointOfView thirdPersonView;

    private Vector3 startPosition;
    private float startYaw, startPitch;

    @Override
    public void onStartTransforming(float pTicks) {
        Minecraft mc = Minecraft.getInstance();

        this.viewBobbing = mc.gameSettings.viewBobbing;
        this.hideGui = mc.gameSettings.hideGUI;
        this.thirdPersonView = mc.gameSettings.getPointOfView();
        PlayerEntity player = mc.player;
        this.flying = player.abilities.isFlying;
        this.startPosition = new Vector3(player.getPosX(), player.getPosY(), player.getPosZ());
        this.startYaw = player.rotationYaw;
        this.startPitch = player.rotationPitch;
        player.setVelocity(0, 0, 0);
        this.active = true;
    }

    @Override
    public void onStopTransforming(float pTicks) {
        if (active) {
            GameSettings settings = Minecraft.getInstance().gameSettings;
            settings.viewBobbing = viewBobbing;
            settings.hideGUI = hideGui;
            settings.setPointOfView(thirdPersonView);
            PlayerEntity player = Minecraft.getInstance().player;
            player.abilities.isFlying = flying;
            player.setPositionAndRotation(startPosition.getX(), startPosition.getY(), startPosition.getZ(), startYaw, startPitch);
            player.setVelocity(0, 0, 0);
            this.active = false;
        }
    }

    @Override
    public void transformRenderView(float pTicks) {
        if (!active) {
            return;
        }

        GameSettings settings = Minecraft.getInstance().gameSettings;
        settings.hideGUI = true;
        settings.viewBobbing = false;
        settings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
        Minecraft.getInstance().player.abilities.isFlying = true;
        Minecraft.getInstance().player.setVelocity(0, 0, 0);
    }

}
