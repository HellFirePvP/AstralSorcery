package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.research.ProgressionTier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationRegistry
 * Created by HellFirePvP
 * Date: 06.02.2016 01:42
 */
public class ConstellationRegistry {

    private static TreeMap<Integer, Tier> tiers = new TreeMap<>();

    public static void registerConstellation(int tier, Constellation constellation) {
        Tier t = getTier(tier);
        if (t == null) {
            AstralSorcery.log.warn("Tried to register Constellation on TierID=" + tier + " which does not exist!");
            return;
        }
        t.addConstellation(constellation);
    }

    public static void registerTier(int tierNumber, ProgressionTier progressionNeeded, Tier.RInformation renderInfo, float chanceForShowingUp) {
        registerTier(tierNumber, progressionNeeded, renderInfo, chanceForShowingUp, null);
    }

    public static void registerTier(int tierNumber, ProgressionTier progressionNeeded, Tier.RInformation renderInfo, float chanceForShowingUp, AppearanceCondition condition) {
        if (tiers.containsKey(tierNumber)) return;
        if (tierNumber < 0 || (tierNumber > 0 && !tiers.containsKey(tierNumber - 1))) return;
        Tier t = new Tier(tierNumber, progressionNeeded, chanceForShowingUp, renderInfo, condition);
        tiers.put(tierNumber, t);
    }

    public static Tier getTier(int tierNumber) {
        return tiers.get(tierNumber);
    }

    @Nullable
    public static Constellation getConstellationByName(String name) {
        if(name == null) return null;

        for (Tier tier : tiers.values()) {
            for (Constellation c : tier.getConstellations()) {
                if (c.getName().equals(name)) return c;
            }
        }
        return null;
    }

    public static List<Constellation> resolve(List<String> constellationsAsStrings) {
        List<Constellation> resolved = new LinkedList<>();
        for (String s : constellationsAsStrings) {
            Constellation c = getConstellationByName(s);
            if(c != null) resolved.add(c);
        }
        return resolved;
    }

    public static int getHighestTierNumber() {
        return tiers.lastKey();
    }

    public static Collection<Tier> ascendingTiers() {
        LinkedList<Tier> sortedTiers = new LinkedList<>();
        for (Integer tierInt : tiers.keySet()) {
            sortedTiers.addLast(tiers.get(tierInt));
        }
        return sortedTiers;
    }

    public static List<Constellation> getAllConstellations() {
        List<Constellation> constellations = new LinkedList<>();
        for (Tier t : ascendingTiers()) {
            constellations.addAll(t.getConstellations().stream().collect(Collectors.toList()));
        }
        return constellations;
    }

    public static int getConstellationId(Constellation c) {
        List<Constellation> allConstellations = getAllConstellations();
        return allConstellations.indexOf(c);
    }

    public static Constellation getConstellationById(int id) {
        List<Constellation> allConstellations = getAllConstellations();
        return allConstellations.get(id);
    }

}
