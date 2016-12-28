package hellfirepvp.astralsorcery.common.data.research;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: EnumGatedKnowledge
* Created by HellFirePvP
* Date: 01.08.2016 / 22:31
*/
public enum EnumGatedKnowledge {

    WAND_TYPE(ProgressionTier.ATTUNEMENT),

    //Specifically rock and celestial crystal items
    CRYSTAL_SIZE(ProgressionTier.BASIC_CRAFT),
    CRYSTAL_PURITY(ProgressionTier.BASIC_CRAFT),
    CRYSTAL_COLLECT(ProgressionTier.BASIC_CRAFT),
    CRYSTAL_TUNE(ProgressionTier.ATTUNEMENT),
    CRYSTAL_TRAIT(ProgressionTier.TRAIT_CRAFT),

    COLLECTOR_CRYSTAL(ProgressionTier.CONSTELLATION_CRAFT),
    COLLECTOR_TYPE(ProgressionTier.CONSTELLATION_CRAFT);

    //RITUAL_PL_BEACON(ProgressionTier.NETWORKING);

    private final ProgressionTier capability;

    private EnumGatedKnowledge(ProgressionTier capability) {
        this.capability = capability;
    }

    public boolean canSee(ProgressionTier compCapability) {
        return capability.ordinal() <= compCapability.ordinal();
    }

}
