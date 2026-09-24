/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.research.perk;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkRemovalResult
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum PerkRemovalResult {

    FAILURE, //Nothing removed
    REMOVE_ALLOCATION, //Removed only 1 allocation source from a specific allocation perkAttributeType
    REMOVE_ALLOCATION_TYPE, //Removed the last allocation from a allocation perkAttributeType
    REMOVE_PERK; //Removed the last allocation and allocation perkAttributeType from a perk

    public boolean isFailure() {
        return this == FAILURE;
    }

    public boolean removesAllocationType() {
        return this == REMOVE_ALLOCATION_TYPE || this.removesPerk();
    }

    public boolean removesPerk() {
        return this == REMOVE_PERK;
    }
}
