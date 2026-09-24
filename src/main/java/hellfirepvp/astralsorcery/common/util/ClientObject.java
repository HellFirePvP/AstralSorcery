/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ClientObject
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ClientObject<T> {

    private T object;

    public ClientObject() {
        this(null);
    }

    public ClientObject(T object) {
        this.object = object;
    }

    @Nullable
    public T get() {
        return object;
    }

    public void set(@Nullable T object) {
        this.object = object;
    }

    public boolean isNull() {
        return this.object == null;
    }

    public void ifPresent(Consumer<T> consumer) {
        if (this.object != null) {
            consumer.accept(this.object);
        }
    }
}
