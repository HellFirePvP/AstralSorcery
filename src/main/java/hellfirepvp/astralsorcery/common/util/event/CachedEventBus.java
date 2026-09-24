/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.event;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CachedEventBus
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CachedEventBus implements IEventBus {

    private final IEventBus decorated;
    private final List<Object> registeredListeners = new ArrayList<>();

    private CachedEventBus(IEventBus decorated) {
        this.decorated = decorated;
    }

    public static CachedEventBus of(IEventBus bus) {
        return new CachedEventBus(bus);
    }

    public void unregisterAll() {
        this.registeredListeners.forEach(this.decorated::unregister);
        this.registeredListeners.clear();
    }

    @Override
    public void register(Object target) {
        this.decorated.register(target);
        this.registeredListeners.add(target);
    }

    @Override
    public <T extends Event> void addListener(Consumer<T> consumer) {
        this.decorated.addListener(consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(Class<T> eventType, Consumer<T> consumer) {
        this.decorated.addListener(eventType, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, Consumer<T> consumer) {
        this.decorated.addListener(priority, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, Class<T> eventType, Consumer<T> consumer) {
        this.decorated.addListener(priority, eventType, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Consumer<T> consumer) {
        this.decorated.addListener(priority, receiveCanceled, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
        this.decorated.addListener(priority, receiveCanceled, eventType, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(boolean receiveCanceled, Consumer<T> consumer) {
        this.decorated.addListener(receiveCanceled, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public <T extends Event> void addListener(boolean receiveCanceled, Class<T> eventType, Consumer<T> consumer) {
        this.decorated.addListener(receiveCanceled, eventType, consumer);
        this.registeredListeners.add(consumer);
    }

    @Override
    public void unregister(Object object) {
        this.decorated.unregister(object);
        this.registeredListeners.remove(object);
    }

    @Override
    public <T extends Event> T post(T event) {
        return this.decorated.post(event);
    }

    @Override
    public <T extends Event> T post(EventPriority phase, T event) {
        return this.decorated.post(phase, event);
    }

    @Override
    public void start() {
        this.decorated.start();
    }
}
