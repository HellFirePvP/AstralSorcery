/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.client.resource.query.SpriteSheetQuery;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FlareColor
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
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
    STANDARD(0x9918B9, 0x5E5DD6),
    RAINBOW(0x0, 0x0);

    public final ColorWrapper color1, color2;

    FlareColor(int color1, int color2) {
        this.color1 = ColorWrapper.opaque(color1);
        this.color2 = ColorWrapper.opaque(color2);
    }

    public SpriteSheetQuery getQuery() {
        return new TextureQuery(AssetLocation.EFFECT, "patreon", "flare_" + this.name().toLowerCase(Locale.ROOT))
                .asSpriteSheet(4, 12);
    }
}
