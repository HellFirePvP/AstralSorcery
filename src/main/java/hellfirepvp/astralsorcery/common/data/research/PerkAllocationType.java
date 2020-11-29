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
 * Class: PerkAllocationType
 * Created by HellFirePvP
 * Date: 29.11.2020 / 18:37
 */
public enum PerkAllocationType {

    UNLOCKED("unlocked"),
    GRANTED("granted");

    private final String saveKey;

    PerkAllocationType(String saveKey) {
        this.saveKey = saveKey;
    }

    public final String getSaveKey() {
        return saveKey;
    }
}
