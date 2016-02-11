package hellfire.astralSorcery.common.util;

import net.minecraft.util.WeightedRandom;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 11.02.2016 22:35
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
