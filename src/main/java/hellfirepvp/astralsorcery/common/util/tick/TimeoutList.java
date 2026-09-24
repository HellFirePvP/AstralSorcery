/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tick;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeoutList
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TimeoutList<V> implements Iterable<V>, TickableListener {

    private final TimeoutDelegate<V> delegate;

    private final List<TimeoutEntry<V>> tickEntries = new LinkedList<>();

    public TimeoutList() {
        this(null);
    }

    public TimeoutList(@Nullable TimeoutDelegate<V> delegate) {
        this.delegate = delegate;
    }

    public void add(V value) {
        this.add(0, value);
    }

    public void add(int timeout, V value) {
        if (value == null) return;

        this.tickEntries.add(new TimeoutEntry<>(timeout, value));
    }

    public boolean setTimeout(int timeout, @Nonnull V value) {
        for (TimeoutEntry<V> entry : tickEntries) {
            if (entry.value.equals(value)) {
                entry.timeout = timeout;
                return true;
            }
        }
        return false;
    }

    public boolean setOrAddTimeout(int timeout, @Nonnull V value) {
        if (!contains(value)) {
            add(timeout, value);
            return true;
        } else {
            return setTimeout(timeout, value);
        }
    }

    public boolean contains(V value) {
        if (value == null) return false;
        return this.tickEntries.stream().anyMatch(entry -> entry.value.equals(value));
    }

    public boolean remove(V key) {
        return this.removeIf(key::equals);
    }

    public boolean removeIf(Predicate<V> test) {
        return this.tickEntries.removeIf(e -> test.test(e.value));
    }

    public int getTimeout(V value) {
        for (TimeoutEntry<V> entry : this.tickEntries) {
            if (entry.value.equals(value)) {
                return entry.timeout;
            }
        }
        return -1;
    }

    public void addAll(TimeoutList<V> entries) {
        if (entries == null) return;

        for (TimeoutEntry<V> entry : entries.tickEntries) {
            setOrAddTimeout(entry.timeout, entry.value);
        }
    }

    public boolean isEmpty() {
        return this.tickEntries.isEmpty();
    }

    @Override
    public void tick() {
        Iterator<TimeoutEntry<V>> iterator = this.tickEntries.iterator();
        while (iterator.hasNext()) {
            TimeoutEntry<V> entry = iterator.next();
            entry.timeout--;
            if (entry.timeout <= 0) {
                if (delegate != null) {
                    delegate.onTimeout(entry.value);
                }
                iterator.remove();
            }
        }
    }

    public void clear() {
        if (delegate != null) {
            tickEntries.forEach(entry -> {
                delegate.onTimeout(entry.value);
            });
        }
        tickEntries.clear();
    }

    @Override
    public Iterator<V> iterator() {
        Iterator<TimeoutEntry<V>> entryIterator = this.tickEntries.iterator();
        return new Iterator<>() {

            @Override
            public boolean hasNext() {
                return entryIterator.hasNext();
            }

            @Override
            public V next() {
                return entryIterator.next().value;
            }

            @Override
            public void remove() {
                entryIterator.remove();
            }
        };
    }

    public interface TimeoutDelegate<V> {

        void onTimeout(V object);

    }

    private static class TimeoutEntry<V> {

        @Nonnull
        private final V value;
        private int timeout;

        private TimeoutEntry(int timeout, @Nonnull V value) {
            this.timeout = timeout;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeoutEntry<?> that = (TimeoutEntry<?>) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

}