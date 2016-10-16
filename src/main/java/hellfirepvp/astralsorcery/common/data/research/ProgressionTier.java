package hellfirepvp.astralsorcery.common.data.research;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ProgressionTier
 * Created by HellFirePvP
 * Date: 01.08.2016 / 22:32
 */
public enum ProgressionTier {

    DISCOVERY,
    BASIC_CRAFT,
    ATTENUATION,
    CONSTELLATION_CRAFT,
    NETWORKING,
    TRAIT_CRAFT,
    ENDGAME;
    //PAST_DIM

    public EnumGatedKnowledge.ViewCapability getViewCapability() {
        int ord = ordinal();
        EnumGatedKnowledge.ViewCapability cap = EnumGatedKnowledge.ViewCapability.NONE;
        if(ord >= BASIC_CRAFT.ordinal()) cap = EnumGatedKnowledge.ViewCapability.BASIC;
        if(ord >= CONSTELLATION_CRAFT.ordinal()) cap = EnumGatedKnowledge.ViewCapability.MOST;
        if(ord >= TRAIT_CRAFT.ordinal()) cap = EnumGatedKnowledge.ViewCapability.ALL;
        return cap;
    }

    public boolean hasNextTier() {
        return ordinal() < ProgressionTier.values().length - 1;
    }

    public boolean isThisLaterOrEqual(ProgressionTier other) {
        return ordinal() >= other.ordinal();
    }

}
