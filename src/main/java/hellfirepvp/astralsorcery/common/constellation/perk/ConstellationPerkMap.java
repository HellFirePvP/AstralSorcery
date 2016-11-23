package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerkMap
 * Created by HellFirePvP
 * Date: 22.11.2016 / 01:15
 */
public class ConstellationPerkMap {

    private TreeMap<OrderedPerkEntry, Position> perks = new TreeMap<>(new PerkOrderer());
    private List<Dependency> perkDependencies = new LinkedList<>();

    public ConstellationPerkMap() {}

    public ConstellationPerkMap addPerk(ConstellationPerks perk, PerkOrder obtainOrder, int x, int y, ConstellationPerks... dependencies) {
        OrderedPerkEntry entry = new OrderedPerkEntry(obtainOrder, perk);
        if(perks.containsKey(entry)) {
            AstralSorcery.log.warn("[AstralSorcery] Tried to register the same perk (id=" + perk.ordinal() + ") twice to a perk map! Skipping registration...");
            return this;
        }
        Position p = new Position(x % 15, y % 15);
        perks.put(entry, p);
        for (ConstellationPerks from : dependencies) {
            OrderedPerkEntry query = new OrderedPerkEntry(null, from);
            if(perks.containsKey(query)) {
                boolean found = false;
                for (Dependency c : perkDependencies) {
                    if((c.from.equals(from) && c.to.equals(perk)) ||
                            (c.to.equals(from) && c.from.equals(perk))) {
                        found = true;
                    }
                }
                if(!found) {
                    perkDependencies.add(new Dependency(from, perk));
                } else {
                    AstralSorcery.log.warn("[AstralSorcery] Tried to register a perk-dependency that already exists! Skipping registration...");
                }
            } else {
                AstralSorcery.log.warn("[AstralSorcery] Tried to register a perk-dependency from a non-registered perk! Skipping registration...");
            }
        }
        return this;
    }

    public List<Dependency> getPerkDependencies() {
        return Collections.unmodifiableList(perkDependencies);
    }

    @Nullable
    public Position getPosition(ConstellationPerks perk) {
        return perks.get(new OrderedPerkEntry(null, perk));
    }

    public Map<OrderedPerkEntry, Position> getPerks() {
        return Collections.unmodifiableMap(perks);
    }

    public List<ConstellationPerks> getAvailablePerksFor(EntityPlayer player, PerkOrder availableOrder, boolean client) {
        PlayerProgress prog = client ? ResearchManager.clientProgress : ResearchManager.getProgress(player);
        if(prog == null) return null;
        List<ConstellationPerk> appliedPerks = prog.getAppliedPerks();
        List<ConstellationPerks> available = new LinkedList<>();
        for (OrderedPerkEntry entry : perks.keySet()) {
            if(entry.order.ordinal() <= availableOrder.ordinal()) {
                available.add(entry.perk);
            }
        }
        for (ConstellationPerk perk : appliedPerks) {
            Iterator<ConstellationPerks> iterator = available.iterator();
            while (iterator.hasNext()) {
                ConstellationPerks av = iterator.next();
                if (av.isInstanceOfThis(perk)) {
                    iterator.remove();
                }
            }
        }
        return available;
    }

    public static enum PerkOrder {

        DEFAULT,
        EXTENDED,
        ALL

    }

    public static class PerkOrderer implements Comparator<OrderedPerkEntry> {

        public PerkOrderer() {}

        @Override
        public int compare(OrderedPerkEntry o1, OrderedPerkEntry o2) {
            return o1.order.ordinal() - o2.order.ordinal();
        }

    }


    public static class OrderedPerkEntry {

        private final PerkOrder order;
        public final ConstellationPerks perk;

        public OrderedPerkEntry(PerkOrder order, ConstellationPerks perk) {
            this.order = order;
            this.perk = perk;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderedPerkEntry that = (OrderedPerkEntry) o;
            return perk == that.perk;
        }

        @Override
        public int hashCode() {
            return perk != null ? perk.hashCode() : 0;
        }
    }

    public static class Dependency {

        public final ConstellationPerks from, to;

        public Dependency(ConstellationPerks from, ConstellationPerks to) {
            this.from = from;
            this.to = to;
        }
    }

    public static class Position {

        public final int x, y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

}
