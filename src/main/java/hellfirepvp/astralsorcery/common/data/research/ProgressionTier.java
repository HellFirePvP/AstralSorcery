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
 * Class: ProgressionTier
 * Created by HellFirePvP
 * Date: 01.08.2016 / 22:32
 */
public enum ProgressionTier {

    DISCOVERY,
    BASIC_CRAFT,
    ATTUNEMENT,
    CONSTELLATION_CRAFT,
    TRAIT_CRAFT,
    BRILLIANCE;

    public boolean hasNextTier() {
        return ordinal() < ProgressionTier.values().length - 1;
    }

    public ProgressionTier next() {
        return values()[Math.min(values().length, ordinal() + 1)];
    }

    public boolean isThisLaterOrEqual(ProgressionTier other) {
        return ordinal() >= other.ordinal();
    }

    public boolean isThisLater(ProgressionTier other) {
        return ordinal() > other.ordinal();
    }

}
