/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import net.minecraft.util.WeightedRandom;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WRItemObject
 * Created by HellFirePvP
 * Date: 07.05.2016 / 15:20
 */
public class WRItemObject<T> extends WeightedRandom.Item {

    private T object;

    public WRItemObject(int itemWeightIn, T value) {
        super(itemWeightIn);
        this.object = value;
    }

    public T getValue() {
        return object;
    }

}
