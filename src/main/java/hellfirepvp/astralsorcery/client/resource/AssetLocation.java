/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.resource;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AssetLocation
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum AssetLocation implements StringRepresentable {

    ITEMS("item"),
    BLOCKS("block"),
    SCREEN("screen"),
    MISC("misc"),
    MODEL("model"),
    EFFECT("effect"),
    ENVIRONMENT("environment"),
    CONSTELLATION("constellation");

    private final String location;

    AssetLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return this.location;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
