/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.lib.ConstellationsAS;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectProperties
 * Created by HellFirePvP
 * Date: 05.06.2019 / 22:18
 */
public class ConstellationEffectProperties {

    private double size;
    private double potency = 1;
    private double effectAmplifier = 1;
    private boolean corrupted = false;
    private double fracturationLower = 1F, fracturationRate = 1F;

    public ConstellationEffectProperties(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public void multiplySize(double multiplier) {
        this.size *= multiplier;
    }

    public double getPotency() {
        return potency;
    }

    public double getEffectAmplifier() {
        return effectAmplifier;
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    public double getFracturationLowerBoundaryMultiplier() {
        return fracturationLower;
    }

    public double getFracturationRate() {
        return fracturationRate;
    }

    public ConstellationEffectProperties modify(IMinorConstellation trait) {
        if (trait != null) {
            if (trait.equals(ConstellationsAS.gelu)) {
                potency *= 1.4F;
                size *= 2F;
            }
            if (trait.equals(ConstellationsAS.ulteria)) {
                effectAmplifier *= 2F;
                size *= 0.4F;
            }
            if (trait.equals(ConstellationsAS.alcara)) {
                fracturationLower *= 0.015F;
                fracturationRate *= 700F;

                size *= 1.5F;
                effectAmplifier *= 2.5F;
                corrupted = true;
            }
            if (trait.equals(ConstellationsAS.vorux)) {
                fracturationLower *= 0.15F;
                fracturationRate *= 400F;

                potency *= 1.2F;
                effectAmplifier *= 5F;
                size *= 2F;
            }
        }
        return this;
    }

}

