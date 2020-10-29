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
                size *= 1.5F;
                corrupted = true;
            }
            if (trait.equals(ConstellationsAS.vorux)) {
                potency *= 4F;
                effectAmplifier *= 0.8F;
                size *= 2F;
            }
        }
        return this;
    }

}

