/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientUtils
 * Created by HellFirePvP
 * Date: 17.11.2018 / 17:21
 */
public class ClientUtils {

    public static boolean isKeybindUsed(KeyBinding key) {
        int keyCode = key.getKeyCode();
        try {
            return Keyboard.isKeyDown(keyCode);
        } catch (Throwable tr) {
            try {
                return Mouse.isButtonDown(keyCode);
            } catch (Throwable tr2) {
                return false;
            }
        }
    }

    public static void grabMouseCursor() {
        if (!Minecraft.IS_RUNNING_ON_MAC || !Mouse.isGrabbed()) {
            Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
        }
    }

    public static void ungrabMouseCursor() {
        if (!Minecraft.IS_RUNNING_ON_MAC || Mouse.isGrabbed()) {
            Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
        }
    }
}