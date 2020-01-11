/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.log;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.LogConfig;

import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogUtil
 * Created by HellFirePvP
 * Date: 06.04.2019 / 13:11
 */
public class LogUtil {

    private static final String PREFIX = "[DEBUG-%s]: %s";

    public static void info(LogCategory category, Supplier<String> msgProvider) {
        if (LogConfig.CONFIG.isLoggingEnabled(category)) {
            AstralSorcery.log.info(String.format(PREFIX, category.name(), msgProvider.get()));
        }
    }
    public static void warn(LogCategory category, Supplier<String> msgProvider) {
        if (LogConfig.CONFIG.isLoggingEnabled(category)) {
            AstralSorcery.log.warn(String.format(PREFIX, category.name(), msgProvider.get()));
        }
    }

}
