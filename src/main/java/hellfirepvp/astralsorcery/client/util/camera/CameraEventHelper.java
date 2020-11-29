/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.util.camera;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CameraEventHelper
 * Created by HellFirePvP
 * Date: 26.11.2020 / 17:33
 */
public class CameraEventHelper {

    public static void attachEventListeners(IEventBus eventBus) {
        eventBus.addListener(CameraEventHelper::onPlayerRender);
    }

    private static void onPlayerRender(RenderPlayerEvent.Pre event) {
        if (event.getPlayer() == Minecraft.getInstance().player && ClientCameraManager.INSTANCE.hasActiveTransformer()) {
            event.setCanceled(true);
        }
    }

}
