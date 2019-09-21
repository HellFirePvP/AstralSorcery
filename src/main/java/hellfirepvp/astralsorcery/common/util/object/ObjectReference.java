/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.object;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ObjectReference
 * Created by HellFirePvP
 * Date: 18.07.2019 / 00:02
 */
public class ObjectReference<T> {

    private T object;

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }
}
