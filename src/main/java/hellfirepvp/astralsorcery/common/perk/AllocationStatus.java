/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.client.lib.SpritesAS;
import hellfirepvp.astralsorcery.client.resource.SpriteSheetResource;
import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AllocationStatus
 * Created by HellFirePvP
 * Date: 08.08.2019 / 16:54
 */
public enum AllocationStatus {

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

    @OnlyIn(Dist.CLIENT)
    public SpriteSheetResource getPerkTreeSprite() {
        switch (this) {
            case GRANTED:
            case ALLOCATED:
                return SpritesAS.SPR_PERK_ACTIVE;
            case UNLOCKABLE:
                return SpritesAS.SPR_PERK_ACTIVATEABLE;
            case UNALLOCATED:
            default:
                return SpritesAS.SPR_PERK_INACTIVE;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public SpriteSheetResource getPerkTreeHaloSprite() {
        switch (this) {
            case GRANTED:
            case ALLOCATED:
                return SpritesAS.SPR_PERK_HALO_ACTIVE;
            case UNLOCKABLE:
                return SpritesAS.SPR_PERK_HALO_ACTIVATEABLE;
            case UNALLOCATED:
            default:
                return SpritesAS.SPR_PERK_HALO_INACTIVE;
        }
    }

    public Color getPerkConnectionColor() {
        switch (this) {
            case GRANTED:
            case ALLOCATED:
                return ColorsAS.PERK_CONNECTION_ALLOCATED;
            case UNLOCKABLE:
                return ColorsAS.PERK_CONNECTION_UNLOCKABLE;
            case UNALLOCATED:
            default:
                return ColorsAS.PERK_CONNECTION_UNALLOCATED;
        }
    }

    public Color getPerkColor() {
        switch (this) {
            case GRANTED:
            case ALLOCATED:
                return ColorsAS.PERK_ALLOCATED;
            case UNLOCKABLE:
                return ColorsAS.PERK_UNLOCKABLE;
            case UNALLOCATED:
            default:
                return ColorsAS.PERK_UNALLOCATED;
        }
    }
}
