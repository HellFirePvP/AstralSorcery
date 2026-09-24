/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.level;

import hellfirepvp.astralsorcery.common.config.server.GeneralConfig;
import net.minecraft.world.level.Level;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DayTimeHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DayTimeHelper {

    public static float getCurrentDaytimeDistribution(Level level) {
        if (level == null) return 0F;
        int dLength = GeneralConfig.CONFIG.dayLength.get();
        float dayPart = ((level.getDayTime() % dLength) + dLength) % dLength;
        if (dayPart < (dLength / 2F)) return 0F;
        float part = dLength / 7F;
        if (dayPart < ((dLength / 2F) + part)) return ((dayPart - ((dLength / 2F) + part)) / part) + 1F;
        if (dayPart > (dLength - part)) return 1F - (dayPart - (dLength - part)) / part;
        return 1F;
    }

    public static boolean isNight(Level level) {
        return getCurrentDaytimeDistribution(level) >= 0.55;
    }

    public static boolean isDay(Level level) {
        return getCurrentDaytimeDistribution(level) <= 0.05;
    }
}
