/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import net.minecraft.world.IWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerIO
 * Created by HellFirePvP
 * Date: 29.06.2019 / 20:17
 */
public class EventHandlerIO {

    private EventHandlerIO() {}

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(EventHandlerIO::onUnload);
    }

    private static void onUnload(WorldEvent.Unload event) {
        IWorld w = event.getWorld();
        SkyHandler.getInstance().informWorldUnload(w);
    }

}
