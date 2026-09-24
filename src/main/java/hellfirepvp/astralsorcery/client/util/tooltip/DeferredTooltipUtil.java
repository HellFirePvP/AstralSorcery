/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.tooltip;

import hellfirepvp.astralsorcery.client.util.RenderUtil;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DeferredTooltipUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DeferredTooltipUtil {

    private static List<Runnable> capturedTooltips = new ArrayList<>();
    private static boolean capturesTooltips = false;

    private DeferredTooltipUtil() {}

    public static void captureTooltips() {
        if (capturesTooltips) {
            throw new IllegalStateException("Already capturing tooltips!");
        }
        capturesTooltips = true;
    }

    public static List<Runnable> stopCapturingTooltips() {
        if (!capturesTooltips) {
            return List.of();
        }
        capturesTooltips = false;
        List<Runnable> captured = capturedTooltips;
        capturedTooltips = new ArrayList<>();
        return captured;
    }

    public static void drawTooltip(GuiGraphics guiGraphics, Consumer<GuiGraphics> run) {
        if (capturesTooltips) {
            GuiGraphics copied = RenderUtil.copy(guiGraphics);
            capturedTooltips.add(() -> {
                run.accept(copied);
                copied.flush();
            });
        } else {
            run.accept(guiGraphics);
        }
    }
}
