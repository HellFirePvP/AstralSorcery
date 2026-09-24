/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class TimeUtil {

    public static int seconds(int seconds) {
        return seconds * 20;
    }

    public static int minutes(int minutes) {
        return seconds(minutes * 60);
    }

    public static int defaultInGameDay() {
        return minutes(20);
    }
}
