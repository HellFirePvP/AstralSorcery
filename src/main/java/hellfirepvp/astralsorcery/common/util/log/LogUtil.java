/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.log;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.config.entry.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.Provider;
import net.minecraftforge.common.config.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogUtil
 * Created by HellFirePvP
 * Date: 06.04.2019 / 13:11
 */
public class LogUtil {

    private static boolean loggingEnabled = false;
    private static Set<LogCategory> loggedCategories = new HashSet<>();
    private static final String PREFIX = "[DEBUG-%s]: %s";

    public static void info(LogCategory category, Provider<String> msgProvider) {
        if (loggingEnabled && loggedCategories.contains(category)) {
            AstralSorcery.log.info(String.format(PREFIX, category.name(), msgProvider.provide()));
        }
    }
    public static void warn(LogCategory category, Provider<String> msgProvider) {
        if (loggingEnabled && loggedCategories.contains(category)) {
            AstralSorcery.log.warn(String.format(PREFIX, category.name(), msgProvider.provide()));
        }
    }

    public static class CfgEntry extends ConfigEntry {

        public CfgEntry() {
            super(Section.GENERAL, "debug_logging");
        }

        @Override
        public void loadFromConfig(Configuration cfg) {
            loggedCategories.clear();

            for (LogCategory cat : LogCategory.values()) {
                boolean enabled = cfg.getBoolean(cat.name(), getConfigurationSection(), false, "Set to true to enable this logging category. Only do this if you have to debug this section of code! May spam your log HEAVILY!");
                if (enabled) {
                    loggedCategories.add(cat);
                }
            }

            loggingEnabled = !loggedCategories.isEmpty();
        }
    }

}
