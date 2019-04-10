/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import hellfirepvp.astralsorcery.common.util.WRItemObject;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: NonDuplicateCappedWeightedList
 * Created by HellFirePvP
 * Date: 11.02.2018 / 16:04
 */
public class NonDuplicateCappedWeightedList<T> extends NonDuplicateCappedList<WRItemObject<T>> {

    public NonDuplicateCappedWeightedList(int cap) {
        super(cap);
    }

    @Nullable
    public WRItemObject<T> getRandomElement() {
        return WeightedRandom.getRandomItem(NonDuplicateCappedList.rand, this.elements);
    }

    @Nullable
    public WRItemObject<T> getRandomElementByChance(Random rand, float rngMultiplier) {
        return WeightedRandom.getRandomItem(NonDuplicateCappedList.rand, this.elements);
    }

    @Override
    public boolean removeElement(WRItemObject<T> element) {
        return elements.remove(element) || elements.remove(element.getValue());
    }

}
