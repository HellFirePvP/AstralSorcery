/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import hellfirepvp.astralsorcery.client.resource.AssetLocation;
import hellfirepvp.astralsorcery.client.resource.query.TextureQuery;
import hellfirepvp.astralsorcery.common.config.server.GeneralConfig;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.level.LevelAccessor;

import java.util.Locale;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MoonPhase
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum MoonPhase {

    FULL, WANING_3_4, WANING_1_2, WANING_1_4,
    NEW, WAXING_1_4, WAXING_1_2, WAXING_3_4;

    public static MoonPhase fromWorld(LevelAccessor world) {
        return fromDayTime(world.dayTime());
    }

    public static MoonPhase fromDayTime(long dayTime) {
        return fromDay((int) (dayTime / GeneralConfig.CONFIG.dayLength.get()));
    }

    public static MoonPhase fromDay(long day) {
        return MiscUtil.getEnumEntry(MoonPhase.class, ((int) day % values().length + values().length) % values().length);
    }

    public String getAssetPath() {
        return "moon_%s".formatted(this.name().toLowerCase(Locale.ROOT));
    }

    public TextureQuery getAssetQuery() {
        return new TextureQuery(AssetLocation.ENVIRONMENT, "moon_phases", this.getAssetPath());
    }
}
