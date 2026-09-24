/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BiasedSuppliers
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class BiasedSuppliers {

    public static <T extends Comparable<T>> Supplier<T> getLowest(int attempts, Supplier<T> supplier) {
        return () -> {
            T value = supplier.get();
            if (attempts <= 0) {
                return value;
            }

            for (int i = 0; i < attempts; i++) {
                T nextValue = supplier.get();
                if (nextValue.compareTo(value) < 0) {
                    value = nextValue;
                }
            }
            return value;
        };
    }

}
