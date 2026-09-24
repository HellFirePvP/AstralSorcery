/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.screen.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.*;
import java.util.function.Predicate;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ScreenEffectTicketManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ScreenEffectTicketManager {

    private static final ScreenEffectTicketManager INSTANCE = new ScreenEffectTicketManager();

    private final Map<TickingTicket, ScreenEffectContainer<?, ?>> knownTickets = new HashMap<>();

    private boolean clear = false;
    private final Set<ScreenEffectTicket<?, ?>> thisTickUsedTickets = new HashSet<>();

    private ScreenEffectTicketManager() {}

    public static ScreenEffectTicketManager getInstance() {
        return INSTANCE;
    }

    private Optional<TickingTicket> findTicket(Predicate<ScreenEffectTicket<?, ?>> test) {
        return this.knownTickets.keySet().stream()
                .filter(tickingTicket -> test.test(tickingTicket.ticket))
                .findFirst();
    }

    public <C extends ScreenEffectContainer<C, T>, T extends ScreenEffectTicket<C, T>> C refreshOrCreate(T ticket) {
        return this.findTicket(existing -> existing.equals(ticket))
                .map(existing -> {
                    existing.refresh();
                    return (C) this.knownTickets.get(existing);
                }).orElseGet(() -> {
                    C ct = ticket.createEffectContainer();
                    this.knownTickets.put(new TickingTicket(ticket), ct);
                    return ct;
                });
    }

    public void tick(ClientTickEvent.Post event) {
        this.thisTickUsedTickets.clear();

        if (this.clear) {
            this.knownTickets.values().forEach(ScreenEffectContainer::clear);
            this.knownTickets.clear();
            this.clear = false;
            return;
        }

        Entity view = Minecraft.getInstance().getCameraEntity();
        if (view == null) view = Minecraft.getInstance().player;
        if (view == null) {
            clearAllEffects();
            return;
        }

        Iterator<TickingTicket> ticketIt = this.knownTickets.keySet().iterator();
        while (ticketIt.hasNext()) {
            TickingTicket ticket = ticketIt.next();
            ScreenEffectContainer<?, ?> container = this.knownTickets.get(ticket);

            ticket.timeout--;
            container.tick();

            if (ticket.timeout <= 0) {
                container.clear();
                ticket.ticket.invalidate();
                ticketIt.remove();
            }
        }
    }

    public void clearAllEffects() {
        this.clear = true;
    }

    public boolean canAddEffects(ScreenEffectTicket<?, ?> ticket) {
        return this.thisTickUsedTickets.add(ticket);
    }

    private static class TickingTicket {

        private final ScreenEffectTicket<?, ?> ticket;
        private int timeout;

        private TickingTicket(ScreenEffectTicket<?, ?> ticket) {
            this.ticket = ticket;
            this.refresh();
        }

        public void refresh() {
            this.timeout = 2;
            this.ticket.validate();
        }
    }

}
