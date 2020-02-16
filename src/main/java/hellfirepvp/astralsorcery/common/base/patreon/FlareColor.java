/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import hellfirepvp.astralsorcery.client.resource.AssetLoader;
import hellfirepvp.astralsorcery.client.resource.query.SpriteQuery;

import java.awt.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: FlareColor
 * Created by HellFirePvP
 * Date: 30.08.2019 / 17:55
 */
public enum FlareColor {

    BLUE(0x157AFF, 0xC1D8FF),
    DARK_RED(0xFF0739, 0xFF5555),
    DAWN(0xFF5186, 0xE95C47),
    GOLD(0xFF9116, 0xFFF26E),
    GREEN(0x5BFF37, 0x63FFA3),
    MAGENTA(0xFC7FFC, 0xFFC6FF),
    RED(0xFF0F2B, 0xFF0F59),
    WHITE(0xBFFFFF, 0xFFFFFF),
    YELLOW(0xFFFF55, 0xFDC71F),
    ELDRITCH(0x620280, 0xAE22FF),
    DARK_GREEN(0x00C601, 0x22FF8F),
    FIRE(0xFF4006, 0xFF9900),
    WATER(0x89DFFF, 0x587ADD),
    EARTH(0xD0863D, 0xCEB392),
    AIR(0xFFFFD1, 0xB2DABD),
    STANDARD(0x9918B9, 0x5E5DD6);

    public final Color color1, color2;

    FlareColor(int c1, int c2) {
        this.color1 = new Color(c1);
        this.color2 = new Color(c2);
    }

    public SpriteQuery getSpriteQuery() {
        return new SpriteQuery(AssetLoader.TextureLocation.EFFECT, 1, 48, "patreonflares", name().toLowerCase());
    }

}
