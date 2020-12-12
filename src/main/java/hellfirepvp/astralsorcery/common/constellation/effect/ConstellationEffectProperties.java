/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.effect;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationEffectProperties
 * Created by HellFirePvP
 * Date: 05.06.2019 / 22:18
 */
public class ConstellationEffectProperties {

    private double size;
    private float potency = 1;
    private float effectAmplifier = 1;
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

    public float getPotency() {
        return potency;
    }

    public void multiplyPotency(float multiplier) {
        this.potency *= multiplier;
    }

    public float getEffectAmplifier() {
        return effectAmplifier;
    }

    public void multiplyEffectAmplifier(float multiplier) {
        this.effectAmplifier *= multiplier;
    }

    public boolean isCorrupted() {
        return corrupted;
    }

    public void setCorrupted(boolean corrupted) {
        this.corrupted = corrupted;
    }
}

