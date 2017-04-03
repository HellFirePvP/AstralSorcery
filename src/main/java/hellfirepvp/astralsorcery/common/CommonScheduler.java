/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CommonScheduler
 * Created by HellFirePvP
 * Date: 30.03.2017 / 22:23
 */
public class CommonScheduler implements ITickHandler {

    private static final Object lock = new Object();

    private boolean inTick = false;
    private Map<Runnable, Integer> queuedRunnables = new HashMap<>();
    private Map<Runnable, Integer> waitingRunnables = new HashMap<>();

    @Override
    public void tick(TickEvent.Type type, Object... context) {

        inTick = true;
        synchronized (lock) {
            inTick = true;
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
            inTick = false;
            for (Map.Entry<Runnable, Integer> waiting : waitingRunnables.entrySet()) {
                queuedRunnables.put(waiting.getKey(), waiting.getValue());
            }
        }
        waitingRunnables.clear();
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Common Scheduler";
    }

    public void addRunnable(Runnable r, int tickDelay) {
        synchronized (lock) {
            if(inTick) {
                waitingRunnables.put(r, tickDelay);
            } else {
                queuedRunnables.put(r, tickDelay);
            }
        }
    }

}
