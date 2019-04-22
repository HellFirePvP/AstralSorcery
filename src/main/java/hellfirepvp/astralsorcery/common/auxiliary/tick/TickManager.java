/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.tick;

import hellfirepvp.astralsorcery.common.event.ClientInitializedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TickManager
 * Created by HellFirePvP
 * Date: 04.08.2016 / 11:20
 */
public class TickManager {

    private static boolean firedFirstTick = false;
    public static final TickManager INSTANCE = new TickManager();

    private Map<TickEvent.Type, List<ITickHandler>> registeredTickHandlers = new HashMap<>();

    private TickManager() {
        for (TickEvent.Type type : TickEvent.Type.values()) {
            registeredTickHandlers.put(type, new LinkedList<>());
        }
    }

    public void attachListeners(IEventBus eventBus) {
        eventBus.addListener(this::worldTick);
        eventBus.addListener(this::serverTick);
        eventBus.addListener(this::playerTick);
        eventBus.addListener(this::renderTick);
        eventBus.addListener(this::clientTick);
    }

    public void register(ITickHandler handler) {
        for (TickEvent.Type type : handler.getHandledTypes()) {
            registeredTickHandlers.get(type).add(handler);
        }
    }

    private void worldTick(TickEvent.WorldTickEvent event) {
        TickEvent.Phase ph = event.phase;
        for (ITickHandler handler : registeredTickHandlers.get(TickEvent.Type.WORLD)) {
            if(handler.canFire(ph)) handler.tick(TickEvent.Type.WORLD, event.world);
        }
    }

    private void serverTick(TickEvent.ServerTickEvent event) {
        TickEvent.Phase ph = event.phase;
        for (ITickHandler handler : registeredTickHandlers.get(TickEvent.Type.SERVER)) {
            if(handler.canFire(ph)) handler.tick(TickEvent.Type.SERVER);
        }
    }

    private void clientTick(TickEvent.ClientTickEvent event) {
        TickEvent.Phase ph = event.phase;
        for (ITickHandler handler : registeredTickHandlers.get(TickEvent.Type.CLIENT)) {
            if(handler.canFire(ph)) handler.tick(TickEvent.Type.CLIENT);
        }

        if(!firedFirstTick) {
            firedFirstTick = true;
            MinecraftForge.EVENT_BUS.post(new ClientInitializedEvent());
        }
    }

    private void renderTick(TickEvent.RenderTickEvent event) {
        TickEvent.Phase ph = event.phase;
        for (ITickHandler handler : registeredTickHandlers.get(TickEvent.Type.RENDER)) {
            if(handler.canFire(ph)) handler.tick(TickEvent.Type.RENDER, event.renderTickTime);
        }
    }

    private void playerTick(TickEvent.PlayerTickEvent event) {
        TickEvent.Phase ph = event.phase;
        for (ITickHandler handler : registeredTickHandlers.get(TickEvent.Type.PLAYER)) {
            if(handler.canFire(ph)) handler.tick(TickEvent.Type.PLAYER, event.player, event.side);
        }
    }

}
