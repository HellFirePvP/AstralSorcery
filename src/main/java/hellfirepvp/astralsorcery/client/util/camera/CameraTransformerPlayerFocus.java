/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraTransformerPlayerFocus
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CameraTransformerPlayerFocus extends RevertableCameraTransformer {

    private final CameraViewEntity cameraEntity;
    private final CameraPersistencyFunction persistencyFunction;

    public CameraTransformerPlayerFocus(CameraViewEntity cameraEntity, CameraPersistencyFunction persistencyFunction) {
        this.cameraEntity = cameraEntity;
        this.persistencyFunction = persistencyFunction;
    }

    @Override
    public void startTransforming() {
        super.startTransforming();

        this.cameraEntity.setAsRenderViewEntity();
    }

    @Override
    public void stopTransforming() {
        super.stopTransforming();

        CameraUtil.resetCamera();

        if (Minecraft.getInstance().level != null) {
            this.cameraEntity.onStopTransforming();
        }
    }

    @Override
    public void transformCameraView(float pTicks) {
        super.transformCameraView(pTicks);

        Vector3 focus = this.cameraEntity.getCameraFocus();
        if (focus != null) {
            this.cameraEntity.transformToFocusOnPoint(focus, pTicks, true);
        }
    }

    @Override
    public void onTick() {
        this.cameraEntity.tickCount++;

        this.cameraEntity.moveEntityTick(Minecraft.getInstance().player, this.cameraEntity.tickCount);
    }

    @Override
    public CameraPersistencyFunction getPersistencyFunction() {
        return this.persistencyFunction;
    }
}
