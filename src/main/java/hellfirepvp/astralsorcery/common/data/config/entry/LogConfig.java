/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.config.entry;

import hellfirepvp.astralsorcery.common.data.config.base.ConfigEntry;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: LogConfig
 * Created by HellFirePvP
 * Date: 20.04.2019 / 20:21
 */
public class LogConfig extends ConfigEntry {

    public static final LogConfig CONFIG = new LogConfig();

    private Map<LogCategory, ForgeConfigSpec.BooleanValue> loggingConfigurations = new HashMap<>();

    private LogConfig() {
        super("logging");
    }

    @Override
    public void createEntries(ForgeConfigSpec.Builder cfgBuilder) {
        for (LogCategory category : LogCategory.values()) {
            ForgeConfigSpec.BooleanValue bValLogging = cfgBuilder
                    .comment("Set to true to enable this logging category. Only do this if you have to debug this section of code! May spam your log HEAVILY!")
                    .translation(translationKey(category.name().toLowerCase()))
                    .define(category.name().toLowerCase(), false);

            loggingConfigurations.put(category, bValLogging);
        }
    }

    public boolean isLoggingEnabled(LogCategory category) {
        ForgeConfigSpec.BooleanValue cfg = this.loggingConfigurations.get(category);
        return cfg == null ? false : cfg.get();
    }

}
