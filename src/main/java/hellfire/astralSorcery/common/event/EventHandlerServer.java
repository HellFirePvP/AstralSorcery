package hellfire.astralSorcery.common.event;

import hellfire.astralSorcery.common.constellation.ConstellationHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 21:55
 */
public class EventHandlerServer {

    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
        if(event.world.provider.getDimensionId() == 0) {
            ConstellationHandler.informTick(event.world);
        }
    }

}
