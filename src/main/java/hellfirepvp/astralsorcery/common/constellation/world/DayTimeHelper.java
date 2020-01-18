/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.world;

import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DayTimeHelper
 * Created by HellFirePvP
 * Date: 01.07.2019 / 07:01
 */
public class DayTimeHelper {

    //Convenience method
    public static float getCurrentDaytimeDistribution(World world) {
        int dLength = GeneralConfig.CONFIG.dayLength.get();
        float dayPart = ((world.getDayTime() % dLength) + dLength) % dLength;
        if (dayPart < (dLength / 2F)) return 0F;
        float part = dLength / 7F;
        if (dayPart < ((dLength / 2F) + part)) return ((dayPart - ((dLength / 2F) + part)) / part) + 1F;
        if (dayPart > (dLength - part)) return 1F - (dayPart - (dLength - part)) / part;
        return 1F;
    }

    public static boolean isNight(World world) {
        return getCurrentDaytimeDistribution(world) >= 0.55;
    }

    public static boolean isDay(World world) {
        return getCurrentDaytimeDistribution(world) <= 0.05;
    }

    //For effect purposes to determine how long those events are/last
    public static int getSolarEclipseHalfDuration() {
        return GeneralConfig.CONFIG.dayLength.get() / 10;
    }

    public static int getLunarEclipseHalfDuration() {
        return GeneralConfig.CONFIG.dayLength.get() / 10;
    }

}
