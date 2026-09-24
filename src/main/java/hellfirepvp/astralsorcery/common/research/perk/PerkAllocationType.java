/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.perk;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAllocationType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum PerkAllocationType implements StringRepresentable {

    UNLOCKED,
    UNLOCKED_NON_CONNECT,
    GRANTED,
    GRANTED_CONNECT;

    @Override
    public String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public boolean hasConnectivity() {
        return this == UNLOCKED || this == GRANTED_CONNECT;
    }

    public boolean isUnlock() {
        return this == UNLOCKED || this == UNLOCKED_NON_CONNECT;
    }
}
