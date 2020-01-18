/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tick;

import com.google.common.collect.Lists;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraftforge.event.TickEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeoutListContainer
 * Created by HellFirePvP
 * Date: 06.07.2019 / 21:48
 */
public class TimeoutListContainer<K, V> implements ITickHandler {

    private EnumSet<TickEvent.Type> tickTypes;
    private final ContainerTimeoutDelegate<K, V> delegate;
    private Map<K, TimeoutList<V>> timeoutListMap = new HashMap<>();

    public TimeoutListContainer(TickEvent.Type... restTypes) {
        this(null, restTypes);
    }

    public TimeoutListContainer(@Nullable ContainerTimeoutDelegate<K, V> delegate, TickEvent.Type... types) {
        this.tickTypes = EnumSet.noneOf(TickEvent.Type.class);
        for (TickEvent.Type type : types) {
            if (type != null) this.tickTypes.add(type);
        }
        this.delegate = delegate;
    }

    public boolean hasList(K key) {
        return timeoutListMap.containsKey(key);
    }

    @Nullable
    public TimeoutList<V> removeList(K key) {
        TimeoutList<V> ret = timeoutListMap.remove(key);
        ret.forEach((v) -> delegate.onContainerTimeout(key, v));
        return ret;
    }

    public TimeoutList<V> getOrCreateList(K key) {
        TimeoutList<V> list = timeoutListMap.get(key);
        if (list == null) {
            list = new TimeoutList<>(new RedirectTimeoutDelegate<>(key, delegate));
            timeoutListMap.put(key, list);
        }
        return list;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Iterator<Map.Entry<K, TimeoutList<V>>> it = timeoutListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, TimeoutList<V>> entry = it.next();
            TimeoutList<V> list = entry.getValue();
            list.tick(type, context);
            if (list.isEmpty()) {
                it.remove();
            }
        }
    }

    public void clear() {
        Lists.newArrayList(timeoutListMap.keySet()).forEach(this::removeList);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return tickTypes;
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "TimeoutListContainer";
    }

    private static class RedirectTimeoutDelegate<K, V> implements TimeoutList.TimeoutDelegate<V> {

        private final K key;
        private final ContainerTimeoutDelegate<K, V> delegate;

        private RedirectTimeoutDelegate(K key, @Nullable ContainerTimeoutDelegate<K, V> delegate) {
            this.key = key;
            this.delegate = delegate;
        }

        @Override
        public void onTimeout(V object) {
            if (delegate != null) {
                delegate.onContainerTimeout(key, object);
            }
        }

    }

    public static interface ContainerTimeoutDelegate<K, V> {

        public void onContainerTimeout(K key, V timedOut);

    }

}