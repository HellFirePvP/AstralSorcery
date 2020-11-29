/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

import net.minecraft.util.Util;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerPerkAllocation
 * Created by HellFirePvP
 * Date: 29.11.2020 / 18:31
 */
public class PlayerPerkAllocation {

    private static final UUID UNLOCK_UUID = Util.DUMMY_UUID;

    private final PerkAllocationType type;
    private final UUID lockUUID;

    private PlayerPerkAllocation(PerkAllocationType type, UUID lockUUID) {
        this.type = type;
        this.lockUUID = lockUUID;
    }

    public static PlayerPerkAllocation unlock() {
        return new PlayerPerkAllocation(PerkAllocationType.UNLOCKED, UNLOCK_UUID);
    }

    public static PlayerPerkAllocation granted(UUID lockUUID) {
        return new PlayerPerkAllocation(PerkAllocationType.GRANTED, lockUUID);
    }

    final PerkAllocationType getType() {
        return type;
    }

    final UUID getLockUUID() {
        return lockUUID;
    }

}
