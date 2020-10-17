/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CalendarUtils
 * Created by HellFirePvP
 * Date: 17.10.2020 / 14:28
 */
public class CalendarUtils {

    public static boolean isAprilFirst() {
        LocalDateTime date = LocalDateTime.now();
        return date.getMonth() == Month.APRIL && date.getDayOfMonth() == 1;
    }
}
