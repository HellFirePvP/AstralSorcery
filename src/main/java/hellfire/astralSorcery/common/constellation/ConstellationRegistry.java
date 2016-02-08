package hellfire.astralSorcery.common.constellation;

import hellfire.astralSorcery.api.constellation.IConstellation;
import hellfire.astralSorcery.api.constellation.IConstellationTier;
import hellfire.astralSorcery.common.AstralSorcery;

import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 06.02.2016 01:42
 */
public class ConstellationRegistry {

    private static TreeMap<Integer, IConstellationTier> tiers = new TreeMap<Integer, IConstellationTier>();

    public static void registerConstellation(int tier, IConstellation constellation) {
        IConstellationTier t = getTier(tier);
        if(t == null) {
            AstralSorcery.log.warn("Tried to register Constellation on TierID=" + tier + " which does not exist!");
            return;
        }
        t.addConstellation(constellation);
    }

    public static void registerTier(int tierNumber, IConstellationTier.RInformation renderInfo, float chanceForShowingUp) {
        if(tiers.containsKey(tierNumber)) return;
        if(tierNumber < 0 || (tierNumber > 0 && !tiers.containsKey(tierNumber - 1))) return;
        IConstellationTier t = new Tier(tierNumber, renderInfo, chanceForShowingUp);
        tiers.put(tierNumber, t);
    }

    public static IConstellationTier getTier(int tierNumber) {
        return tiers.get(tierNumber);
    }

    public static int getHighestTierNumber() {
        return tiers.lastKey();
    }

    public static Collection<IConstellationTier> ascendingTiers() {
        LinkedList<IConstellationTier> sortedTiers = new LinkedList<IConstellationTier>();
        for(Integer tierInt : tiers.keySet()) {
            sortedTiers.addLast(tiers.get(tierInt));
        }
        return sortedTiers;
    }

}
