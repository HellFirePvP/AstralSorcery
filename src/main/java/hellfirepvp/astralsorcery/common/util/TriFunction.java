/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TriFunction
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TriFunction<S, T, U, R> {

    R apply(S s, T t, U u);

    default <V> TriFunction<S, T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (S s, T t, U u) -> after.apply(apply(s, t, u));
    }
}
