/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.event;

import hellfirepvp.astralsorcery.common.util.SidedHelper;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SidedEventBus
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class SidedEventBus {

    private final IEventBus decorated;
    private final LogicalSide side;

    private SidedEventBus(IEventBus decorated, LogicalSide side) {
        this.decorated = decorated;
        this.side = side;
    }

    public static SidedEventBus of(IEventBus bus, LogicalSide side) {
        return new SidedEventBus(bus, side);
    }

    public LogicalSide getSide() {
        return this.side;
    }

    public <T extends Event> void addListener(Class<T> eventType, Function<T, LogicalSide> sideExtractor, Consumer<T> consumer) {
        this.decorated.addListener(eventType, this.wrapConsumer(sideExtractor, consumer));
    }

    public <T extends Event> void addListener(EventPriority priority, Class<T> eventType, Function<T, LogicalSide> sideExtractor, Consumer<T> consumer) {
        this.decorated.addListener(priority, eventType, this.wrapConsumer(sideExtractor, consumer));
    }

    public <T extends Event> void addListener(EventPriority priority, boolean receiveCanceled, Class<T> eventType, Function<T, LogicalSide> sideExtractor, Consumer<T> consumer) {
        this.decorated.addListener(priority, receiveCanceled, eventType, this.wrapConsumer(sideExtractor, consumer));
    }

    public <T extends Event> void addListener(boolean receiveCanceled, Class<T> eventType, Function<T, LogicalSide> sideExtractor, Consumer<T> consumer) {
        this.decorated.addListener(receiveCanceled, eventType, this.wrapConsumer(sideExtractor, consumer));
    }

    private <T extends Event> Consumer<T> wrapConsumer(Function<T, LogicalSide> sideExtractor, Consumer<T> consumer) {
        return event -> {
            if (sideExtractor.apply(event) == this.side) {
                consumer.accept(event);
            }
        };
    }

    public static <T extends EntityEvent> Function<T, LogicalSide> entityEvent() {
        return event -> SidedHelper.getSide(event.getEntity());
    }

    public static <T extends BlockEvent> Function<T , LogicalSide> blockEvent() {
        return event -> event.getLevel().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER;
    }
}
