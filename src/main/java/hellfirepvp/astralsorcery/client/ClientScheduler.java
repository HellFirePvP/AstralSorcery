package hellfirepvp.astralsorcery.client;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientScheduler
 * Created by HellFirePvP
 * Date: 11.08.2016 / 13:04
 */
public class ClientScheduler implements ITickHandler {

    private static int clientTick = 0;

    private Map<Runnable, Integer> queuedRunnables = new HashMap<>();
    private final Object lock = new Object();

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        clientTick++;

        synchronized (lock) {
            Iterator<Runnable> iterator = queuedRunnables.keySet().iterator();
            while (iterator.hasNext()) {
                Runnable r = iterator.next();
                int delay = queuedRunnables.get(r);
                delay--;
                if(delay <= 0) {
                    r.run();
                    iterator.remove();
                } else {
                    queuedRunnables.put(r, delay);
                }
            }
        }
    }

    public static int getClientTick() {
        return clientTick;
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Client Scheduler";
    }

    public void addRunnable(Runnable r, int tickDelay) {
        synchronized (lock) {
            queuedRunnables.put(r, tickDelay);
        }
    }

}
