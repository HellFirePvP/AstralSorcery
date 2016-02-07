package hellfire.astralSorcery.client.event;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 14:36
 */
public class ClientTickHandler {

    @SubscribeEvent
    public void onCTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.END) return;
    }

}
