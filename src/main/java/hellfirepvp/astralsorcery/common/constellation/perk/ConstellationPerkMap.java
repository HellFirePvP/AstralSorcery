/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationPerkMap
 * Created by HellFirePvP
 * Date: 22.11.2016 / 01:15
 */
public class ConstellationPerkMap {

    private HashMap<ConstellationPerks, Position> perks = new HashMap<>();
    private HashMap<ConstellationPerks, PerkOrder> perkOrderMap = new HashMap<>();
    private List<Dependency> perkDependencies = new LinkedList<>();

    public ConstellationPerkMap() {}

    public ConstellationPerkMap addPerk(ConstellationPerks perk, PerkOrder obtainOrder, int x, int y, ConstellationPerks... dependencies) {
        if(perks.containsKey(perk)) {
            AstralSorcery.log.warn("[AstralSorcery] Tried to register the same perk (id=" + perk.ordinal() + ") twice to a perk map! Skipping registration...");
            return this;
        }
        Position p = new Position(x % 15, y % 15);
        perks.put(perk, p);
        perkOrderMap.put(perk, obtainOrder);
        for (ConstellationPerks from : dependencies) {
            if(perks.containsKey(from)) {
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

    /*private boolean hasPerk(ConstellationPerks perk) {
        for (OrderedPerkEntry perkEntry : perks.keySet()) {
            if (perkEntry.perk.ordinal() == perk.ordinal()) {
                return true;
            }
        }
        return false;
    }*/

    public List<Dependency> getPerkDependencies() {
        return Collections.unmodifiableList(perkDependencies);
    }

    @Nullable
    public Position getPosition(ConstellationPerks perk) {
        return perks.get(perk);
    }

    public Map<ConstellationPerks, Position> getPerks() {
        return Collections.unmodifiableMap(perks);
    }

    @Deprecated
    public List<ConstellationPerks> getAvailablePerksFor(EntityPlayer player, PerkOrder availableOrder, boolean client) {
        PlayerProgress prog = client ? ResearchManager.clientProgress : ResearchManager.getProgress(player);
        if(prog == null) return null;
        Map<ConstellationPerk, Integer> appliedPerks = prog.getAppliedPerks();
        List<ConstellationPerks> available = new LinkedList<>();
        for (ConstellationPerks entry : perkOrderMap.keySet()) {
            PerkOrder order = perkOrderMap.get(entry);
            if(order.ordinal() <= availableOrder.ordinal()) {
                available.add(entry);
            }
        }
        for (ConstellationPerk perk : appliedPerks.keySet()) {
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

        DEFAULT
        //EXTENDED,
        //ALL

    }

    /*public static class PerkOrderer implements Comparator<OrderedPerkEntry> {

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
            return perk.ordinal() == that.perk.ordinal();
        }

        @Override
        public int hashCode() {
            return perk != null ? perk.hashCode() : 0;
        }
    }*/

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
