/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: GatedKnowledge
* Created by HellFirePvP
* Date: 01.08.2016 / 22:31
*/
public enum GatedKnowledge {

    //Specifically rock and celestial crystal items
    CRYSTAL_SIZE(ProgressionTier.BASIC_CRAFT),
    CRYSTAL_PURITY(ProgressionTier.BASIC_CRAFT),
    CRYSTAL_COLLECT(ProgressionTier.BASIC_CRAFT),
    CRYSTAL_TUNE(ProgressionTier.ATTUNEMENT),
    CRYSTAL_TRAIT(ProgressionTier.TRAIT_CRAFT),
    CRYSTAL_FRACTURE(ProgressionTier.ATTUNEMENT),

    COLLECTOR_CRYSTAL(ProgressionTier.CONSTELLATION_CRAFT),
    COLLECTOR_TYPE(ProgressionTier.CONSTELLATION_CRAFT),

    CONSTELLATION_RITUAL(ProgressionTier.ATTUNEMENT),
    CONSTELLATION_ENCH_POTION(ProgressionTier.CONSTELLATION_CRAFT),

    CONSTELLATION_PAPER_CRAFT(ProgressionTier.TRAIT_CRAFT),
    CONSTELLATION_CAPE(ProgressionTier.TRAIT_CRAFT),
    CONSTELLATION_CORRUPTION(ProgressionTier.TRAIT_CRAFT);

    private final ProgressionTier capability;

    private GatedKnowledge(ProgressionTier capability) {
        this.capability = capability;
    }

    public boolean canSee(PlayerProgress progress) {
        return canSee(progress.getTierReached());
    }

    public boolean canSee(ProgressionTier compCapability) {
        return capability.ordinal() <= compCapability.ordinal();
    }

}
