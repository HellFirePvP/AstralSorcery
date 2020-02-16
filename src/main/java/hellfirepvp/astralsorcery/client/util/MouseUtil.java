/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MouseUtil
 * Created by HellFirePvP
 * Date: 14.02.2020 / 21:52
 */
public class MouseUtil {

    private static boolean preventGuiChange = false;

    public static void attachEventListeners(IEventBus eventBus) {
        eventBus.addListener(EventPriority.HIGHEST, MouseUtil::onGuiOpen);
    }

    public static void ungrab() {
        Minecraft.getInstance().mouseHelper.ungrabMouse();
    }

    public static void grab() {
        preventGuiChange = true;
        Minecraft.getInstance().mouseHelper.grabMouse();
    }

    private static void onGuiOpen(GuiOpenEvent event) {
        if (preventGuiChange) {
            preventGuiChange = false;
            event.setCanceled(true);
        }
    }

}
