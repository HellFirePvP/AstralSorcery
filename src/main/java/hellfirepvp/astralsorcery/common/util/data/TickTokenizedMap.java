package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Iterator;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TickTokenizedMap
 * Created by HellFirePvP
 * Date: 07.11.2016 / 11:25
 */
public class TickTokenizedMap<K, V extends TickTokenizedMap.TickMapToken<?>> extends TokenizedMap<K, V> implements ITickHandler {

    private EnumSet<TickEvent.Type> tickTypes;

    public TickTokenizedMap(@Nonnull TickEvent.Type first, TickEvent.Type... restTypes) {
        this.tickTypes = EnumSet.of(first, restTypes);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        Iterator<Entry<K, V>> iteratorEntries = entrySet().iterator();
        while (iteratorEntries.hasNext()) {
            Entry<K, V> entry = iteratorEntries.next();
            entry.getValue().tick();
            if(entry.getValue().getRemainingTimeout() <= 0) {
                iteratorEntries.remove();
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
        return "TickTokenMap";
    }

    public static class SimpleTickToken<E> implements TickMapToken<E> {

        private final E value;
        private int timeout;

        public SimpleTickToken(@Nonnull E value, int initialTimeout) {
            this.value = value;
            this.timeout = initialTimeout;
        }

        @Override
        public int getRemainingTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

        public void addToTimeout(int timeout) {
            this.timeout += timeout;
        }

        @Override
        public void tick() {
            timeout--;
        }

        @Override
        public E getValue() {
            return value;
        }
    }

    public static interface TickMapToken<E> extends MapToken<E> {

        public int getRemainingTimeout();

        public void tick();

    }

}
