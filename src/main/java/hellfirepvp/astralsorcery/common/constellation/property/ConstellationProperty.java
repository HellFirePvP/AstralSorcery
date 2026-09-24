/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.property;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ConstellationProperty
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class ConstellationProperty<T extends ConstellationProperty<T>> {

    private final Key<T> key;
    private final BaseConstellation constellation;

    protected ConstellationProperty(Key<T> key, BaseConstellation constellation) {
        this.key = key;
        this.constellation = constellation;
    }

    public final BaseConstellation getConstellation() {
        return constellation;
    }

    public final Key<T> getKey() {
        return key;
    }

    public static class Key<T extends ConstellationProperty<T>> {}
}
