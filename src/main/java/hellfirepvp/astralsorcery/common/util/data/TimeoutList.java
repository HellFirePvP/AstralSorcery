package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TimeoutList
 * Created by HellFirePvP
 * Date: 17.11.2016 / 22:34
 */
public class TimeoutList<V> implements ITickHandler {

    private final TimeoutDelegate<V> delegate;
    private final EnumSet<TickEvent.Type> tickTypes;

    private List<TimeoutEntry<V>> tickEntries = new LinkedList<>();
    private final Object syncLock = new Object();

    public TimeoutList(@Nullable TimeoutDelegate<V> delegate, @Nonnull TickEvent.Type first, @Nonnull TickEvent.Type... types) {
        this.delegate = delegate;
        this.tickTypes = EnumSet.of(first, types);
    }

    public void add(V value) {
        this.add(0, value);
    }

    public void add(int timeout, V value) {
        synchronized (syncLock) {
            this.tickEntries.add(new TimeoutEntry<>(timeout, value));
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        synchronized (syncLock) {
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
        private V value;

        private TimeoutEntry(int timeout, V value) {
            this.timeout = timeout;
            this.value = value;
        }
    }

}
