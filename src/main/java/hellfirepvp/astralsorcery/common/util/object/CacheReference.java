/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.object;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CacheReference
 * Created by HellFirePvP
 * Date: 21.09.2019 / 15:53
 */
public class CacheReference<T> implements Supplier<T> {

    private final Supplier<T> objectSupplier;
    private T object = null;

    public CacheReference(Supplier<T> objectSupplier) {
        this.objectSupplier = objectSupplier;
    }

    @Override
    public T get() {
        if (object == null) {
            object = objectSupplier.get();
        }
        return object;
    }
}
