package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.AstralSorcery;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRegistry
 * Created by HellFirePvP
 * Date: 06.02.2016 01:42
 */
public class ConstellationRegistry {

    private static TreeMap<Integer, Tier> tiers = new TreeMap<Integer, Tier>();

    public static void registerConstellation(int tier, Constellation constellation) {
        Tier t = getTier(tier);
        if(t == null) {
            AstralSorcery.log.warn("Tried to register Constellation on TierID=" + tier + " which does not exist!");
            return;
        }
        t.addConstellation(constellation);
    }

    public static void registerTier(int tierNumber, Tier.RInformation renderInfo, float chanceForShowingUp, Tier.AppearanceCondition condition) {
        if(tiers.containsKey(tierNumber)) return;
        if(tierNumber < 0 || (tierNumber > 0 && !tiers.containsKey(tierNumber - 1))) return;
        Tier t = new Tier(tierNumber, chanceForShowingUp, renderInfo, condition);
        tiers.put(tierNumber, t);
    }

    public static Tier getTier(int tierNumber) {
        return tiers.get(tierNumber);
    }

    public static Constellation getConstellationByName(String name) {
        for (Tier tier : tiers.values()) {
            for(Constellation c : tier.getConstellations()) {
                if(c.getName().equals(name)) return c;
            }
        }
        return null;
    }

    public static int getHighestTierNumber() {
        return tiers.lastKey();
    }

    public static Collection<Tier> ascendingTiers() {
        LinkedList<Tier> sortedTiers = new LinkedList<Tier>();
        for(Integer tierInt : tiers.keySet()) {
            sortedTiers.addLast(tiers.get(tierInt));
        }
        return sortedTiers;
    }

    public static Collection<Constellation> getAllConstellations() {
        List<Constellation> constellations = new LinkedList<Constellation>();
        for (Tier t : ascendingTiers()) {
            for(Constellation c : t.getConstellations()) constellations.add(c);
        }
        return constellations;
    }

}
