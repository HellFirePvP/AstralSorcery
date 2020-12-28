/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PartialEffectExecutor
 * Created by HellFirePvP
 * Date: 13.12.2020 / 18:16
 */
public class PartialEffectExecutor {

    private static final Random random = new Random();

    private final Random rand;
    private final float amount;
    private float currentAmount;

    public PartialEffectExecutor(float amount) {
        this(amount, random);
    }

    public PartialEffectExecutor(float amount, Random rand) {
        this.rand = rand;
        this.amount = amount;
        this.currentAmount = amount;
    }

    public boolean canExecute() {
        return currentAmount > 1 || rand.nextFloat() < currentAmount;
    }

    public void markExecution() {
        currentAmount -= 1F;
    }

    public void reset() {
        this.currentAmount = this.amount;
    }

    public boolean executeAll(Runnable run) {
        boolean ranAtLeastOnce = false;
        while (canExecute()) {
            markExecution();
            run.run();
            ranAtLeastOnce = true;
        }
        return ranAtLeastOnce;
    }
}
