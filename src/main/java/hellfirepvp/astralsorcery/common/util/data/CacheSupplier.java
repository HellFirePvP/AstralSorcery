/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CacheSupplier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CacheSupplier<T> implements Supplier<T> {

    private final Supplier<T> objectSupplier;
    private T object = null;

    public CacheSupplier(Supplier<T> objectSupplier) {
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
