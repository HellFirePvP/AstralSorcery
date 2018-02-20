/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeoutList
 * Created by HellFirePvP
 * Date: 17.11.2016 / 22:34
 */
public class TimeoutList<V> implements ITickHandler, Iterable<V> {

    private final TimeoutDelegate<V> delegate;
    private final EnumSet<TickEvent.Type> tickTypes;

    private List<TimeoutEntry<V>> tickEntries = new LinkedList<>();

    public TimeoutList(@Nullable TimeoutDelegate<V> delegate, TickEvent.Type... types) {
        this.delegate = delegate;
        this.tickTypes = EnumSet.noneOf(TickEvent.Type.class);
        for (TickEvent.Type type : types) {
            if(type != null) this.tickTypes.add(type);
        }
    }

    public void add(V value) {
        this.add(0, value);
    }

    public void add(int timeout, V value) {
        if(value == null) return;

        this.tickEntries.add(new TimeoutEntry<>(timeout, value));
    }

    public boolean setTimeout(int timeout, @Nonnull V value) {
        for (TimeoutEntry<V> entry : tickEntries) {
            if(entry.value.equals(value)) {
                entry.timeout = timeout;
                return true;
            }
        }
        return false;
    }

    public boolean setOrAddTimeout(int timeout, @Nonnull V value) {
        if(!contains(value)) {
            add(timeout, value);
            return true;
        } else {
            return setTimeout(timeout, value);
        }
    }

    public boolean contains(V value) {
        if(value == null) return false;
        for (TimeoutEntry<V> entry : tickEntries) {
            if(entry.value.equals(value)) return true;
        }
        return false;
    }

    public int getTimeout(V value) {
        for (TimeoutEntry<V> entry : tickEntries) {
            if(entry.value.equals(value)) {
                return entry.timeout;
            }
        }
        return -1;
    }

    public void addAll(TimeoutList<V> entries) {
        if(entries == null) return;

        for(TimeoutEntry<V> entry : entries.tickEntries) {
            setOrAddTimeout(entry.timeout, entry.value);
        }
    }

    public boolean isEmpty() {
        return tickEntries.isEmpty();
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Iterator<TimeoutEntry<V>> iterator = tickEntries.iterator();
        while (iterator.hasNext()) {
            TimeoutEntry<V> entry = iterator.next();
            entry.timeout--;
            if(entry.timeout <= 0) {
                if(delegate != null) {
                    delegate.onTimeout(entry.value);
                }
                iterator.remove();
            }
        }
    }

    @Override
    public Iterator<V> iterator() {
        return new Iterator<V>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < tickEntries.size();
            }

            @Override
            public V next() {
                V val = tickEntries.get(index).value;
                index++;
                return val;
            }
        };
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
        return "TimeoutList";
    }

    public static interface TimeoutDelegate<V> {

        public void onTimeout(V object);

    }

    private static class TimeoutEntry<V> {

        private int timeout;
        @Nonnull private V value;

        private TimeoutEntry(int timeout, @Nonnull V value) {
            this.timeout = timeout;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeoutEntry that = (TimeoutEntry) o;
            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

}
