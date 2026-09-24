/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.perk;

import net.minecraft.Util;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkAllocation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkAllocation {

    private static final UUID UNLOCK_UUID = Util.NIL_UUID;
    private static final UUID UNLOCK_NON_CONNET_UUID = new UUID(0L, 1L);

    private final PerkAllocationType type;
    private final UUID lockUUID;

    private PerkAllocation(PerkAllocationType type, UUID lockUUID) {
        this.type = type;
        this.lockUUID = lockUUID;
    }

    /**
     * Used for normal allocations via tree.
     * Don't use this unless you need to simulate player actions on the tree directly.
     * For the love of fck you probably won't need this, don't look at this.
     */
    public static PerkAllocation unlock() {
        return new PerkAllocation(PerkAllocationType.UNLOCKED, UNLOCK_UUID);
    }

    /**
     * Used for normal allocations on the tree, but not granting connections from there.
     * Don't use this unless you need to simulate player actions on the tree directly.
     * You don't want to use this, even less so than unlock().
     */
    public static PerkAllocation unlockNoConnections() {
        return new PerkAllocation(PerkAllocationType.UNLOCKED_NON_CONNECT, UNLOCK_NON_CONNET_UUID);
    }

    /**
     * Used for perks granting other perks while maintaining connectability from them.
     * Do not use aside from perks directly.
     * Similarly, i hope you don't need this. Really.
     */
    public static PerkAllocation grantedConnections(UUID lockUUID) {
        return new PerkAllocation(PerkAllocationType.GRANTED_CONNECT, lockUUID);
    }

    /**
     * Used for "things" granting a perk specifically; Comes with a free uuid to use as identifier.
     * Use this for items or other things granting perks freely. You probably want this. Or just ask me how to use things here.
     */
    public static PerkAllocation granted(UUID lockUUID) {
        return new PerkAllocation(PerkAllocationType.GRANTED, lockUUID);
    }

    public PerkAllocationType getType() {
        return this.type;
    }

    public UUID getLockUUID() {
        return this.lockUUID;
    }
}
