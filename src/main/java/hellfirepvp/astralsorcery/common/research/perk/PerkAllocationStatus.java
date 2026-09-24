/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.perk;

import hellfirepvp.astralsorcery.common.lib.constants.ColorsAS;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAllocationStatus
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum PerkAllocationStatus {

    /**
     * Can neither be allocated next nor is allocated currently
     */
    UNALLOCATED,

    /**
     * Is allocated and grants its effect
     */
    ALLOCATED,

    /**
     * Is allocated and grants its effect, though is not currently counted as "allocated/unlocked" in a persistent sense.
     * Cannot be additionally unlocked through normal means
     */
    GRANTED,

    /**
     * Is not allocated, but could be allocated next.
     */
    UNLOCKABLE;

    public boolean isAllocated() {
        return this == ALLOCATED || this == GRANTED;
    }

    public ColorWrapper getPerkTreeConnectionColor() {
        return switch (this) {
            case GRANTED, ALLOCATED -> ColorsAS.PERK_CONNECTION_ALLOCATED;
            case UNLOCKABLE -> ColorsAS.PERK_CONNECTION_UNLOCKABLE;
            case UNALLOCATED -> ColorsAS.PERK_CONNECTION_UNALLOCATED;
        };
    }
}
