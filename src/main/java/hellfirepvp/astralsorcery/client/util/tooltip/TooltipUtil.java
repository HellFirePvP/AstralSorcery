/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.tooltip;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

import java.util.Stack;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TooltipUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TooltipUtil {

    private static final TooltipUtil INSTANCE = new TooltipUtil();
    private final Stack<ColorOverride> colorOverrides = new Stack<>();
    private boolean issuedWarning = false;

    private ClientTooltipPositioner lastPositioner = DefaultTooltipPositioner.INSTANCE;

    private TooltipUtil() {}

    public static TooltipUtil getInstance() {
        return INSTANCE;
    }

    public static ClientTooltipPositioner getLastPositioner() {
        return getInstance().lastPositioner;
    }

    public static void blueColor(Runnable renderTooltip) {
        changeColor(0xFF000011, 0xFF000011, 0xFF000047, 0xFF000047, renderTooltip);
    }

    public static void changeColor(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd, Runnable renderTooltip) {
        getInstance().runWithColor(backgroundStart, backgroundEnd, borderStart, borderEnd, renderTooltip);
    }

    private void runWithColor(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd, Runnable renderTooltip) {
        this.colorOverrides.push(new ColorOverride(backgroundStart, backgroundEnd, borderStart, borderEnd));
        int sizePre = this.colorOverrides.size();
        renderTooltip.run();
        int sizePost = this.colorOverrides.size();

        if (sizePre - 1 != sizePost) {
            if (!this.issuedWarning) {
                AstralSorcery.LOG.warn("Tooltip event is not firing as expected? Colors might not look as expected.");
                this.issuedWarning = true;
            }
        }
    }

    public void colorTooltip(RenderTooltipEvent.Color colorEvent) {
        if (!this.colorOverrides.isEmpty()) {
            ColorOverride override = this.colorOverrides.pop();
            colorEvent.setBackgroundStart(override.backgroundStart);
            colorEvent.setBackgroundEnd(override.backgroundEnd);
            colorEvent.setBorderStart(override.borderStart);
            colorEvent.setBorderEnd(override.borderEnd);
        }
    }

    public void tooltipContext(RenderTooltipEvent.Pre event) {
        this.lastPositioner = event.getTooltipPositioner();
    }

    private static record ColorOverride(int backgroundStart, int backgroundEnd, int borderStart, int borderEnd) {}
}
