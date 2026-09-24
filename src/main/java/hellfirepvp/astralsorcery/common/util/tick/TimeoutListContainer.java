/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tick;

import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeoutListContainer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TimeoutListContainer<K, V> implements TickableListener {

    private final ContainerTimeoutDelegate<K, V> delegate;
    private final Map<K, TimeoutList<V>> timeoutListMap = new HashMap<>();

    public TimeoutListContainer() {
        this(null);
    }

    public TimeoutListContainer(@Nullable ContainerTimeoutDelegate<K, V> delegate) {
        this.delegate = delegate;
    }

    public boolean hasList(K key) {
        return this.timeoutListMap.containsKey(key);
    }

    @Nullable
    public TimeoutList<V> removeList(K key) {
        TimeoutList<V> ret = this.timeoutListMap.remove(key);
        if (this.delegate != null) {
            ret.forEach((v) -> this.delegate.onContainerTimeout(key, v));
        }
        return ret;
    }

    public boolean removeAnyListEntry(Predicate<V> valueTest) {
        boolean removed = false;
        for (Map.Entry<K, TimeoutList<V>> entry : this.timeoutListMap.entrySet()) {
            Iterator<V> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                V value = iterator.next();
                if (valueTest.test(value)) {
                    if (this.delegate != null) {
                        this.delegate.onContainerTimeout(entry.getKey(), value);
                    }
                    iterator.remove();
                    removed = true;
                }
            }
        }
        return removed;
    }

    public TimeoutList<V> getOrCreateList(K key) {
        TimeoutList<V> list = this.timeoutListMap.get(key);
        if (list == null) {
            list = new TimeoutList<>(new RedirectTimeoutDelegate<>(key, delegate));
            this.timeoutListMap.put(key, list);
        }
        return list;
    }

    @Override
    public void tick() {
        Iterator<Map.Entry<K, TimeoutList<V>>> it = this.timeoutListMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, TimeoutList<V>> entry = it.next();
            TimeoutList<V> list = entry.getValue();
            list.tick();
            if (list.isEmpty()) {
                it.remove();
            }
        }
    }

    public void clear() {
        Lists.newArrayList(this.timeoutListMap.keySet()).forEach(this::removeList);
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

    public static class ForwardingTimeoutDelegate<K, V extends TimeoutList.TimeoutDelegate<K>> implements ContainerTimeoutDelegate<K, V> {

        @Override
        public void onContainerTimeout(K key, V timedOut) {
            timedOut.onTimeout(key);
        }

    }

    public interface ContainerTimeoutDelegate<K, V> {

        void onContainerTimeout(K key, V timedOut);

    }
}
