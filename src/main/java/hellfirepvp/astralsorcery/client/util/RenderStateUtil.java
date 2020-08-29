package hellfirepvp.astralsorcery.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderState;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RenderStateUtil
 * Created by HellFirePvP
 * Date: 29.08.2020 / 14:32
 */
public class RenderStateUtil {

    public static class CullState extends RenderState.CullState {

        private final boolean enabled;

        public CullState(boolean enabled) {
            super(enabled);
            this.enabled = enabled;
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            if (!enabled) {
                RenderSystem.disableCull();
            }
        }

        @Override
        public void clearRenderState() {
            super.clearRenderState();
        }
    }

    public static class WriteMaskState extends RenderState.WriteMaskState {

        private final boolean colorMask;
        private final boolean depthMask;

        public WriteMaskState(boolean colorMask, boolean depthMask) {
            super(colorMask, depthMask);
            this.colorMask = colorMask;
            this.depthMask = depthMask;
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            if (depthMask) {
                RenderSystem.depthMask(true);
            }
            if (colorMask) {
                RenderSystem.colorMask(true, true, true, true);
            }
        }

        @Override
        public void clearRenderState() {
            super.clearRenderState();
        }
    }
}
