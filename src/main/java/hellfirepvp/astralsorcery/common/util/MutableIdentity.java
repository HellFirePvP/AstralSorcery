/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MutableIdentity
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class MutableIdentity {

    private String identifier;

    protected MutableIdentity(String identifier) {
        this.identifier = identifier;
    }

    public final String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutableIdentity that)) return false;
        return Objects.equals(this.identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier);
    }
}
