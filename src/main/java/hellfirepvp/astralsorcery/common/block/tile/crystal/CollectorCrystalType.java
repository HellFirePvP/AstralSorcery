/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.tile.crystal;

import hellfirepvp.astralsorcery.common.lib.ColorsAS;
import net.minecraft.block.material.MaterialColor;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CollectorCrystalType
 * Created by HellFirePvP
 * Date: 10.08.2019 / 20:34
 */
public enum CollectorCrystalType {

    ROCK_CRYSTAL(ColorsAS.ROCK_CRYSTAL, MaterialColor.WHITE_TERRACOTTA),
    CELESTIAL_CRYSTAL(ColorsAS.CELESTIAL_CRYSTAL, MaterialColor.CYAN);

    private final Color displayColor;
    private final MaterialColor matColor;

    CollectorCrystalType(Color displayColor, MaterialColor matColor) {
        this.displayColor = displayColor;
        this.matColor = matColor;
    }

    public Color getDisplayColor() {
        return displayColor;
    }

    public MaterialColor getMaterialColor() {
        return matColor;
    }
}
