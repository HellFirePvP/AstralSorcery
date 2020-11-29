/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.research;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkRemovalResult
 * Created by HellFirePvP
 * Date: 29.11.2020 / 21:02
 */
public enum  PerkRemovalResult {

    FAILURE, //Nothing removed
    REMOVE_ALLOCATION, //Removed only 1 allocation source from a specific allocation type
    REMOVE_ALLOCATION_TYPE, //Removed the last allocation from a allocation type
    REMOVE_PERK; //Removed the last allocation and allocation type from a perk

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
