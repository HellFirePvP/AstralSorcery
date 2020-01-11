/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.math.MathHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EffectIncrementer
 * Created by HellFirePvP
 * Date: 10.07.2019 / 20:10
 */
public class EffectIncrementer {

    private final int cap;
    private int current = 0;

    public EffectIncrementer(int max) {
        this.cap = max;
    }

    public void update(boolean increment) {
        if (increment) {
            this.current++;
        } else {
            this.current--;
        }
        this.current = MathHelper.clamp(this.current, 0, this.cap);
    }

    public int get() {
        return current;
    }

    public float getAsPercentage() {
        return this.current / ((float) this.cap);
    }

}
