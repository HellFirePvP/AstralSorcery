package hellfirepvp.astralsorcery.common.data.research;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: EnumGatedKnowledge
* Created by HellFirePvP
* Date: 01.08.2016 / 22:31
*/
public enum EnumGatedKnowledge {

    CRYSTAL_SIZE(ViewCapability.BASIC),
    CRYSTAL_PURITY(ViewCapability.BASIC),
    CRYSTAL_COLLECT(ViewCapability.MOST),
    COLLECTOR_TYPE(ViewCapability.MOST);

    private final ViewCapability capability;

    private EnumGatedKnowledge(ViewCapability capability) {
        this.capability = capability;
    }

    public boolean canSee(ViewCapability compCapability) {
        return capability.ordinal() <= compCapability.ordinal();
    }

    public static enum ViewCapability {

        NONE, //Always
        BASIC, //After BASIC_CRAFT
        MOST, //After CONSTELLATION_CRAFT
        ALL //After TRAIT_CRAFT

    }
}
