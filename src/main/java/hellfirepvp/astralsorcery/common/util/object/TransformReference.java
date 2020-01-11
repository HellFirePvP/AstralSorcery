/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.object;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransformReference
 * Created by HellFirePvP
 * Date: 13.10.2019 / 11:00
 */
public class TransformReference<T, R> {

    private final T object;
    private final Function<T, R> transform;

    public TransformReference(T object, Function<T, R> transform) {
        this.object = object;
        this.transform = transform;
    }

    public T getReference() {
        return object;
    }

    public R getValue() {
        return transform.apply(object);
    }

}
