/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CameraManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CameraManager {

    private static final CameraManager INSTANCE = new CameraManager();

    private final Deque<RevertableCameraTransformer> transformers = new ArrayDeque<>();
    private RevertableCameraTransformer activeTransformer = null;

    private CameraManager() {}

    public static CameraManager getInstance() {
        return INSTANCE;
    }

    public void onClientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().player == null) {
            this.transformers.clear();
            this.activeTransformer = null;
            return;
        }

        if (!Minecraft.getInstance().isPaused()) {
            if (this.activeTransformer != null) {
                if (this.activeTransformer.getPersistencyFunction().isExpired()) {
                    this.activeTransformer.stopTransforming();
                    this.activeTransformer = null;
                }
            }

            if (this.activeTransformer == null && !this.transformers.isEmpty()) {
                this.activeTransformer = this.transformers.pop();
                this.activeTransformer.startTransforming();
            }

            if (this.activeTransformer != null) {
                this.activeTransformer.onTick();
            }
        }
    }

    public void onRenderTick(RenderFrameEvent.Pre event) {
        if (Minecraft.getInstance().player == null) return;
        this.getActiveTransformer().ifPresent(transformer -> {
            transformer.transformCameraView(event.getPartialTick().getGameTimeDeltaPartialTick(false));
        });
    }

    public void onMouseInput(InputEvent.MouseButton.Pre event) {
        if (Minecraft.getInstance().isPaused()) return;
        this.getActiveTransformer().ifPresent(transformer -> {
            event.setCanceled(true);
        });
    }

    public Optional<RevertableCameraTransformer> getActiveTransformer() {
        return Optional.ofNullable(this.activeTransformer);
    }

    public void addTransformer(RevertableCameraTransformer transformer) {
        this.transformers.push(transformer);
    }

    public void removeTransformer(RevertableCameraTransformer transformer) {
        if (transformer.equals(this.activeTransformer)) {
            this.activeTransformer.stopTransforming();
            this.activeTransformer = null;
        } else {
            this.transformers.remove(transformer);
        }
    }

    public void clearTransformers() {
        this.getActiveTransformer().ifPresent(RevertableCameraTransformer::stopTransforming);
        this.activeTransformer = null;
        this.transformers.clear();
    }
}
